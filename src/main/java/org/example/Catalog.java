package org.example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Каталог товаров. Остатки можно менять через AdminService (поставка).
 * Начальный склад задан последним числом в add(..., stock, imageUrl).
 */
public class Catalog {

    private static final Map<String, Product> BY_ID = new LinkedHashMap<>();

    static {
        // ══════════ 🏀 БАСКЕТБОЛ ══════════
        add("bsk_ball_st","Мяч баскетбольный (улица)","🏀","Резина, размер 7, для асфальта",89_000,"basketball","all",40,"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&q=80");
        add("bsk_ball_h","Мяч баскетбольный (зал)","🏀","Натуральная кожа, размер 7",145_000,"basketball","pro",20,"https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=600&q=80");
        add("bsk_shoes","Кроссовки Nike баскетбол","👟","Высокий верх, амортизация, р.40–47",420_000,"basketball","all",15,"https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80");
        add("bsk_jersey","Форма баскетбольная","🏅","Майка + шорты, дышащий полиэстер",110_000,"basketball","all",30,"https://images.unsplash.com/photo-1519861531473-9200262188bf?w=600&q=80");
        add("bsk_knee","Наколенники баскетбол","🦵","Компрессионные, S/M/L/XL",45_000,"basketball","all",50,"https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600&q=80");
        add("bsk_dribb","Дриблинг-тренажёр (10 шт)","🎯","Конусы для отработки дриблинга",55_000,"basketball","beginner",25,"https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff?w=600&q=80");
        add("bsk_net","Сетка для кольца","🥅","Нейлон, диаметр 45 см",25_000,"basketball","all",60,"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&q=80");

        // ══════════ ⚽ ФУТБОЛ ══════════
        add("ftb_ball","Мяч футбольный Adidas","⚽","Размер 5, синтетическая кожа",95_000,"football","all",35,"https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=600&q=80");
        add("ftb_boot_g","Бутсы для травы (шипы)","👟","Нат. кожа, 12 шипов, р.39–46",280_000,"football","all",18,"https://images.unsplash.com/photo-1511886929837-354d827aae26?w=600&q=80");
        add("ftb_boot_h","Бутсы для зала (футзал)","👟","Резиновая подошва, усиленный носок",195_000,"football","all",22,"https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=600&q=80");
        add("ftb_jersey","Форма футбольная","🏅","Реплика клубов / сборной Узбекистана",130_000,"football","all",40,"https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=600&q=80");
        add("ftb_shin","Щитки футбольные","🛡","Жёсткий пластик + набивка, S/M/L",35_000,"football","all",45,"https://images.unsplash.com/photo-1552667466-07770ae110d0?w=600&q=80");
        add("ftb_gloves","Перчатки вратаря","🧤","Латекс 4 мм, размеры 7–11",85_000,"football","all",20,"https://images.unsplash.com/photo-1606925797300-0b35e9d1794e?w=600&q=80");
        add("ftb_ladder","Лестница координации 6м","🪜","12 ступеней, в сумке",60_000,"football","beginner",30,"https://images.unsplash.com/photo-1599058945522-28d584b6f0ff?w=600&q=80");
        add("ftb_goal","Мини-ворота складные (2шт)","🥅","Сталь + сетка, 120×80 см",170_000,"football","all",12,"https://images.unsplash.com/photo-1551958219-acbc630e2914?w=600&q=80");

        // ══════════ 🏐 ВОЛЕЙБОЛ ══════════
        add("vb_ball","Мяч волейбольный Mikasa","🏐","Официальный размер и вес",80_000,"volleyball","all",28,"https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=600&q=80");
        add("vb_net_b","Сетка пляжного волейбола","🏖","УФ-защита, 8.5×1 м",150_000,"volleyball","all",10,"https://images.unsplash.com/photo-1547941126-3d5322b218b0?w=600&q=80");
        add("vb_net_h","Сетка зальная официальная","🏟","9.5×1 м, регулируемая",200_000,"volleyball","pro",8,"https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=600&q=80");
        add("vb_knee","Наколенники волейбольные","🦵","Толстый поролон, S–XL",50_000,"volleyball","all",55,"https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600&q=80");
        add("vb_shoes","Кроссовки Asics волейбол","👟","Амортизация под прыжки",350_000,"volleyball","all",14,"https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80");
        add("vb_pump","Насос для мячей","💨","Металл, 2 иглы в комплекте",20_000,"volleyball","all",70,"https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff?w=600&q=80");

        // ══════════ 🥊 БОКС ══════════
        add("box_gl_b","Перчатки боксёрские 10oz","🥊","Иск. кожа, для начинающих",85_000,"boxing","beginner",30,"https://images.unsplash.com/photo-1598971457999-ca4ef48a9a71?w=600&q=80");
        add("box_gl_p","Перчатки боксёрские 14oz","🥊","Нат. кожа, для спаррингов",180_000,"boxing","pro",15,"https://images.unsplash.com/photo-1615117972428-28de67cda58e?w=600&q=80");
        add("box_bag","Боксёрская груша 25кг","🏋","Текстильный наполнитель, 70 см",320_000,"boxing","all",10,"https://images.unsplash.com/photo-1549476464-37392f717541?w=600&q=80");
        add("box_wraps","Бинты боксёрские 4.5м","🩹","Эластичные, 2 шт.",25_000,"boxing","all",80,"https://images.unsplash.com/photo-1598971457999-ca4ef48a9a71?w=600&q=80");
        add("box_mouth","Капа боксёрская","😬","Термопластик",15_000,"boxing","all",60,"https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=600&q=80");
        add("box_helmet","Шлем боксёрский","⛑","Кожзам, S/M/L",120_000,"boxing","all",20,"https://images.unsplash.com/photo-1555597673-b21d5c935865?w=600&q=80");
        add("box_pads","Лапы боксёрские (пара)","🤜","Изогнутые, толстая набивка",95_000,"boxing","all",25,"https://images.unsplash.com/photo-1590556409324-aa1d726e5c3c?w=600&q=80");
        add("box_skip","Скакалка скоростная","💫","Стальной трос, подшипники",30_000,"boxing","all",45,"https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=600&q=80");

        // ══════════ 🥋 MMA ══════════
        add("mma_gl","Перчатки MMA открытые","🥋","4 oz, открытые пальцы",75_000,"mma","all",25,"https://images.unsplash.com/photo-1617791160505-6f00504e3519?w=600&q=80");
        add("mma_shorts","Шорты MMA","🩳","Гибкий пояс, разрезы на боках",65_000,"mma","all",35,"https://images.unsplash.com/photo-1555597673-b21d5c935865?w=600&q=80");
        add("mma_rash","Рашгард длинный рукав","👕","Компрессионный, XS–XXL",90_000,"mma","all",28,"https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=600&q=80");
        add("mma_shin","Щитки на голень MMA","🛡","ПВХ + EVA пена",70_000,"mma","all",40,"https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600&q=80");
        add("mma_bag","Груша напольная на пружине","🏋","160 см, регулируемая",480_000,"mma","all",6,"https://images.unsplash.com/photo-1549476464-37392f717541?w=600&q=80");
        add("mma_rope","Боевой канат 9м","🪢","Полипропилен, 38 мм",195_000,"mma","pro",8,"https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=600&q=80");

        // ══════════ 🥋 КАРАТЭ ══════════
        add("krt_gi_b","Кимоно для начинающих","🥋","Хлопок 8oz, с белым поясом",75_000,"karate","beginner",20,"https://images.unsplash.com/photo-1555597408-26bc8e548a46?w=600&q=80");
        add("krt_gi_p","Кимоно WKF (соревн.)","🥋","Одобрено WKF, усиленные швы",180_000,"karate","pro",10,"https://images.unsplash.com/photo-1555597408-26bc8e548a46?w=600&q=80");
        add("krt_belt","Набор поясов (все цвета)","🎽","9 поясов: белый→чёрный",45_000,"karate","all",30,"https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80");
        add("krt_gl","Перчатки кумите WKF","🥊","XS/S/M/L/XL",65_000,"karate","pro",18,"https://images.unsplash.com/photo-1598971457999-ca4ef48a9a71?w=600&q=80");
        add("krt_chest","Защита тела (нагрудник)","🛡","Пластик + подкладка, S/M/L/XL",80_000,"karate","all",15,"https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=600&q=80");
        add("krt_helmet","Шлем каратэ с маской","⛑","Полная защита лица",95_000,"karate","all",12,"https://images.unsplash.com/photo-1555597673-b21d5c935865?w=600&q=80");
        add("krt_mak","Макивара настенная","🎯","Рисовая соломка + холст",55_000,"karate","all",20,"https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff?w=600&q=80");
        add("krt_board","Доски для разбивания 5шт","🪵","Сосна 30×20×1 см",35_000,"karate","all",40,"https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=600&q=80");

        // ══════════ 🎁 НАБОРЫ ══════════
        add("set_box","Набор начинающего боксёра","🎁","Перчатки+бинты+капа+скакалка. −20%!",130_000,"boxing","beginner",15,"https://images.unsplash.com/photo-1615117972428-28de67cda58e?w=600&q=80");
        add("set_mma","Стартовый набор MMA","🎁","Перчатки+шорты+рашгард+щитки",260_000,"mma","beginner",10,"https://images.unsplash.com/photo-1617791160505-6f00504e3519?w=600&q=80");
        add("set_ftb","Набор юного футболиста","🎁","Мяч+форма+щитки+гетры",280_000,"football","beginner",12,"https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=600&q=80");
        add("set_krt","Набор каратека-новичка","🎁","Ги+пояс+перчатки кумите",190_000,"karate","beginner",8,"https://images.unsplash.com/photo-1555597408-26bc8e548a46?w=600&q=80");
    }

    private static void add(String id, String name, String emoji, String desc,
                             double price, String cat, String level,
                             int stock, String img) {
        BY_ID.put(id, new Product(id, name, emoji, desc, price, cat, level, stock, img));
    }

    // ── Методы поиска ─────────────────────────────────────────────────────────

    public static List<Product> getAll() {
        return new ArrayList<>(BY_ID.values());
    }

    public static List<Product> byCategory(String cat) {
        return BY_ID.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(cat))
            .collect(Collectors.toList());
    }

    public static List<Product> getSets() {
        return BY_ID.values().stream()
            .filter(p -> p.getId().startsWith("set_"))
            .collect(Collectors.toList());
    }

    public static Optional<Product> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    /** Обновить остаток товара (вызывается из AdminService) */
    public static boolean addStock(String productId, int qty) {
        Product p = BY_ID.get(productId);
        if (p == null) return false;
        p.increaseStock(qty);
        return true;
    }

    public static boolean setStock(String productId, int qty) {
        Product p = BY_ID.get(productId);
        if (p == null) return false;
        p.setStock(qty);
        return true;
    }

    /** Уменьшить остаток при оформлении заказа */
    public static void decreaseStock(String productId, int qty) {
        Product p = BY_ID.get(productId);
        if (p != null) p.decreaseStock(qty);
    }

    public static Map<String, String> categoryLabels() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("basketball", "🏀 Баскетбол");
        m.put("football",   "⚽ Футбол");
        m.put("volleyball", "🏐 Волейбол");
        m.put("boxing",     "🥊 Бокс");
        m.put("mma",        "🥋 MMA");
        m.put("karate",     "🥋 Каратэ");
        return m;
    }
}
