package org.example;

import java.util.Set;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║              КОНФИГУРАЦИЯ АДМИНИСТРАТОРОВ            ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * Впиши сюда chat_id до 4 администраторов.
 * Узнать свой chat_id: напиши боту @userinfobot
 *
 * ADMIN_1 — главный (получает все уведомления о заказах)
 * ADMIN_2, ADMIN_3, ADMIN_4 — дополнительные
 *
 * Если администратор не нужен — оставь 0L
 */
public class AdminConfig {

    // ── Вставь сюда chat_id администраторов ──────────────────────────────────

    public static final long ADMIN_1 = 6688348215L;   // ← впиши свой chat_id
    public static final long ADMIN_2 = 5656532611L;   // ← второй админ
    public static final long ADMIN_3 = 6569272292L;   // ← третий администраторов
   public static final long ADMIN_4 = 1004193078L;   // ← четвёртый админ

    // ─────────────────────────────────────────────────────────────────────────

    private static final Set<Long> ADMIN_IDS = Set.of(
        ADMIN_1, ADMIN_2, ADMIN_3, ADMIN_4
    ).stream().filter(id -> id != 0L)
     .collect(java.util.stream.Collectors.toUnmodifiableSet());

    /** Является ли данный chat_id администратором */
    public static boolean isAdmin(long chatId) {
        return ADMIN_IDS.contains(chatId);
    }

    /** Все активные (не нулевые) chat_id администраторов */
    public static Set<Long> allAdmins() {
        return ADMIN_IDS;
    }

    /** Главный администратор (ADMIN_1) */
    public static long mainAdmin() {
        return ADMIN_1;
    }
}
