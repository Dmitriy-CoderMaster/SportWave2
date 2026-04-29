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
        add("bsk_ball_st","Мяч баскетбольный (улица)","🏀","Резина, размер 7, для асфальта",89_000,"basketball","all",40,"https://admin.di-sport.uz/storage/galleries/25403/VbsYxyKGdxs1YXGxQJ2MxSSG0r1aOwePLnRCYfaa.webp");
        add("bsk_ball_h","Мяч баскетбольный (зал)","🏀","Натуральная кожа, размер 7",145_000,"basketball","pro",20,"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&q=80");
        add("bsk_shoes","Кроссовки Nike баскетбол","👟","Высокий верх, амортизация, р.40–47",420_000,"basketball","all",15,"https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80");
        add("bsk_jersey","Форма баскетбольная","🏅","Майка + шорты, дышащий полиэстер",110_000,"basketball","all",30,"https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcTxid9gwbIBOmOJPnuvNvwuB3UedM-JbBzLF0Hftl-k7O07AjWyzTotxVtJw1rC9rp8QdxDiGU05TlrDCDlSKV1gl6IsSeMDg8dawySuiEE7HMgyby3X5NFzY6qp8dR_sKgyw&usqp=CAc");
        add("bsk_knee","Наколенники баскетбол","🦵","Компрессионные, S/M/L/XL",45_000,"basketball","all",50,"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9cl5KzFrYHdIM_jjnokAwa5xjhGwldvQlhQ&s");
        add("bsk_dribb","Дриблинг-тренажёр (10 шт)","🎯","Конусы для отработки дриблинга",55_000,"basketball","beginner",25,"data:image/jpeg;base64,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");
        add("bsk_net","Сетка для кольца","🥅","Нейлон, диаметр 45 см",25_000,"basketball","all",60,"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFsqlMru-p86ewbZzu-_Lk7Gzt8325qfmh5A&s");

        // ══════════ ⚽ ФУТБОЛ ══════════
        add("ftb_ball","Мяч футбольный Adidas","⚽","Размер 5, синтетическая кожа",95_000,"football","all",35,"https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=600&q=80");
        add("ftb_boot_g","Бутсы для травы (шипы)","👟","Нат. кожа, 12 шипов, р.39–46",280_000,"football","all",18,"https://images.unsplash.com/photo-1511886929837-354d827aae26?w=600&q=80");
        add("ftb_boot_h","Бутсы для зала (футзал)","👟","Резиновая подошва, усиленный носок",195_000,"football","all",22,"https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=600&q=80");
        add("ftb_jersey","Форма футбольная","🏅","Реплика клубов / сборной Узбекистана",130_000,"football","all",40,"https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=600&q=80");
        add("ftb_shin","Щитки футбольные","🛡","Жёсткий пластик + набивка, S/M/L",35_000,"football","all",45,"https://images.unsplash.com/photo-1552667466-07770ae110d0?w=600&q=80");
        add("ftb_gloves","Перчатки вратаря","🧤","Латекс 4 мм, размеры 7–11",85_000,"football","all",20,"https://images.unsplash.com/photo-1606925797300-0b35e9d1794e?w=600&q=80");
        add("ftb_ladder","Лестница координации 6м","🪜","12 ступеней, в сумке",60_000,"football","beginner",30,"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTyaLjN17z5gzZa-LwAIcLNII2wllrAJCKP6w&s");
        add("ftb_goal","Мини-ворота складные (2шт)","🥅","Сталь + сетка, 120×80 см",170_000,"football","all",12,"https://images.unsplash.com/photo-1551958219-acbc630e2914?w=600&q=80");

        // ══════════ 🏐 ВОЛЕЙБОЛ ══════════
        add("vb_ball","Мяч волейбольный Mikasa","🏐","Официальный размер и вес",80_000,"volleyball","all",28,"https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=600&q=80");
        add("vb_net_b","Сетка пляжного волейбола","🏖","УФ-защита, 8.5×1 м",150_000,"volleyball","all",10,"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQYznSpF5P2Hgqul2hqJDE6ERsoD_1bUDi4sw&s");
        add("vb_net_h","Сетка зальная официальная","🏟","9.5×1 м, регулируемая",200_000,"volleyball","pro",8,"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQznv4G46cYnob02B-57Cd8QyF3WsXGosE9BQ&s");
        add("vb_knee","Наколенники волейбольные","🦵","Толстый поролон, S–XL",50_000,"volleyball","all",55,"https://ir.ozone.ru/s3/multimedia-7/c1000/6267241579.jpg");
        add("vb_shoes","Кроссовки Asics волейбол","👟","Амортизация под прыжки",350_000,"volleyball","all",14,"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyAFJnClGC02NxnGaNS6YT-kO3EJUA5SkOiw&s");
        add("vb_pump","Насос для мячей","💨","Металл, 2 иглы в комплекте",20_000,"volleyball","all",70,"https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcTO3xL3goB5XdLOcdvF6WLykbiSoB9xD0rMcju1qACkgWlyTfvplIGsIT_S1oEGh1T-MVPdWX92MC76cSCpfcVxOv6hF7CFhv84b6UZcBcEUmLfFT62FNJPrFYRXgE7o2WPCAQ&usqp=CAc");

        // ══════════ 🥊 БОКС ══════════
        add("box_gl_b","Перчатки боксёрские 10oz","🥊","Иск. кожа, для начинающих",85000,"boxing","beginner",30,
                "https://www.kindpng.com/picc/m/418-4188644_boxing-gloves-and-hand-wraps-10count-fitness-v6.png");
        add("box_gl_p","Перчатки боксёрские 14oz","🥊","Нат. кожа, для спаррингов",180000,"boxing","pro",15,
                "https://images.unsplash.com/photo-1599058917765-a780eda07a3e");
        add("box_bag","Боксёрская груша 25кг","🏋","Текстильный наполнитель, 70 см",320000,"boxing","all",10,
                "https://images.unsplash.com/photo-1579758629938-03607ccdbaba");
        add("box_wraps","Бинты боксёрские 4.5м","🩹","Эластичные, 2 шт.",25000,"boxing","all",80,
                "https://images.unsplash.com/photo-1605296867304-46d5465a13f1");
        add("box_mouth","Капа боксёрская","😬","Термопластик",15000,"boxing","all",60,
                "https://images.unsplash.com/photo-1628779238951-be2c9f2a59f4");
        add("box_helmet","Шлем боксёрский","⛑","Кожзам, S/M/L",120000,"boxing","all",20,
                "https://images.unsplash.com/photo-1617042375876-a13e36732a04");
        add("box_pads","Лапы боксёрские (пара)","🤜","Изогнутые, толстая набивка",95000,"boxing","all",25,
                "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed");
        add("box_skip","Скакалка скоростная","💫","Стальной трос, подшипники",30000,"boxing","all",45,
                "https://images.unsplash.com/photo-1594737625785-a6cbdabd333c");
        // ══════════ 🥋 MMA ══════════
        add("mma_gl","Перчатки MMA открытые","🥋","4 oz, открытые пальцы",75000,"mma","all",25,
                "https://images.unsplash.com/photo-1605296867724-fa87a8efab3c?w=600&q=80");

        add("mma_shorts","Шорты MMA","🩳","Гибкий пояс, разрезы на боках",65000,"mma","all",35,
                "https://images.unsplash.com/photo-1599058918144-1ffabb6ab9a0?w=600&q=80");

        add("mma_rash","Рашгард длинный рукав","👕","Компрессионный, XS–XXL",90000,"mma","all",28,
                "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=600&q=80");

        add("mma_shin","Щитки на голень MMA","🛡","ПВХ + EVA пена",70000,"mma","all",40,
                "https://images.unsplash.com/photo-1617042375876-a13e36732a04?w=600&q=80");

        add("mma_bag","Груша напольная на пружине","🏋","160 см, регулируемая",480000,"mma","all",6,
                "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=600&q=80");

        add("mma_rope","Боевой канат 9м","🪢","Полипропилен, 38 мм",195000,"mma","pro",8,
                "https://images.unsplash.com/photo-1549060279-7e168fcee0c2?w=600&q=80");

        // ══════════ 🥋 КАРАТЭ ══════════
        add("krt_gi_b","Кимоно для начинающих","🥋","Хлопок 8oz, с белым поясом",75000,"karate","beginner",20,
                "https://images.unsplash.com/photo-1605286834033-8d6a3d1f7c42?w=600&q=80");

        add("krt_gi_p","Кимоно WKF (соревн.)","🥋","Одобрено WKF, усиленные швы",180000,"karate","pro",10,
                "https://images.unsplash.com/photo-1594737625785-cb3b9cfe3f7c?w=600&q=80");

        add("krt_belt","Набор поясов (все цвета)","🎽","9 поясов: белый→чёрный",45000,"karate","all",30,
                "https://images.unsplash.com/photo-1617042375876-a13e36732a04?w=600&q=80");

        add("krt_gl","Перчатки кумите WKF","🥊","XS/S/M/L/XL",65000,"karate","pro",18,
                "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?w=600&q=80");

        add("krt_chest","Защита тела (нагрудник)","🛡","Пластик + подкладка, S/M/L/XL",80000,"karate","all",15,
                "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=600&q=80");

        add("krt_helmet","Шлем каратэ с маской","⛑","Полная защита лица",95000,"karate","all",12,
                "https://images.unsplash.com/photo-1605296867724-fa87a8efab3c?w=600&q=80");

        add("krt_mak","Макивара настенная","🎯","Рисовая соломка + холст",55000,"karate","all",20,
                "https://images.unsplash.com/photo-1549476464-37392f717541?w=600&q=80");

        add("krt_board","Доски для разбивания 5шт","🪵","Сосна 30×20×1 см",35000,"karate","all",40,
                "https://images.unsplash.com/photo-1581091215367-59ab6b4f0c0b?w=600&q=80");

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
