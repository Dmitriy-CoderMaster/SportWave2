package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public void save(Order o) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO orders (order_id,user_id,username,first_name,phone,address,payment_method,total_price,status,created_at)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """)) {
                ps.setString(1, o.getOrderId()); ps.setLong(2, o.getUserId());
                ps.setString(3, o.getUsername()); ps.setString(4, o.getFirstName());
                ps.setString(5, o.getPhone()); ps.setString(6, o.getAddress());
                ps.setString(7, o.getPaymentMethod()); ps.setDouble(8, o.getTotalPrice());
                ps.setString(9, o.getStatus().name());
                ps.setTimestamp(10, Timestamp.valueOf(o.getCreatedAt()));
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("""
                INSERT INTO order_items (order_id,product_id,product_name,product_emoji,quantity,price,total)
                VALUES (?,?,?,?,?,?,?)
                """)) {
                for (CartItem item : o.getItems()) {
                    ps.setString(1, o.getOrderId()); ps.setString(2, item.getProduct().getId());
                    ps.setString(3, item.getProduct().getName()); ps.setString(4, item.getProduct().getEmoji());
                    ps.setInt(5, item.getQuantity()); ps.setDouble(6, item.getProduct().getPrice());
                    ps.setDouble(7, item.totalPrice()); ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
            log.info("╔══════════════════════════════════════════════════════════╗");
            log.info("║  📦  ЗАКАЗ #{} СОХРАНЁН В БД", o.getOrderId());
            log.info("╠══════════════════════════════════════════════════════════╣");
            log.info("║  👤 {} (@{}) id={}", o.getFirstName(), o.getUsername(), o.getUserId());
            for (CartItem item : o.getItems())
                log.info("║  🏷  {} × {} = {} сум", item.getProduct().getName(), item.getQuantity(), String.format("%.0f", item.totalPrice()));
            log.info("║  💰 {} | {}", String.format("%.0f сум", o.getTotalPrice()), o.paymentDisplay());
            log.info("╚══════════════════════════════════════════════════════════╝");
        } catch (SQLException e) {
            log.error("❌ DB save order failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save order", e);
        }
    }

    public List<Order> forUser(long userId) {
        List<Order> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM orders WHERE user_id=? ORDER BY created_at DESC LIMIT 10")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapOrder(rs, conn));
            }
        } catch (SQLException e) { log.error("❌ forUser: {}", e.getMessage()); }
        return result;
    }

    public List<Order> getRecent(int limit) {
        List<Order> result = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM orders ORDER BY created_at DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapOrder(rs, conn));
            }
        } catch (SQLException e) { log.error("❌ getRecent: {}", e.getMessage()); }
        return result;
    }

    public boolean updateStatus(String orderId, Order.Status newStatus) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status=? WHERE order_id=?")) {
            ps.setString(1, newStatus.name()); ps.setString(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { log.error("❌ updateStatus: {}", e.getMessage()); return false; }
    }

    public int totalCount() {
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM orders")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { log.error("❌ count: {}", e.getMessage()); }
        return 0;
    }

    public double totalRevenue() {
        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_price),0) FROM orders WHERE status != 'CANCELLED'")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { log.error("❌ revenue: {}", e.getMessage()); }
        return 0;
    }

    private Order mapOrder(ResultSet rs, Connection conn) throws SQLException {
        String orderId = rs.getString("order_id");
        return new Order(
            orderId, rs.getLong("user_id"), rs.getString("username"), rs.getString("first_name"),
            loadItems(orderId, conn), rs.getDouble("total_price"),
            rs.getString("phone"), rs.getString("address"), rs.getString("payment_method"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            Order.Status.valueOf(rs.getString("status"))
        );
    }

    private List<CartItem> loadItems(String orderId, Connection conn) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM order_items WHERE order_id=?")) {
            ps.setString(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pid = rs.getString("product_id");
                    Product p = Catalog.byId(pid).orElseGet(() ->
                    {
                        try {
                            return new Product(pid, rs.getString("product_name"), rs.getString("product_emoji"),
                                "", rs.getDouble("price"), "", "all", 0, null);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    items.add(new CartItem(p, rs.getInt("quantity")));
                }
            }
        }
        return items;
    }
}
