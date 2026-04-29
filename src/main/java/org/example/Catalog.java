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
        add("bsk_ball_st","Мяч баскетбольный (улица)","🏀","Резина, размер 7",89000,"basketball","all",40,
                "https://images.pexels.com/photos/358042/pexels-photo-358042.jpeg?w=600&h=400&fit=crop");

        add("bsk_ball_h","Мяч баскетбольный (зал)","🏀","Натуральная кожа, размер 7",145000,"basketball","pro",20,
                "https://images.pexels.com/photos/1752757/pexels-photo-1752757.jpeg?w=600&h=400&fit=crop");

        add("bsk_shoes","Кроссовки Nike баскетбол","👟","Высокий верх, амортизация",420000,"basketball","all",15,
                "https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?w=600&h=400&fit=crop");

        add("bsk_jersey","Форма баскетбольная","🏅","Майка + шорты",110000,"basketball","all",30,
                "https://images.pexels.com/photos/3895734/pexels-photo-3895734.jpeg?w=600&h=400&fit=crop");

        add("bsk_knee","Наколенники баскетбол","🦵","Компрессионные",45000,"basketball","all",50,
                "https://images.pexels.com/photos/4162488/pexels-photo-4162488.jpeg?w=600&h=400&fit=crop");

        add("bsk_dribb","Дриблинг-тренажёр (10 шт)","🎯","Конусы",55000,"basketball","beginner",25,
                "https://images.pexels.com/photos/3621185/pexels-photo-3621185.jpeg?w=600&h=400&fit=crop");

        add("bsk_net","Сетка для кольца","🥅","Нейлон",25000,"basketball","all",60,
                "https://images.pexels.com/photos/1040482/pexels-photo-1040482.jpeg?w=600&h=400&fit=crop");


        // ⚽ ФУТБОЛ
        add("ftb_ball","Мяч футбольный Adidas","⚽","Размер 5",95000,"football","all",35,
                "https://images.pexels.com/photos/47730/the-ball-stadion-football-the-pitch-47730.jpeg?w=600&h=400&fit=crop");

        add("ftb_boot_g","Бутсы для травы","👟","Шипы",280000,"football","all",18,
                "https://images.pexels.com/photos/1618200/pexels-photo-1618200.jpeg?w=600&h=400&fit=crop");

        add("ftb_boot_h","Бутсы для зала","👟","Футзал",195000,"football","all",22,
                "https://images.pexels.com/photos/2220972/pexels-photo-2220972.jpeg?w=600&h=400&fit=crop");

        add("ftb_jersey","Форма футбольная","🏅","Комплект",130000,"football","all",40,
                "https://images.pexels.com/photos/3621184/pexels-photo-3621184.jpeg?w=600&h=400&fit=crop");

        add("ftb_shin","Щитки футбольные","🛡","Защита",35000,"football","all",45,
                "https://images.pexels.com/photos/3886060/pexels-photo-3886060.jpeg?w=600&h=400&fit=crop");

        add("ftb_gloves","Перчатки вратаря","🧤","Латекс",85000,"football","all",20,
                "https://images.pexels.com/photos/3886069/pexels-photo-3886069.jpeg?w=600&h=400&fit=crop");

        add("ftb_ladder","Лестница координации","🪜","6м",60000,"football","beginner",30,
                "https://images.pexels.com/photos/4162579/pexels-photo-4162579.jpeg?w=600&h=400&fit=crop");

        add("ftb_goal","Мини-ворота","🥅","Складные",170000,"football","all",12,
                "https://images.pexels.com/photos/274422/pexels-photo-274422.jpeg?w=600&h=400&fit=crop");


        // 🏐 ВОЛЕЙБОЛ
        add("vb_ball","Мяч волейбольный","🏐","Официальный",80000,"volleyball","all",28,
                "https://images.pexels.com/photos/1263426/pexels-photo-1263426.jpeg?w=600&h=400&fit=crop");

        add("vb_net_b","Сетка пляжная","🏖","УФ-защита",150000,"volleyball","all",10,
                "https://images.pexels.com/photos/1263349/pexels-photo-1263349.jpeg?w=600&h=400&fit=crop");

        add("vb_net_h","Сетка зальная","🏟","Официальная",200000,"volleyball","pro",8,
                "https://images.pexels.com/photos/1619860/pexels-photo-1619860.jpeg?w=600&h=400&fit=crop");

        add("vb_knee","Наколенники","🦵","Поролон",50000,"volleyball","all",55,
                "https://images.pexels.com/photos/4162488/pexels-photo-4162488.jpeg?w=600&h=400&fit=crop");

        add("vb_shoes","Кроссовки Asics","👟","Для прыжков",350000,"volleyball","all",14,
                "https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?w=600&h=400&fit=crop");

        add("vb_pump","Насос","💨","Для мячей",20000,"volleyball","all",70,
                "https://images.pexels.com/photos/3812945/pexels-photo-3812945.jpeg?w=600&h=400&fit=crop");


        // 🥊 БОКС
        add("box_gl_b","Перчатки 10oz","🥊","Начинающим",85000,"boxing","beginner",30,
                "https://images.pexels.com/photos/3763873/pexels-photo-3763873.jpeg?w=600&h=400&fit=crop");

        add("box_gl_p","Перчатки 14oz","🥊","Спарринг",180000,"boxing","pro",15,
                "https://images.pexels.com/photos/4428291/pexels-photo-4428291.jpeg?w=600&h=400&fit=crop");

        add("box_bag","Груша","🏋","25кг",320000,"boxing","all",10,
                "https://images.pexels.com/photos/3763872/pexels-photo-3763872.jpeg?w=600&h=400&fit=crop");

        add("box_wraps","Бинты","🩹","4.5м",25000,"boxing","all",80,
                "https://images.pexels.com/photos/4428277/pexels-photo-4428277.jpeg?w=600&h=400&fit=crop");

        add("box_mouth","Капа","😬","Защита зубов",15000,"boxing","all",60,
                "https://images.pexels.com/photos/4761779/pexels-photo-4761779.jpeg?w=600&h=400&fit=crop");

        add("box_helmet","Шлем","⛑","Защита",120000,"boxing","all",20,
                "https://images.pexels.com/photos/4761663/pexels-photo-4761663.jpeg?w=600&h=400&fit=crop");

        add("box_pads","Лапы","🤜","Тренерские",95000,"boxing","all",25,
                "https://images.pexels.com/photos/4428290/pexels-photo-4428290.jpeg?w=600&h=400&fit=crop");

        add("box_skip","Скакалка","💫","Скоростная",30000,"boxing","all",45,
                "https://images.pexels.com/photos/3766211/pexels-photo-3766211.jpeg?w=600&h=400&fit=crop");


        // 🥋 MMA
        add("mma_gl","Перчатки MMA","🥋","4 oz",75000,"mma","all",25,
                "https://images.pexels.com/photos/4761792/pexels-photo-4761792.jpeg?w=600&h=400&fit=crop");

        add("mma_shorts","Шорты MMA","🩳","Гибкие",65000,"mma","all",35,
                "https://images.pexels.com/photos/4761672/pexels-photo-4761672.jpeg?w=600&h=400&fit=crop");

        add("mma_rash","Рашгард","👕","Компрессионный",90000,"mma","all",28,
                "https://images.pexels.com/photos/4162451/pexels-photo-4162451.jpeg?w=600&h=400&fit=crop");

        add("mma_shin","Щитки","🛡","Голень",70000,"mma","all",40,
                "https://images.pexels.com/photos/4761796/pexels-photo-4761796.jpeg?w=600&h=400&fit=crop");

        add("mma_bag","Груша напольная","🏋","160 см",480000,"mma","all",6,
                "https://images.pexels.com/photos/3763871/pexels-photo-3763871.jpeg?w=600&h=400&fit=crop");

        add("mma_rope","Канат","🪢","9м",195000,"mma","pro",8,
                "https://images.pexels.com/photos/1552106/pexels-photo-1552106.jpeg?w=600&h=400&fit=crop");


        // 🥋 КАРАТЭ
        add("krt_gi_b","Кимоно","🥋","Начинающий",75000,"karate","beginner",20,
                "https://images.pexels.com/photos/8612091/pexels-photo-8612091.jpeg?w=600&h=400&fit=crop");

        add("krt_gi_p","Кимоно WKF","🥋","Соревн.",180000,"karate","pro",10,
                "https://images.pexels.com/photos/8612090/pexels-photo-8612090.jpeg?w=600&h=400&fit=crop");

        add("krt_belt","Пояса","🎽","Набор",45000,"karate","all",30,
                "https://images.pexels.com/photos/8612097/pexels-photo-8612097.jpeg?w=600&h=400&fit=crop");

        add("krt_gl","Перчатки","🥊","Кумите",65000,"karate","pro",18,
                "https://images.pexels.com/photos/4428291/pexels-photo-4428291.jpeg?w=600&h=400&fit=crop");

        add("krt_chest","Нагрудник","🛡","Защита",80000,"karate","all",15,
                "https://images.pexels.com/photos/8612089/pexels-photo-8612089.jpeg?w=600&h=400&fit=crop");

        add("krt_helmet","Шлем","⛑","Маска",95000,"karate","all",12,
                "https://images.pexels.com/photos/4761663/pexels-photo-4761663.jpeg?w=600&h=400&fit=crop");

        add("krt_mak","Макивара","🎯","Настенная",55000,"karate","all",20,
                "https://images.pexels.com/photos/8612092/pexels-photo-8612092.jpeg?w=600&h=400&fit=crop");

        add("krt_board","Доски","🪵","Разбивание",35000,"karate","all",40,
                "https://images.pexels.com/photos/8612094/pexels-photo-8612094.jpeg?w=600&h=400&fit=crop");
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