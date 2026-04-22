package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Основная логика бота + маршрутизация в AdminService.
 */
public class BotService {

    private final OrderService orderService;
    private final StatsService statsService;
    private final AdminService adminService;
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface PhotoSender { void send(SendPhoto photo); }
    private PhotoSender photoSender;

    public BotService(OrderService orderService, StatsService statsService) {
        this.orderService = orderService;
        this.statsService = statsService;
        this.adminService = new AdminService(orderService, statsService);
    }

    public void setPhotoSender(PhotoSender ps) { this.photoSender = ps; }

    // ══════════════════════════════════════════════════════════════════════════
    //  Текст
    // ══════════════════════════════════════════════════════════════════════════

    public void onText(long chatId, String firstName, String username,
                       String text, Consumer<SendMessage> send) {

        // Записываем визит
        statsService.recordVisit(chatId, username, firstName);

        UserSession s = session(chatId);

        // Состояние поставки (только для админов)
        if (AdminConfig.isAdmin(chatId)) {
            if (adminService.handleText(chatId, text, s, send)) return;
            if (text.equals("/admin") || text.equals("🔐 Админ")) {
                adminService.showAdminMenu(chatId, send); return;
            }
        }

        switch (text) {
            case "/start"                -> { welcome(chatId, firstName, send); return; }
            case "/menu", "🏠 Меню"      -> { mainMenu(chatId, send); return; }
            case "/catalog","🛍 Каталог" -> { catalog(chatId, send); return; }
            case "/sets",  "🎁 Наборы"  -> { showCategory(chatId, "sets", send); return; }
            case "/cart",  "🛒 Корзина" -> { showCart(chatId, s, send); return; }
            case "/orders","📋 Заказы"   -> { myOrders(chatId, send); return; }
            case "/help",  "❓ Помощь"  -> { help(chatId, send); return; }
            case "/about", "ℹ️ О нас"  -> { about(chatId, send); return; }
        }

        switch (s.getState()) {
            case AWAITING_PHONE   -> onPhone(chatId, text, s, send);
            case AWAITING_ADDRESS -> onAddress(chatId, text, s, send);
            default               -> mainMenu(chatId, send);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Callback
    // ══════════════════════════════════════════════════════════════════════════

    public void onCallback(long chatId, String firstName, String username,
                           String data, Consumer<SendMessage> send) {

        UserSession s = session(chatId);

        // Маршрутизация в AdminService
        if (AdminConfig.isAdmin(chatId)) {
            if (data.equals("adm:menu")) { adminService.showAdminMenu(chatId, send); return; }
            if (adminService.handleCallback(chatId, data, s, send)) return;
        }

        // Обычный пользователь
        if      (data.startsWith("cat:"))       showCategory(chatId, data.substring(4), send);
        else if (data.startsWith("p:"))         showProduct(chatId, data.substring(2), send);
        else if (data.startsWith("buy:"))       startBuy(chatId, data.substring(4), s, send);
        else if (data.startsWith("cart_add:"))  cartAdd(chatId, data.substring(9), s, send);
        else if (data.startsWith("cart_del:"))  cartDel(chatId, data.substring(9), s, send);
        else if (data.startsWith("q:"))         onQty(chatId, data.substring(2), s, send);
        else if (data.equals("show_cart"))      showCart(chatId, s, send);
        else if (data.equals("checkout_cart"))  checkoutFromCart(chatId, s, send);
        else if (data.equals("clear_cart"))     clearCart(chatId, s, send);
        else if (data.equals("pay_cash"))       onPayment(chatId, "cash", username, firstName, s, send);
        else if (data.equals("pay_card"))       onPayment(chatId, "card", username, firstName, s, send);
        else if (data.equals("ok"))             finalizeOrder(chatId, username, firstName, s, send);
        else if (data.equals("cancel"))         { s.resetCheckout(); msg(chatId,"❌ Отменено.", send); mainMenu(chatId, send); }
        else if (data.equals("catalog"))        catalog(chatId, send);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  /start
    // ══════════════════════════════════════════════════════════════════════════

    private void welcome(long chatId, String firstName, Consumer<SendMessage> send) {
        msg(chatId, String.format("""
            🏆 *Привет, %s! Добро пожаловать в SportWave!*
            
            Твой магазин спортивных товаров 💪
            🏀⚽🏐🥊🥋
            ━━━━━━━━━━━━━━━━━━
            ✅ 50+ товаров с фото
            ✅ Корзина и выбор оплаты
            ✅ Доставка по всему Узбекистану
            ━━━━━━━━━━━━━━━━━━
            """, firstName), send);
        send.accept(SendMessage.builder().chatId(chatId).text("Выбери раздел:")
            .replyMarkup(mainKeyboard(AdminConfig.isAdmin(chatId))).build());
    }

    private void mainMenu(long chatId, Consumer<SendMessage> send) {
        send.accept(SendMessage.builder().chatId(chatId).text("🏠 *Главное меню*")
            .parseMode("Markdown")
            .replyMarkup(mainKeyboard(AdminConfig.isAdmin(chatId))).build());
    }

    private ReplyKeyboardMarkup mainKeyboard(boolean isAdmin) {
        List<KeyboardRow> rows = new ArrayList<>(Arrays.asList(
            new KeyboardRow(Arrays.asList(new KeyboardButton("🛍 Каталог"),  new KeyboardButton("🎁 Наборы"))),
            new KeyboardRow(Arrays.asList(new KeyboardButton("🛒 Корзина"),  new KeyboardButton("📋 Заказы"))),
            new KeyboardRow(Arrays.asList(new KeyboardButton("❓ Помощь"),   new KeyboardButton("ℹ️ О нас")))
        ));
        if (isAdmin) rows.add(new KeyboardRow(List.of(new KeyboardButton("🔐 Админ"))));
        return ReplyKeyboardMarkup.builder().keyboard(rows).resizeKeyboard(true).build();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Каталог
    // ══════════════════════════════════════════════════════════════════════════

    private void catalog(long chatId, Consumer<SendMessage> send) {
        Map<String, String> cats = Catalog.categoryLabels();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int i = 0;
        for (var e : cats.entrySet()) {
            row.add(ibtn(e.getValue(), "cat:" + e.getKey()));
            if (++i % 2 == 0) { rows.add(new ArrayList<>(row)); row.clear(); }
        }
        if (!row.isEmpty()) rows.add(row);
        rows.add(List.of(ibtn("🎁 Готовые наборы", "cat:sets")));
        rows.add(List.of(ibtn("🛒 Моя корзина", "show_cart")));

        send.accept(SendMessage.builder().chatId(chatId).text("🛍 *КАТАЛОГ*\n\nВыбери вид спорта 👇")
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build()).build());
    }

    private void showCategory(long chatId, String cat, Consumer<SendMessage> send) {
        List<Product> list = "sets".equals(cat) ? Catalog.getSets() : Catalog.byCategory(cat);
        String title = "sets".equals(cat) ? "🎁 *НАБОРЫ*"
            : "*" + Catalog.categoryLabels().getOrDefault(cat, "Товары").toUpperCase() + "*";
        if (list.isEmpty()) { msg(chatId, "😔 Товаров нет.", send); return; }

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Product p : list)
            rows.add(List.of(ibtn(p.toShortLine(), "p:" + p.getId())));
        rows.add(List.of(ibtn("◀️ Каталог", "catalog"), ibtn("🛒 Корзина", "show_cart")));

        send.accept(SendMessage.builder().chatId(chatId)
            .text(title + "\n\n_Нажми на товар — увидишь фото и остаток_")
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build()).build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Карточка товара с фото + остаток
    // ══════════════════════════════════════════════════════════════════════════

    private void showProduct(long chatId, String id, Consumer<SendMessage> send) {
        Optional<Product> opt = Catalog.byId(id);
        if (opt.isEmpty()) { msg(chatId, "❌ Товар не найден.", send); return; }
        Product p = opt.get();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (p.isInStock()) {
            rows.add(Arrays.asList(
                ibtn("🛒 В корзину",       "cart_add:" + p.getId()),
                ibtn("⚡ Заказать сразу",  "buy:"      + p.getId())
            ));
        } else {
            rows.add(List.of(ibtn("👀 Смотреть другие товары", "catalog")));
        }
        rows.add(List.of(ibtn("◀️ Назад", "cat:" + p.getCategory()), ibtn("🛒 Корзина", "show_cart")));

        InlineKeyboardMarkup kbd = InlineKeyboardMarkup.builder().keyboard(rows).build();

        if (p.hasImage() && photoSender != null) {
            try {
                photoSender.send(SendPhoto.builder()
                    .chatId(String.valueOf(chatId))
                    .photo(new InputFile(p.getImageUrl()))
                    .caption(p.toCaption()).parseMode("Markdown")
                    .replyMarkup(kbd).build());
                return;
            } catch (Exception ignored) {}
        }
        send.accept(SendMessage.builder().chatId(chatId).text(p.toCaption()).parseMode("Markdown")
            .replyMarkup(kbd).build());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Корзина
    // ══════════════════════════════════════════════════════════════════════════

    private void showCart(long chatId, UserSession s, Consumer<SendMessage> send) {
        if (s.isCartEmpty()) {
            msg(chatId, "🛒 *Корзина пуста*\n\nПерейди в каталог и добавь товары!", send); return;
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (CartItem item : s.getCart())
            rows.add(List.of(ibtn("❌ Убрать: " + item.getProduct().getEmoji() + " " +
                item.getProduct().getName(), "cart_del:" + item.getProduct().getId())));
        rows.add(Arrays.asList(ibtn("✅ Оформить", "checkout_cart"), ibtn("🗑 Очистить", "clear_cart")));
        rows.add(List.of(ibtn("◀️ Каталог", "catalog")));

        send.accept(SendMessage.builder().chatId(chatId).text(s.cartText()).parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(rows).build()).build());
    }

    private void cartAdd(long chatId, String productId, UserSession s, Consumer<SendMessage> send) {
        Optional<Product> opt = Catalog.byId(productId);
        if (opt.isEmpty()) { msg(chatId, "❌ Товар не найден.", send); return; }
        Product p = opt.get();
        if (!p.isInStock()) { msg(chatId, "❌ Товара нет на складе.", send); return; }
        s.setSelectedProduct(p);
        send.accept(SendMessage.builder().chatId(chatId)
            .text(String.format("🛒 *%s* — выбери количество:\n_(на складе: %d шт.)_",
                p.getName(), p.getStock()))
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(Arrays.asList(
                Arrays.asList(ibtn("1 шт.","q:1"), ibtn("2 шт.","q:2"),
                              ibtn("3 шт.","q:3"), ibtn("5 шт.","q:5")),
                List.of(ibtn("❌ Отмена", "p:" + productId))
            )).build()).build());
    }

    private void onQty(long chatId, String qStr, UserSession s, Consumer<SendMessage> send) {
        Product p = s.getSelectedProduct();
        if (p == null) { msg(chatId, "❌ Выбери товар.", send); return; }
        try {
            int q = Integer.parseInt(qStr);
            if (q > p.getStock()) {
                msg(chatId, String.format("⚠️ На складе только *%d шт.*", p.getStock()), send); return;
            }
            s.addToCart(p, q);
            s.setSelectedProduct(null);
            msg(chatId, String.format("✅ %s *%s* × %d добавлен!\n💰 Корзина: *%.0f сум*",
                p.getEmoji(), p.getName(), q, s.cartTotal()), send);
            send.accept(SendMessage.builder().chatId(chatId).text("Что дальше?")
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(Arrays.asList(
                    Arrays.asList(ibtn("🛒 Корзина","show_cart"), ibtn("📦 Оформить","checkout_cart")),
                    List.of(ibtn("🛍 Продолжить покупки","catalog"))
                )).build()).build());
        } catch (NumberFormatException e) { msg(chatId, "⚠️ Введи число.", send); }
    }

    private void cartDel(long chatId, String productId, UserSession s, Consumer<SendMessage> send) {
        s.removeFromCart(productId);
        msg(chatId, "✅ Убрано из корзины.", send);
        showCart(chatId, s, send);
    }

    private void clearCart(long chatId, UserSession s, Consumer<SendMessage> send) {
        s.clearCart(); msg(chatId, "🗑 Корзина очищена.", send);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Быстрый заказ
    // ══════════════════════════════════════════════════════════════════════════

    private void startBuy(long chatId, String id, UserSession s, Consumer<SendMessage> send) {
        Optional<Product> opt = Catalog.byId(id);
        if (opt.isEmpty()) { msg(chatId, "❌ Не найден.", send); return; }
        Product p = opt.get();
        if (!p.isInStock()) { msg(chatId, "❌ Нет на складе.", send); return; }
        s.setSelectedProduct(p); s.setCheckoutFromCart(false);
        send.accept(SendMessage.builder().chatId(chatId)
            .text(String.format("⚡ *%s*\n💰 %.0f сум/шт.\n📊 На складе: %d шт.\n\nКоличество:",
                p.getName(), p.getPrice(), p.getStock()))
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(Arrays.asList(
                Arrays.asList(ibtn("1 шт.","q:1"),ibtn("2 шт.","q:2"),
                              ibtn("3 шт.","q:3"),ibtn("5 шт.","q:5")),
                List.of(ibtn("❌ Отмена","cancel"))
            )).build()).build());
    }

    private void checkoutFromCart(long chatId, UserSession s, Consumer<SendMessage> send) {
        if (s.isCartEmpty()) { msg(chatId, "🛒 Корзина пуста!", send); return; }
        s.setCheckoutFromCart(true);
        s.setState(UserSession.State.AWAITING_PHONE);
        send.accept(SendMessage.builder().chatId(chatId)
            .text(s.cartText() + "\n\n📞 *Введи номер телефона:*")
            .parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(ibtn("❌ Отмена","cancel")))).build()).build());
    }

    private void onPhone(long chatId, String text, UserSession s, Consumer<SendMessage> send) {
        if (text.trim().length() < 7) { msg(chatId,"⚠️ Слишком короткий номер.",send); return; }
        s.setPhone(text.trim()); s.setState(UserSession.State.AWAITING_ADDRESS);
        msg(chatId, "✅ Номер сохранён!\n\n🏠 *Введи адрес доставки:*\n_Пример: Ташкент, ул. Навои 15, кв. 42_", send);
    }

    private void onAddress(long chatId, String text, UserSession s, Consumer<SendMessage> send) {
        if (text.trim().length() < 5) { msg(chatId,"⚠️ Адрес слишком короткий.",send); return; }
        s.setAddress(text.trim()); s.setState(UserSession.State.CHOOSING_PAYMENT);
        send.accept(SendMessage.builder().chatId(chatId).text("""
            ✅ Адрес сохранён!
            
            💳 *Выбери способ оплаты:*
            """).parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(Arrays.asList(
                Arrays.asList(ibtn("💵 Наличные при получении","pay_cash"),
                              ibtn("💳 Перевод на карту","pay_card")),
                List.of(ibtn("❌ Отмена","cancel"))
            )).build()).build());
    }

    private void onPayment(long chatId, String method, String username, String firstName,
                           UserSession s, Consumer<SendMessage> send) {
        s.setPaymentMethod(method); s.setState(UserSession.State.CONFIRMING);
        List<CartItem> items = buildItems(s);
        double total = items.stream().mapToDouble(CartItem::totalPrice).sum();
        String payStr = "card".equals(method) ? "💳 Перевод на карту" : "💵 Наличные";

        StringBuilder sb = new StringBuilder("📋 *Проверь заказ*\n══════════════════\n\n");
        for (CartItem item : items) sb.append(item.toLine()).append("\n\n");
        sb.append(String.format("━━━━━━━━━━━━━━━━━━\n💰 *%.0f сум*\n📞 `%s`\n🏠 %s\n%s",
            total, s.getPhone(), s.getAddress(), payStr));
        if ("card".equals(method))
            sb.append("\n\n💳 *Карта:* `8600 1234 5678 9012` _(SportWave Shop)_");

        send.accept(SendMessage.builder().chatId(chatId).text(sb.toString()).parseMode("Markdown")
            .replyMarkup(InlineKeyboardMarkup.builder().keyboard(List.of(
                Arrays.asList(ibtn("✅ Подтвердить","ok"), ibtn("❌ Отмена","cancel"))
            )).build()).build());
    }

    private void finalizeOrder(long chatId, String username, String firstName,
                               UserSession s, Consumer<SendMessage> send) {
        List<CartItem> items = buildItems(s);
        if (items.isEmpty()) { msg(chatId,"❌ Нет товаров.",send); s.resetAll(); return; }

        // Уменьшить остатки на складе
        for (CartItem item : items)
            Catalog.decreaseStock(item.getProduct().getId(), item.getQuantity());

        Order order = new Order(chatId, username, firstName, items,
                                s.getPhone(), s.getAddress(), s.getPaymentMethod());
        orderService.save(order);
        statsService.recordOrder();
        s.resetAll();

        // Клиенту
        send.accept(SendMessage.builder().chatId(chatId).text(order.toClientMessage())
            .parseMode("Markdown").replyMarkup(mainKeyboard(false)).build());

        // Всем администраторам
        adminService.notifyAllAdmins(order.toAdminMessage(), send);

        // Предупреждение о низком остатке — тоже всем админам
        for (CartItem item : items) {
            Product p = Catalog.byId(item.getProduct().getId()).orElse(null);
            if (p == null) continue;
            if (p.getStock() == 0) {
                adminService.notifyAllAdmins(String.format(
                    "⚠️ *СКЛАД: Товар закончился!*\n%s *%s*\nОстаток: *0 шт.*\nТребуется поставка!",
                    p.getEmoji(), p.getName()), send);
            } else if (p.isLowStock()) {
                adminService.notifyAllAdmins(String.format(
                    "⚡ *СКЛАД: Мало товара!*\n%s *%s*\nОстаток: *%d шт.*",
                    p.getEmoji(), p.getName(), p.getStock()), send);
            }
        }
    }

    private List<CartItem> buildItems(UserSession s) {
        if (s.isCheckoutFromCart()) return new ArrayList<>(s.getCart());
        if (s.getSelectedProduct() == null) return List.of();
        return List.of(new CartItem(s.getSelectedProduct(), s.getQuantity()));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Прочее
    // ══════════════════════════════════════════════════════════════════════════

    private void myOrders(long chatId, Consumer<SendMessage> send) {
        List<Order> list = orderService.forUser(chatId);
        if (list.isEmpty()) { msg(chatId,"📋 Заказов нет. Перейди в 🛍 Каталог!",send); return; }
        StringBuilder sb = new StringBuilder("📋 *ТВОИ ЗАКАЗЫ*\n\n");
        for (Order o : list)
            sb.append(String.format("🎫 `#%s` — *%.0f сум* — %s — %s\n\n",
                o.getOrderId(), o.getTotalPrice(), o.paymentDisplay(), o.getStatus().getDisplay()));
        msg(chatId, sb.toString(), send);
    }

    private void help(long chatId, Consumer<SendMessage> send) {
        msg(chatId, """
            ❓ *ПОМОЩЬ*
            ─────────────────────
            1. 🛍 Каталог → выбери спорт
            2. Нажми товар → увидишь *фото и остаток*
            3. *В корзину* или *Заказать сразу*
            4. Введи телефон → адрес → оплата
            5. Подтверди ✅
            
            💳 Карта: `8600 1234 5678 9012`
            📞 Менеджер: @SportWaveManager
            🚚 Доставка 1–3 дня
            """, send);
    }

    private void about(long chatId, Consumer<SendMessage> send) {
        msg(chatId, """
            ℹ️ *О SportWave*
            🏆 Магазин спортивных товаров
            📍 Ташкент, Узбекистан
            ✅ 50+ товаров | 6 видов спорта
            ✅ Фото каждого товара
            ✅ Доставка по Узбекистану
            """, send);
    }

    private void msg(long chatId, String text, Consumer<SendMessage> send) {
        send.accept(SendMessage.builder().chatId(chatId).text(text).parseMode("Markdown").build());
    }
    private InlineKeyboardButton ibtn(String l, String cb) {
        return InlineKeyboardButton.builder().text(l).callbackData(cb).build();
    }
    private UserSession session(long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new UserSession());
    }
}
