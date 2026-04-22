package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║   🏆 SportWave Bot — Точка входа                     ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * Run → Edit Configurations → Environment Variables:
 *
 * ── Telegram ──────────────────────
 *   BOT_TOKEN     = токен от @BotFather
 *   BOT_USERNAME  = имя бота без @
 *
 * ── PostgreSQL ────────────────────
 *   DB_HOST       = localhost
 *   DB_PORT       = 5432
 *   DB_NAME       = sportwave
 *   DB_USER       = postgres
 *   DB_PASSWORD   = твой_пароль
 *
 * ── Admins (вписать в AdminConfig.java) ──
 *   ADMIN_1..4    = chat_id (узнать через @userinfobot)
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("╔══════════════════════════════════╗");
        log.info("║  🏆 SportWave Bot  STARTING...   ║");
        log.info("╚══════════════════════════════════╝");

        // 1. База данных
        try {
            Database.init();
        } catch (Exception e) {
            log.error("❌ DB init failed: {}", e.getMessage(), e);
            System.exit(1);
        }

        // 2. Сервисы
        OrderService orderService = new OrderService();
        StatsService statsService = new StatsService();

        // 3. Бот
        String token    = env("8638371846:AAF8Hoi3oI-pZ3Gl35Zh4FWQUR0ikUpoHIA",    "8638371846:AAF8Hoi3oI-pZ3Gl35Zh4FWQUR0ikUpoHIA");
        String username = env("SportWave", "SportWave");

        log.info("▶ Bot: @{}", username);
        log.info("▶ Admins: {}", AdminConfig.allAdmins());

        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new Bot(token, username, orderService, statsService));
            log.info("✅ Bot is running!");
        } catch (TelegramApiException e) {
            log.error("❌ Bot start failed: {}", e.getMessage(), e);
            Database.close();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("🛑 Shutting down...");
            Database.close();
        }));
    }

    private static String env(String k, String def) {
        String v = System.getenv(k);
        if (v == null || v.isBlank()) v = System.getProperty(k.toLowerCase().replace("_","."));
        return (v != null && !v.isBlank()) ? v : def;
    }
}