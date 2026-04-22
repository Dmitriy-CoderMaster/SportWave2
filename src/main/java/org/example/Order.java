package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Order {

    public enum Status {
        NEW("🆕 Новый"), CONFIRMED("✅ Подтверждён"),
        SHIPPED("🚚 Отправлен"), DELIVERED("📦 Доставлен"), CANCELLED("❌ Отменён");
        private final String display;
        Status(String d) { this.display = d; }
        public String getDisplay() { return display; }
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final String        orderId;
    private final long          userId;
    private final String        username;
    private final String        firstName;
    private final List<CartItem>items;
    private final double        totalPrice;
    private final String        phone;
    private final String        address;
    private final String        paymentMethod;
    private final LocalDateTime createdAt;
    private       Status        status;

    public Order(long userId, String username, String firstName,
                 List<CartItem> items, String phone, String address, String paymentMethod) {
        this.orderId       = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.userId        = userId;
        this.username      = username;
        this.firstName     = firstName;
        this.items         = items;
        this.totalPrice    = items.stream().mapToDouble(CartItem::totalPrice).sum();
        this.phone         = phone;
        this.address       = address;
        this.paymentMethod = paymentMethod;
        this.createdAt     = LocalDateTime.now();
        this.status        = Status.NEW;
    }

    // Конструктор для восстановления из БД
    public Order(String orderId, long userId, String username, String firstName,
                 List<CartItem> items, double totalPrice, String phone, String address,
                 String paymentMethod, LocalDateTime createdAt, Status status) {
        this.orderId = orderId; this.userId = userId; this.username = username;
        this.firstName = firstName; this.items = items; this.totalPrice = totalPrice;
        this.phone = phone; this.address = address; this.paymentMethod = paymentMethod;
        this.createdAt = createdAt; this.status = status;
    }

    public String paymentDisplay() {
        return "card".equals(paymentMethod) ? "💳 Перевод на карту" : "💵 Наличные при получении";
    }

    public String toAdminMessage() {
        StringBuilder sb = new StringBuilder(String.format("""
            ╔══════════════════════════════╗
            ║  🛒 НОВЫЙ ЗАКАЗ  #%s
            ╚══════════════════════════════╝
            👤 %s (@%s) | ID: `%d`
            📦 *Товары:*
            """, orderId, firstName, username != null ? username : "—", userId));
        int i = 1;
        for (CartItem c : items)
            sb.append(String.format("  %d. %s %s × %d = *%.0f сум*\n",
                i++, c.getProduct().getEmoji(), c.getProduct().getName(), c.getQuantity(), c.totalPrice()));
        sb.append(String.format("\n💰 *ИТОГО: %.0f сум*\n%s\n📞 %s\n🏠 %s\n🕐 %s",
            totalPrice, paymentDisplay(), phone, address, createdAt.format(FMT)));
        if ("card".equals(paymentMethod)) sb.append("\n\n⚠️ _Клиент выбрал оплату картой!_");
        return sb.toString();
    }

    public String toClientMessage() {
        StringBuilder sb = new StringBuilder(
            String.format("✅ *Заказ принят!*\n\n🎫 `#%s`\n\n", orderId));
        for (CartItem c : items)
            sb.append(String.format("%s %s × %d — *%.0f сум*\n",
                c.getProduct().getEmoji(), c.getProduct().getName(), c.getQuantity(), c.totalPrice()));
        sb.append(String.format("\n💰 *Итого: %.0f сум*\n%s\n📞 %s\n🏠 %s",
            totalPrice, paymentDisplay(), phone, address));
        if ("card".equals(paymentMethod))
            sb.append("\n\n💳 *Карта:* `8600 1234 5678 9012`\n_SportWave Shop_\n_После перевода отправь скриншот менеджеру_");
        else
            sb.append("\n\n_Оплата наличными курьеру_ 📦");
        sb.append("\n\n⏱ Ожидай звонка в течение 30 минут!\nСпасибо за покупку в *SportWave* 🏆");
        return sb.toString();
    }

    public String toLogLine() {
        StringBuilder s = new StringBuilder();
        for (CartItem i : items) s.append(i.getProduct().getName()).append("×").append(i.getQuantity()).append(" ");
        return String.format("ORDER[#%s] user=%d items=[%s] total=%.0f pay=%s phone=%s time=%s",
            orderId, userId, s.toString().trim(), totalPrice, paymentMethod, phone, createdAt.format(FMT));
    }

    public String        getOrderId()       { return orderId; }
    public long          getUserId()        { return userId; }
    public String        getUsername()      { return username; }
    public String        getFirstName()     { return firstName; }
    public List<CartItem>getItems()         { return items; }
    public double        getTotalPrice()    { return totalPrice; }
    public String        getPhone()         { return phone; }
    public String        getAddress()       { return address; }
    public String        getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public Status        getStatus()        { return status; }
    public void          setStatus(Status s){ this.status = s; }
}
