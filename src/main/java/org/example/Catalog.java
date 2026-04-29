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
                "https://source.unsplash.com/600x400/?basketball,ball,outdoor");

        add("bsk_ball_h","Мяч баскетбольный (зал)","🏀","Натуральная кожа, размер 7",145000,"basketball","pro",20,
                "https://source.unsplash.com/600x400/?basketball,leather,ball,indoor");

        add("bsk_shoes","Кроссовки Nike баскетбол","👟","Высокий верх, амортизация",420000,"basketball","all",15,
                "https://source.unsplash.com/600x400/?basketball,sneakers,nike,shoes");

        add("bsk_jersey","Форма баскетбольная","🏅","Майка + шорты",110000,"basketball","all",30,
                "https://source.unsplash.com/600x400/?basketball,jersey,uniform");

        add("bsk_knee","Наколенники баскетбол","🦵","Компрессионные",45000,"basketball","all",50,
                "https://source.unsplash.com/600x400/?knee,pads,sports,protection");

        add("bsk_dribb","Дриблинг-тренажёр (10 шт)","🎯","Конусы",55000,"basketball","beginner",25,
                "https://source.unsplash.com/600x400/?sport,training,cones,agility");

        add("bsk_net","Сетка для кольца","🥅","Нейлон",25000,"basketball","all",60,
                "https://source.unsplash.com/600x400/?basketball,hoop,net,ring");


        // ⚽ ФУТБОЛ
        add("ftb_ball","Мяч футбольный Adidas","⚽","Размер 5",95000,"football","all",35,
                "https://source.unsplash.com/600x400/?football,soccer,ball,adidas");

        add("ftb_boot_g","Бутсы для травы","👟","Шипы",280000,"football","all",18,
                "https://source.unsplash.com/600x400/?football,boots,cleats,grass");

        add("ftb_boot_h","Бутсы для зала","👟","Футзал",195000,"football","all",22,
                "https://source.unsplash.com/600x400/?futsal,indoor,football,shoes");

        add("ftb_jersey","Форма футбольная","🏅","Комплект",130000,"football","all",40,
                "https://source.unsplash.com/600x400/?football,soccer,jersey,uniform");

        add("ftb_shin","Щитки футбольные","🛡","Защита",35000,"football","all",45,
                "https://source.unsplash.com/600x400/?football,shin,guards,protection");

        add("ftb_gloves","Перчатки вратаря","🧤","Латекс",85000,"football","all",20,
                "https://source.unsplash.com/600x400/?goalkeeper,gloves,football,latex");

        add("ftb_ladder","Лестница координации","🪜","6м",60000,"football","beginner",30,
                "https://source.unsplash.com/600x400/?agility,ladder,training,sport");

        add("ftb_goal","Мини-ворота","🥅","Складные",170000,"football","all",12,
                "https://source.unsplash.com/600x400/?football,mini,goal,portable");


        // 🏐 ВОЛЕЙБОЛ
        add("vb_ball","Мяч волейбольный","🏐","Официальный",80000,"volleyball","all",28,
                "https://source.unsplash.com/600x400/?volleyball,ball");

        add("vb_net_b","Сетка пляжная","🏖","УФ-защита",150000,"volleyball","all",10,
                "https://source.unsplash.com/600x400/?beach,volleyball,net,sand");

        add("vb_net_h","Сетка зальная","🏟","Официальная",200000,"volleyball","pro",8,
                "https://source.unsplash.com/600x400/?volleyball,net,indoor,court");

        add("vb_knee","Наколенники","🦵","Поролон",50000,"volleyball","all",55,
                "https://source.unsplash.com/600x400/?volleyball,knee,pads,protection");

        add("vb_shoes","Кроссовки Asics","👟","Для прыжков",350000,"volleyball","all",14,
                "https://source.unsplash.com/600x400/?volleyball,asics,shoes,sneakers");

        add("vb_pump","Насос","💨","Для мячей",20000,"volleyball","all",70,
                "https://source.unsplash.com/600x400/?ball,pump,inflator,sport");


        // 🥊 БОКС
        add("box_gl_b","Перчатки 10oz","🥊","Начинающим",85000,"boxing","beginner",30,
                "https://source.unsplash.com/600x400/?boxing,gloves,red");

        add("box_gl_p","Перчатки 14oz","🥊","Спарринг",180000,"boxing","pro",15,
                "https://source.unsplash.com/600x400/?boxing,gloves,sparring,pro");

        add("box_bag","Груша","🏋","25кг",320000,"boxing","all",10,
                "https://source.unsplash.com/600x400/?punching,bag,boxing,heavy");

        add("box_wraps","Бинты","🩹","4.5м",25000,"boxing","all",80,
                "https://source.unsplash.com/600x400/?boxing,hand,wraps,bandage");

        add("box_mouth","Капа","😬","Защита зубов",15000,"boxing","all",60,
                "https://source.unsplash.com/600x400/?mouthguard,boxing,teeth,protection");

        add("box_helmet","Шлем","⛑","Защита",120000,"boxing","all",20,
                "https://source.unsplash.com/600x400/?boxing,headgear,helmet,protection");

        add("box_pads","Лапы","🤜","Тренерские",95000,"boxing","all",25,
                "https://source.unsplash.com/600x400/?boxing,focus,pads,mitts,trainer");

        add("box_skip","Скакалка","💫","Скоростная",30000,"boxing","all",45,
                "https://source.unsplash.com/600x400/?jump,rope,speed,boxing,skipping");


        // 🥋 MMA
        add("mma_gl","Перчатки MMA","🥋","4 oz",75000,"mma","all",25,
                "https://source.unsplash.com/600x400/?mma,gloves,grappling");

        add("mma_shorts","Шорты MMA","🩳","Гибкие",65000,"mma","all",35,
                "https://source.unsplash.com/600x400/?mma,fight,shorts");

        add("mma_rash","Рашгард","👕","Компрессионный",90000,"mma","all",28,
                "https://source.unsplash.com/600x400/?rashguard,compression,mma,bjj");

        add("mma_shin","Щитки","🛡","Голень",70000,"mma","all",40,
                "https://source.unsplash.com/600x400/?mma,shin,guards,leg,protection");

        add("mma_bag","Груша напольная","🏋","160 см",480000,"mma","all",6,
                "https://source.unsplash.com/600x400/?freestanding,punching,bag,mma");

        add("mma_rope","Канат","🪢","9м",195000,"mma","pro",8,
                "https://source.unsplash.com/600x400/?battle,rope,gym,training");


        // 🥋 КАРАТЭ
        add("krt_gi_b","Кимоно","🥋","Начинающий",75000,"karate","beginner",20,
                "https://source.unsplash.com/600x400/?karate,gi,kimono,white");

        add("krt_gi_p","Кимоно WKF","🥋","Соревн.",180000,"karate","pro",10,
                "https://source.unsplash.com/600x400/?karate,wkf,gi,competition,uniform");

        add("krt_belt","Пояса","🎽","Набор",45000,"karate","all",30,
                "https://source.unsplash.com/600x400/?karate,belt,colored,martial,arts");

        add("krt_gl","Перчатки","🥊","Кумите",65000,"karate","pro",18,
                "https://source.unsplash.com/600x400/?karate,kumite,gloves");

        add("krt_chest","Нагрудник","🛡","Защита",80000,"karate","all",15,
                "https://source.unsplash.com/600x400/?karate,chest,protector,body,armor");

        add("krt_helmet","Шлем","⛑","Маска",95000,"karate","all",12,
                "https://source.unsplash.com/600x400/?karate,helmet,headgear,face,mask");

        add("krt_mak","Макивара","🎯","Настенная",55000,"karate","all",20,
                "https://source.unsplash.com/600x400/?makiwara,karate,striking,board,wall");

        add("krt_board","Доски","🪵","Разбивание",35000,"karate","all",40,
                "https://source.unsplash.com/600x400/?karate,breaking,wooden,boards");
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