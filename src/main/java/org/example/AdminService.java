package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════╗
 * ║              АДМИН-ПАНЕЛЬ SportWave                  ║
 * ╚══════════════════════════════════════════════════════╝
 *
 * Доступна только администраторам из AdminConfig.
 *
 * Возможности:
 *   📊 Статистика пользователей и заказов
 *   📦 Управление складом (остатки)
 *   🚚 Поставка (добавить товар на склад)
 *   🛒 Последние заказы
 *   💰 Выручка
 *   📋 Список товаров с остатками
 */
public class AdminService {

    private final OrderService orderService;
    private final StatsService statsService;

    public AdminService(OrderService orderService, StatsService statsService) {
        this.orderService = orderService;
        this.statsService = statsService;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Главное меню админа
    // ══════════════════════════════════════════════════════════════════════════

    public void showAdminMenu(long chatId, Consumer<SendMessage> send) {
        String text = """
            🔐 *АДМИН-ПАНЕЛЬ SportWave*
            ══════════════════════════
            
            Выбери раздел 👇
            """;

        List<List<InlineKeyboardButton>> rows = Arrays.asList(
            Arrays.asList(
                ibtn("📊 Статистика",    "adm:stats"),
                ibtn("🛒 Заказы",        "adm:orders")
            ),
            Arrays.asList(
                ibtn("📦 Склад",         "adm:stock"),
                ibtn("🚚 Поставка",      "adm:supply")
            ),
            Arrays.asList(
                ibtn("💰 Выручка",       "adm:revenue"),
                ibtn("👥 Пользователи",  "adm:users")
            )
        );

        send.accept(SendMessage.builder()
            .chatId(chatId).text(text).parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
            .build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Обработка callback для админа
    // ══════════════════════════════════════════════════════════════════════════

    public boolean handleCallback(long chatId, String data,
                                   UserSession session, Consumer<SendMessage> send) {
        if (!data.startsWith("adm:") && !data.startsWith("supply:")) return false;

        if      (data.equals("adm:stats"))   showStats(chatId, send);
        else if (data.equals("adm:orders"))  showRecentOrders(chatId, send);
        else if (data.equals("adm:stock"))   showStock(chatId, send);
        else if (data.equals("adm:supply"))  showSupplyCategories(chatId, send);
        else if (data.equals("adm:revenue")) showRevenue(chatId, send);
        else if (data.equals("adm:users"))   showUsers(chatId, send);
        else if (data.startsWith("adm:stock_cat:")) {
            showStockByCategory(chatId, data.substring(14), send);
        }
        else if (data.startsWith("supply:cat:")) {
            showSupplyProducts(chatId, data.substring(11), send);
        }
        else if (data.startsWith("supply:prod:")) {
            String productId = data.substring(12);
            session.setPendingSupplyProductId(productId);
            session.setState(UserSession.State.ADMIN_SUPPLY_ENTER_QTY);
            Optional<Product> p = Catalog.byId(productId);
            String name = p.map(Product::getName).orElse(productId);
            int cur = p.map(Product::getStock).orElse(0);
            msg(chatId, String.format("""
                🚚 *Поставка товара*
                📦 %s
                📊 Сейчас на складе: *%d шт.*
                
                Введи количество пришедшего товара:
                """, name, cur), send);
        }
        else if (data.startsWith("adm:setstatus:")) {
            // adm:setstatus:ORDER_ID:STATUS
            String[] parts = data.substring(14).split(":");
            if (parts.length == 2) {
                String orderId = parts[0];
                try {
                    Order.Status ns = Order.Status.valueOf(parts[1]);
                    boolean ok = orderService.updateStatus(orderId, ns);
                    msg(chatId, ok
                        ? "✅ Статус заказа #" + orderId + " обновлён: " + ns.getDisplay()
                        : "❌ Заказ не найден.", send);
                } catch (Exception e) {
                    msg(chatId, "❌ Неверный статус.", send);
                }
            }
        }

        return true;
    }

    /** Обработка текстового ввода от админа (количество поставки) */
    public boolean handleText(long chatId, String text, UserSession session,
                               Consumer<SendMessage> send) {
        if (session.getState() != UserSession.State.ADMIN_SUPPLY_ENTER_QTY) return false;

        String productId = session.getPendingSupplyProductId();
        session.setState(UserSession.State.IDLE);
        session.setPendingSupplyProductId(null);

        try {
            int qty = Integer.parseInt(text.trim());
            if (qty <= 0) { msg(chatId, "⚠️ Количество должно быть больше 0.", send); return true; }

            boolean ok = Catalog.addStock(productId, qty);
            if (ok) {
                int newStock = Catalog.byId(productId).map(Product::getStock).orElse(0);
                msg(chatId, String.format("""
                    ✅ *Поставка принята!*
                    
                    📦 %s
                    ➕ Добавлено: *%d шт.*
                    📊 Итого на складе: *%d шт.*
                    """,
                    Catalog.byId(productId).map(Product::getName).orElse(productId),
                    qty, newStock), send);
            } else {
                msg(chatId, "❌ Товар не найден.", send);
            }
        } catch (NumberFormatException e) {
            msg(chatId, "⚠️ Введи число (например: 50)", send);
        }
        return true;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  📊 Статистика
    // ══════════════════════════════════════════════════════════════════════════

    private void showStats(long chatId, Consumer<SendMessage> send) {
        String report = statsService.buildStatsReport(orderService);
        send.accept(SendMessage.builder()
            .chatId(chatId).text(report).parseMode("Markdown")
            .replyMarkup(backToAdminKbd())
            .build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  🛒 Последние заказы
    // ══════════════════════════════════════════════════════════════════════════

    private void showRecentOrders(long chatId, Consumer<SendMessage> send) {
        List<Order> orders = orderService.getRecent(10);
        if (orders.isEmpty()) { msg(chatId, "📭 Заказов пока нет.", send); return; }

        StringBuilder sb = new StringBuilder("🛒 *ПОСЛЕДНИЕ 10 ЗАКАЗОВ*\n\n");
        for (Order o : orders) {
            sb.append(String.format(
                "🎫 `#%s` — *%.0f сум* — %s\n👤 %s | %s\n\n",
                o.getOrderId(), o.getTotalPrice(), o.getStatus().getDisplay(),
                o.getFirstName(), o.paymentDisplay()
            ));
        }

        // Кнопки смены статуса для последнего заказа
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (!orders.isEmpty()) {
            String oid = orders.get(0).getOrderId();
            rows.add(Arrays.asList(
                ibtn("✅ Подтвердить",  "adm:setstatus:" + oid + ":CONFIRMED"),
                ibtn("🚚 Отправлен",    "adm:setstatus:" + oid + ":SHIPPED")
            ));
            rows.add(Arrays.asList(
                ibtn("📦 Доставлен",   "adm:setstatus:" + oid + ":DELIVERED"),
                ibtn("❌ Отменить",     "adm:setstatus:" + oid + ":CANCELLED")
            ));
        }
        rows.add(List.of(ibtn("◀️ Назад", "adm:menu")));

        send.accept(SendMessage.builder()
            .chatId(chatId).text(sb.toString()).parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
            .build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  📦 Склад — выбор категории
    // ══════════════════════════════════════════════════════════════════════════

    private void showStock(long chatId, Consumer<SendMessage> send) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Map.Entry<String, String> e : Catalog.categoryLabels().entrySet()) {
            rows.add(List.of(ibtn(e.getValue(), "adm:stock_cat:" + e.getKey())));
        }
        rows.add(List.of(ibtn("🎁 Наборы", "adm:stock_cat:sets")));
        rows.add(List.of(ibtn("◀️ Назад", "adm:menu")));

        send.accept(SendMessage.builder()
            .chatId(chatId)
            .text("📦 *СКЛАД* — выбери категорию:")
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
            .build());
    }

    private void showStockByCategory(long chatId, String cat, Consumer<SendMessage> send) {
        List<Product> products = "sets".equals(cat) ? Catalog.getSets() : Catalog.byCategory(cat);
        if (products.isEmpty()) { msg(chatId, "Товаров нет.", send); return; }

        StringBuilder sb = new StringBuilder("📦 *ОСТАТКИ НА СКЛАДЕ*\n\n");
        for (Product p : products) {
            String status;
            if (p.getStock() == 0)        status = "❌ 0 шт.";
            else if (p.isLowStock())      status = "⚡ " + p.getStock() + " шт. (МАЛО!)";
            else                          status = "✅ " + p.getStock() + " шт.";
            sb.append(String.format("%s *%s*\n   %s\n\n", p.getEmoji(), p.getName(), status));
        }

        send.accept(SendMessage.builder()
            .chatId(chatId).text(sb.toString()).parseMode("Markdown")
            .replyMarkup(backToAdminKbd())
            .build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  🚚 Поставка — шаг 1: выбор категории
    // ══════════════════════════════════════════════════════════════════════════

    private void showSupplyCategories(long chatId, Consumer<SendMessage> send) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Map.Entry<String, String> e : Catalog.categoryLabels().entrySet()) {
            rows.add(List.of(ibtn(e.getValue(), "supply:cat:" + e.getKey())));
        }
        rows.add(List.of(ibtn("🎁 Наборы", "supply:cat:sets")));
        rows.add(List.of(ibtn("◀️ Назад", "adm:menu")));

        send.accept(SendMessage.builder()
            .chatId(chatId)
            .text("🚚 *ПОСТАВКА*\n\nВыбери категорию товара:")
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
            .build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  🚚 Поставка — шаг 2: выбор товара
    // ══════════════════════════════════════════════════════════════════════════

    private void showSupplyProducts(long chatId, String cat, Consumer<SendMessage> send) {
        List<Product> products = "sets".equals(cat) ? Catalog.getSets() : Catalog.byCategory(cat);
        if (products.isEmpty()) { msg(chatId, "Товаров нет.", send); return; }

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Product p : products) {
            String label = p.getEmoji() + " " + p.getName() + " [" + p.getStock() + " шт.]";
            rows.add(List.of(ibtn(label, "supply:prod:" + p.getId())));
        }
        rows.add(List.of(ibtn("◀️ Назад", "adm:supply")));

        send.accept(SendMessage.builder()
            .chatId(chatId)
            .text("🚚 *Выбери товар для поставки:*\n_В скобках — текущий остаток_")
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build())
            .build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  💰 Выручка
    // ══════════════════════════════════════════════════════════════════════════

    private void showRevenue(long chatId, Consumer<SendMessage> send) {
        double total = orderService.totalRevenue();
        int    count = orderService.totalCount();

        String text = String.format("""
            💰 *ВЫРУЧКА SportWave*
            ══════════════════════
            
            🛒 Всего заказов: *%d*
            💵 Общая выручка: *%.0f сум*
            📊 Средний чек:   *%.0f сум*
            """,
            count, total, count > 0 ? total / count : 0);

        send.accept(SendMessage.builder()
            .chatId(chatId).text(text).parseMode("Markdown")
            .replyMarkup(backToAdminKbd()).build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  👥 Пользователи
    // ══════════════════════════════════════════════════════════════════════════

    private void showUsers(long chatId, Consumer<SendMessage> send) {
        String report = statsService.buildStatsReport(orderService);
        send.accept(SendMessage.builder()
            .chatId(chatId).text(report).parseMode("Markdown")
            .replyMarkup(backToAdminKbd()).build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ══════════════════════════════════════════════════════════════════════════

    /** Уведомить всех администраторов о новом заказе */
    public void notifyAllAdmins(String message, Consumer<SendMessage> send) {
        for (long adminId : AdminConfig.allAdmins()) {
            send.accept(SendMessage.builder()
                .chatId(adminId).text(message).parseMode("Markdown").build());
        }
    }

    private InlineKeyboardMarkup backToAdminKbd() {
        return InlineKeyboardMarkup.builder()
            .keyboard(List.of(List.of(ibtn("◀️ В меню админа", "adm:menu"))))
            .build();
    }

    private void msg(long chatId, String text, Consumer<SendMessage> send) {
        send.accept(SendMessage.builder().chatId(chatId).text(text).parseMode("Markdown").build());
    }

    private InlineKeyboardButton ibtn(String label, String cb) {
        return InlineKeyboardButton.builder().text(label).callbackData(cb).build();
    }
}
