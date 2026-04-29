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
                "https://images.unsplash.com/photo-1546519638-68e109498ffc?w=600&q=80");

        add("bsk_ball_h","Мяч баскетбольный (зал)","🏀","Натуральная кожа, размер 7",145000,"basketball","pro",20,
                "https://images.unsplash.com/photo-1519861531473-9200262188bf?w=600&q=80");

        add("bsk_shoes","Кроссовки Nike баскетбол","👟","Высокий верх, амортизация",420000,"basketball","all",15,
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&q=80");

        // basketball jersey uniform
        add("bsk_jersey","Форма баскетбольная","🏅","Майка + шорты",110000,"basketball","all",30,
                "https://images.unsplash.com/photo-1515523110800-9415d13b84a8?w=600&q=80");

        // knee pads compression sports
        add("bsk_knee","Наколенники баскетбол","🦵","Компрессионные",45000,"basketball","all",50,
                "https://images.unsplash.com/photo-1620188526357-a670cdf98fe8?w=600&q=80");

        // sport training cones dribbling
        add("bsk_dribb","Дриблинг-тренажёр (10 шт)","🎯","Конусы",55000,"basketball","beginner",25,
                "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=600&q=80");

        // basketball hoop net nylon
        add("bsk_net","Сетка для кольца","🥅","Нейлон",25000,"basketball","all",60,
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80");


        // ⚽ ФУТБОЛ
        add("ftb_ball","Мяч футбольный Adidas","⚽","Размер 5",95000,"football","all",35,
                "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=600&q=80");

        // football boots cleats grass
        add("ftb_boot_g","Бутсы для травы","👟","Шипы",280000,"football","all",18,
                "https://images.unsplash.com/photo-1511886929837-354d827aae26?w=600&q=80");

        // futsal indoor football shoes
        add("ftb_boot_h","Бутсы для зала","👟","Футзал",195000,"football","all",22,
                "https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=600&q=80");

        // football soccer jersey
        add("ftb_jersey","Форма футбольная","🏅","Комплект",130000,"football","all",40,
                "https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=600&q=80");

        // football shin guards
        add("ftb_shin","Щитки футбольные","🛡","Защита",35000,"football","all",45,
                "https://images.unsplash.com/photo-1610810023555-e4d77f41c10e?w=600&q=80");

        // goalkeeper gloves latex
        add("ftb_gloves","Перчатки вратаря","🧤","Латекс",85000,"football","all",20,
                "https://images.unsplash.com/photo-1606925797300-0b35e9d1794e?w=600&q=80");

        // agility ladder training
        add("ftb_ladder","Лестница координации","🪜","6м",60000,"football","beginner",30,
                "https://images.unsplash.com/photo-1599058918144-1ffabb6ab9a0?w=600&q=80");

        // mini portable football goal
        add("ftb_goal","Мини-ворота","🥅","Складные",170000,"football","all",12,
                "https://images.unsplash.com/photo-1551958219-acbc630e2914?w=600&q=80");


        // 🏐 ВОЛЕЙБОЛ
        // volleyball ball
        add("vb_ball","Мяч волейбольный","🏐","Официальный",80000,"volleyball","all",28,
                "https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=600&q=80");

        // beach volleyball net sand
        add("vb_net_b","Сетка пляжная","🏖","УФ-защита",150000,"volleyball","all",10,
                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=600&q=80");

        // indoor volleyball net court
        add("vb_net_h","Сетка зальная","🏟","Официальная",200000,"volleyball","pro",8,
                "https://images.unsplash.com/photo-1521412644187-c49fa049e84d?w=600&q=80");

        // volleyball knee pads
        add("vb_knee","Наколенники","🦵","Поролон",50000,"volleyball","all",55,
                "https://images.unsplash.com/photo-1620188526357-a670cdf98fe8?w=600&q=80");

        // asics volleyball shoes
        add("vb_shoes","Кроссовки Asics","👟","Для прыжков",350000,"volleyball","all",14,
                "https://images.unsplash.com/photo-1556048219-bb6978360b84?w=600&q=80");

        // ball pump inflator
        add("vb_pump","Насос","💨","Для мячей",20000,"volleyball","all",70,
                "https://images.unsplash.com/photo-1588612568467-a6b2458b3c6c?w=600&q=80");


        // 🥊 БОКС
        // boxing gloves beginner
        add("box_gl_b","Перчатки 10oz","🥊","Начинающим",85000,"boxing","beginner",30,
                "https://images.unsplash.com/photo-1599058917765-a780eda07a3e?w=600&q=80");

        // boxing gloves sparring pro
        add("box_gl_p","Перчатки 14oz","🥊","Спарринг",180000,"boxing","pro",15,
                "https://images.unsplash.com/photo-1517438476312-10d79c077509?w=600&q=80");

        // heavy punching bag boxing
        add("box_bag","Груша","🏋","25кг",320000,"boxing","all",10,
                "https://images.unsplash.com/photo-1579758629938-03607ccdbaba?w=600&q=80");

        // boxing hand wraps bandage
        add("box_wraps","Бинты","🩹","4.5м",25000,"boxing","all",80,
                "https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=600&q=80");

        // mouthguard boxing
        add("box_mouth","Капа","😬","Защита зубов",15000,"boxing","all",60,
                "https://images.unsplash.com/photo-1628779238951-be2c9f2a59f4?w=600&q=80");

        // boxing headgear helmet
        add("box_helmet","Шлем","⛑","Защита",120000,"boxing","all",20,
                "https://images.unsplash.com/photo-1617042375876-a13e36732a04?w=600&q=80");

        // boxing focus pads mitts
        add("box_pads","Лапы","🤜","Тренерские",95000,"boxing","all",25,
                "https://images.unsplash.com/photo-1591491653056-4e9e4b9d9b8b?w=600&q=80");

        // speed jump rope boxing
        add("box_skip","Скакалка","💫","Скоростная",30000,"boxing","all",45,
                "https://images.unsplash.com/photo-1598632596899-c7e4f8bf3a5b?w=600&q=80");


        // 🥋 MMA
        // mma grappling gloves 4oz
        add("mma_gl","Перчатки MMA","🥋","4 oz",75000,"mma","all",25,
                "https://images.unsplash.com/photo-1605296867724-fa87a8efab3c?w=600&q=80");

        // mma fight shorts
        add("mma_shorts","Шорты MMA","🩳","Гибкие",65000,"mma","all",35,
                "https://images.unsplash.com/photo-1599058918144-1ffabb6ab9a0?w=600&q=80");

        // rashguard compression bjj mma
        add("mma_rash","Рашгард","👕","Компрессионный",90000,"mma","all",28,
                "https://images.unsplash.com/photo-1571731956672-f2b94d7dd0cb?w=600&q=80");

        // mma shin guards leg protection
        add("mma_shin","Щитки","🛡","Голень",70000,"mma","all",40,
                "https://images.unsplash.com/photo-1610810023555-e4d77f41c10e?w=600&q=80");

        // freestanding punching bag tall
        add("mma_bag","Груша напольная","🏋","160 см",480000,"mma","all",6,
                "https://images.unsplash.com/photo-1579758629938-03607ccdbaba?w=600&q=80");

        // battle rope gym training
        add("mma_rope","Канат","🪢","9м",195000,"mma","pro",8,
                "https://images.unsplash.com/photo-1549060279-7e168fcee0c2?w=600&q=80");


        // 🥋 КАРАТЭ
        // karate gi white beginner
        add("krt_gi_b","Кимоно","🥋","Начинающий",75000,"karate","beginner",20,
                "https://images.unsplash.com/photo-1555597673-b21d5c935865?w=600&q=80");

        // wkf karate gi competition
        add("krt_gi_p","Кимоно WKF","🥋","Соревн.",180000,"karate","pro",10,
                "https://images.unsplash.com/photo-1594737625785-cb3b9cfe3f7c?w=600&q=80");

        // karate belt colored ranks set
        add("krt_belt","Пояса","🎽","Набор",45000,"karate","all",30,
                "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=600&q=80");

        // karate kumite gloves red blue
        add("krt_gl","Перчатки","🥊","Кумите",65000,"karate","pro",18,
                "https://images.unsplash.com/photo-1517438476312-10d79c077509?w=600&q=80");

        // chest protector body armor karate
        add("krt_chest","Нагрудник","🛡","Защита",80000,"karate","all",15,
                "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=600&q=80");

        // karate helmet face mask
        add("krt_helmet","Шлем","⛑","Маска",95000,"karate","all",12,
                "https://images.unsplash.com/photo-1617042375876-a13e36732a04?w=600&q=80");

        // makiwara wall striking board
        add("krt_mak","Макивара","🎯","Настенная",55000,"karate","all",20,
                "https://images.unsplash.com/photo-1504439468489-c8920d796a29?w=600&q=80");

        // wooden breaking boards karate
        add("krt_board","Доски","🪵","Разбивание",35000,"karate","all",40,
                "https://images.unsplash.com/photo-1581091215367-59ab6b4f0c0b?w=600&q=80");
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