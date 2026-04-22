package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

/**
 * Статистика бота: уникальные пользователи, визиты, активность.
 * Хранится в PostgreSQL таблице bot_users.
 */
public class StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsService.class);

    // Кэш в памяти для быстрой проверки (очищается при перезапуске)
    private final Set<Long> seenToday = ConcurrentHashMap.newKeySet();

    // ══════════════════════════════════════════════════════════════════════════
    //  Создание таблиц (вызывается из Database.init)
    // ══════════════════════════════════════════════════════════════════════════

    public static void createTables() {
        try (Connection conn = Database.getConnection();
             Statement  stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS bot_users (
                    user_id     BIGINT PRIMARY KEY,
                    username    VARCHAR(100),
                    first_name  VARCHAR(100),
                    first_seen  TIMESTAMP NOT NULL DEFAULT NOW(),
                    last_seen   TIMESTAMP NOT NULL DEFAULT NOW(),
                    visit_count INT       NOT NULL DEFAULT 1
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS daily_stats (
                    stat_date   DATE PRIMARY KEY,
                    new_users   INT NOT NULL DEFAULT 0,
                    active_users INT NOT NULL DEFAULT 0,
                    orders_count INT NOT NULL DEFAULT 0
                )
                """);

            log.info("✅ Stats tables ready");
        } catch (SQLException e) {
            log.error("❌ Stats tables creation failed: {}", e.getMessage(), e);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Регистрация визита пользователя
    // ══════════════════════════════════════════════════════════════════════════

    public void recordVisit(long userId, String username, String firstName) {
        try (Connection conn = Database.getConnection()) {

            // Upsert пользователя
            try (PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO bot_users (user_id, username, first_name, first_seen, last_seen, visit_count)
                VALUES (?, ?, ?, NOW(), NOW(), 1)
                ON CONFLICT (user_id) DO UPDATE
                  SET last_seen   = NOW(),
                      visit_count = bot_users.visit_count + 1,
                      username    = EXCLUDED.username,
                      first_name  = EXCLUDED.first_name
                """)) {
                ps.setLong(1,   userId);
                ps.setString(2, username);
                ps.setString(3, firstName);
                ps.executeUpdate();
            }

            // Обновить daily_stats
            boolean isNewToday = seenToday.add(userId);
            try (PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO daily_stats (stat_date, new_users, active_users)
                VALUES (CURRENT_DATE, ?, 1)
                ON CONFLICT (stat_date) DO UPDATE
                  SET new_users    = daily_stats.new_users    + EXCLUDED.new_users,
                      active_users = daily_stats.active_users + 1
                """)) {
                ps.setInt(1, isNewToday ? 1 : 0);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            log.error("❌ recordVisit failed: {}", e.getMessage());
        }
    }

    /** Отметить оформленный заказ в дневной статистике */
    public void recordOrder() {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO daily_stats (stat_date, orders_count)
                VALUES (CURRENT_DATE, 1)
                ON CONFLICT (stat_date) DO UPDATE
                  SET orders_count = daily_stats.orders_count + 1
                """)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("❌ recordOrder stats failed: {}", e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Получение статистики для админ-панели
    // ══════════════════════════════════════════════════════════════════════════

    public String buildStatsReport(OrderService orderService) {
        StringBuilder sb = new StringBuilder("📊 *СТАТИСТИКА БОТА*\n\n");

        try (Connection conn = Database.getConnection()) {

            // Всего пользователей
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM bot_users")) {
                if (rs.next()) sb.append(String.format("👥 Всего пользователей: *%d*\n", rs.getInt(1)));
            }

            // Новые сегодня
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bot_users WHERE first_seen::date = CURRENT_DATE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sb.append(String.format("🆕 Новых сегодня: *%d*\n", rs.getInt(1)));
                }
            }

            // Активных сегодня
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bot_users WHERE last_seen::date = CURRENT_DATE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sb.append(String.format("🟢 Активных сегодня: *%d*\n", rs.getInt(1)));
                }
            }

            // Активных за 7 дней
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM bot_users WHERE last_seen >= NOW() - INTERVAL '7 days'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sb.append(String.format("📅 За 7 дней: *%d*\n", rs.getInt(1)));
                }
            }

        } catch (SQLException e) {
            sb.append("⚠️ Ошибка получения данных из БД\n");
            log.error("❌ Stats query failed: {}", e.getMessage());
        }

        // Заказы
        int totalOrders = orderService.totalCount();
        sb.append(String.format("\n🛒 Всего заказов: *%d*\n", totalOrders));

        // Последние 7 дней по дням
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                SELECT stat_date, new_users, active_users, orders_count
                FROM daily_stats
                WHERE stat_date >= CURRENT_DATE - 6
                ORDER BY stat_date DESC
                """)) {
            try (ResultSet rs = ps.executeQuery()) {
                sb.append("\n📈 *По дням (последние 7):*\n");
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM");
                while (rs.next()) {
                    LocalDate date = rs.getDate("stat_date").toLocalDate();
                    int newU   = rs.getInt("new_users");
                    int active = rs.getInt("active_users");
                    int ord    = rs.getInt("orders_count");
                    sb.append(String.format("  %s — 👥%d новых, 🟢%d акт., 🛒%d зак.\n",
                        date.format(fmt), newU, active, ord));
                }
            }
        } catch (SQLException e) {
            log.error("❌ Daily stats query failed: {}", e.getMessage());
        }

        return sb.toString();
    }
}
