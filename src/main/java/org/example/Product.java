package org.example;

/**
 * Модель товара с остатком на складе.
 * stock — количество единиц на складе (обновляется через админ-панель).
 */
public class Product {

    // Пороги для предупреждений
    public static final int LOW_STOCK_THRESHOLD = 5;  // "Осталось мало!"

    private final String  id;
    private final String  name;
    private final String  emoji;
    private final String  description;
    private final double  price;
    private final String  category;
    private final String  level;
    private final String  imageUrl;

    // Изменяемые поля
    private int     stock;      // остаток на складе
    private boolean inStock;    // вычисляется из stock

    public Product(String id, String name, String emoji, String description,
                   double price, String category, String level,
                   int stock, String imageUrl) {
        this.id          = id;
        this.name        = name;
        this.emoji       = emoji;
        this.description = description;
        this.price       = price;
        this.category    = category;
        this.level       = level;
        this.imageUrl    = imageUrl;
        this.stock       = stock;
        this.inStock     = stock > 0;
    }

    // ── Управление остатком ───────────────────────────────────────────────────

    public synchronized void decreaseStock(int qty) {
        stock = Math.max(0, stock - qty);
        inStock = stock > 0;
    }

    public synchronized void increaseStock(int qty) {
        stock += qty;
        inStock = stock > 0;
    }

    public synchronized void setStock(int qty) {
        stock = Math.max(0, qty);
        inStock = stock > 0;
    }

    /** Мало товара (> 0 но ≤ порога) */
    public boolean isLowStock() {
        return stock > 0 && stock <= LOW_STOCK_THRESHOLD;
    }

    // ── Тексты для пользователей ──────────────────────────────────────────────

    /** Статусная строка склада для покупателя */
    public String stockStatusForUser() {
        if (stock == 0) {
            return "❌ *Нет в наличии*\n_К сожалению, данного товара нет на складе. Не переживайте — наши менеджеры скоро пополнят запасы! А пока можете посмотреть другие товары_ 👇";
        } else if (isLowStock()) {
            return String.format("⚡ *Поторопитесь! Осталось всего %d шт.!*", stock);
        } else {
            return String.format("✅ В наличии: *%d шт.*", stock);
        }
    }

    /** Подпись под фото для покупателя */
    public String toCaption() {
        String lvl = switch (level) {
            case "beginner" -> "🟢 Новичок";
            case "pro"      -> "🔴 Профи";
            default         -> "🔵 Любой уровень";
        };
        return String.format("""
            %s *%s*
            ─────────────────────
            📋 %s
            💰 Цена: *%.0f сум*
            📦 %s
            🎯 Уровень: %s
            ─────────────────────
            %s
            """,
            emoji, name, description, price,
            stockStatusForUser(), lvl,
            stock > 0
                ? "Нажми *В корзину* или *Заказать сразу*!"
                : "👀 Смотри другие товары в каталоге"
        );
    }

    /** Строка в списке товаров */
    public String toShortLine() {
        String stockMark = stock == 0 ? " ❌" : (isLowStock() ? " ⚡" + stock + "шт" : "");
        return emoji + " " + name + " — " + String.format("%.0f", price) + " сум" + stockMark;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public String  getId()          { return id; }
    public String  getName()        { return name; }
    public String  getEmoji()       { return emoji; }
    public String  getDescription() { return description; }
    public double  getPrice()       { return price; }
    public String  getCategory()    { return category; }
    public String  getLevel()       { return level; }
    public int     getStock()       { return stock; }
    public boolean isInStock()      { return inStock; }
    public String  getImageUrl()    { return imageUrl; }
    public boolean hasImage()       { return imageUrl != null && !imageUrl.isBlank(); }
}
