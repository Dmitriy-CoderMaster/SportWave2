package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PostgreSQL + HikariCP пул соединений.
 *
 * Настройка в IntelliJ IDEA:
 * Run → Edit Configurations → Environment Variables:
 *
 *   DB_HOST      = localhost
 *   DB_PORT      = 5432
 *   DB_NAME      = sportwave
 *   DB_USER      = postgres
 *   DB_PASSWORD  = твой_пароль
 *
 * Создать БД: CREATE DATABASE sportwave;
 * Таблицы создаются автоматически!
 */
public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static HikariDataSource pool;

    public static void init() {
        String host     = env("DB_HOST",     "localhost");
        String port     = env("DB_PORT",     "5432");
        String dbName   = env("DB_NAME",     "sportwave");
        String user     = env("DB_USER",     "postgres");
        String password = env("DB_PASSWORD", "root");

        log.info("📦 Connecting to PostgreSQL: {}@{}:{}/{}", user, host, port, dbName);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName));
        cfg.setUsername(user);
        cfg.setPassword(password);
        cfg.setDriverClassName("org.postgresql.Driver");
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(2);
        cfg.setConnectionTimeout(30_000);
        cfg.setPoolName("SportWavePool");
        cfg.setConnectionTestQuery("SELECT 1");

        pool = new HikariDataSource(cfg);
        log.info("✅ DB pool created");

        createTables();
        StatsService.createTables();
    }

    public static Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    public static void close() {
        if (pool != null && !pool.isClosed()) { pool.close(); log.info("🔌 DB pool closed"); }
    }

    private static void createTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id             SERIAL PRIMARY KEY,
                    order_id       VARCHAR(20)   NOT NULL UNIQUE,
                    user_id        BIGINT        NOT NULL,
                    username       VARCHAR(100),
                    first_name     VARCHAR(100),
                    phone          VARCHAR(30)   NOT NULL,
                    address        TEXT          NOT NULL,
                    payment_method VARCHAR(10)   NOT NULL DEFAULT 'cash',
                    total_price    NUMERIC(12,2) NOT NULL,
                    status         VARCHAR(20)   NOT NULL DEFAULT 'NEW',
                    created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
                )
                """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id            SERIAL PRIMARY KEY,
                    order_id      VARCHAR(20)   NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                    product_id    VARCHAR(50)   NOT NULL,
                    product_name  VARCHAR(200)  NOT NULL,
                    product_emoji VARCHAR(10),
                    quantity      INT           NOT NULL,
                    price         NUMERIC(12,2) NOT NULL,
                    total         NUMERIC(12,2) NOT NULL
                )
                """);

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_time ON orders(created_at DESC)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_items_order ON order_items(order_id)");

            log.info("✅ DB schema ready");
        } catch (SQLException e) {
            log.error("❌ createTables failed: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static String env(String key, String def) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) v = System.getProperty(key.toLowerCase().replace("_", "."));
        return (v != null && !v.isBlank()) ? v : def;
    }
}
