package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Состояние диалога + корзина пользователя.
 * Также хранит состояние диалога при добавлении поставки (для админа).
 */
public class UserSession {

    public enum State {
        IDLE,
        AWAITING_PHONE,
        AWAITING_ADDRESS,
        CHOOSING_PAYMENT,
        CONFIRMING,
        // Админские состояния
        ADMIN_SUPPLY_CHOOSE_PRODUCT,  // ждём выбор товара
        ADMIN_SUPPLY_ENTER_QTY        // ввод количества поставки
    }

    private State   state   = State.IDLE;
    private Product selectedProduct;
    private int     quantity = 1;
    private String  phone;
    private String  address;
    private String  paymentMethod;
    private boolean checkoutFromCart = false;

    // Корзина
    private final List<CartItem> cart = new ArrayList<>();

    // Для поставки (админ)
    private String pendingSupplyProductId;

    // ── Корзина ───────────────────────────────────────────────────────────────

    public void addToCart(Product p, int qty) {
        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(p.getId())) {
                item.addQuantity(qty); return;
            }
        }
        cart.add(new CartItem(p, qty));
    }

    public boolean removeFromCart(String productId) {
        return cart.removeIf(i -> i.getProduct().getId().equals(productId));
    }

    public void clearCart()    { cart.clear(); }
    public List<CartItem> getCart() { return cart; }
    public boolean isCartEmpty()    { return cart.isEmpty(); }
    public int     cartSize()       { return cart.size(); }

    public double cartTotal() {
        return cart.stream().mapToDouble(CartItem::totalPrice).sum();
    }

    public String cartText() {
        if (cart.isEmpty()) return "🛒 Корзина пуста";
        StringBuilder sb = new StringBuilder("🛒 *ТВОЯ КОРЗИНА*\n\n");
        int i = 1;
        for (CartItem item : cart) sb.append(i++).append(". ").append(item.toLine()).append("\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━\n");
        sb.append(String.format("💰 *ИТОГО: %.0f сум*", cartTotal()));
        return sb.toString();
    }

    public void resetCheckout() {
        state = State.IDLE; selectedProduct = null; quantity = 1;
        phone = null; address = null; paymentMethod = null; checkoutFromCart = false;
    }

    public void resetAll() { resetCheckout(); cart.clear(); }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public State   getState()                     { return state; }
    public void    setState(State s)              { this.state = s; }
    public Product getSelectedProduct()           { return selectedProduct; }
    public void    setSelectedProduct(Product p)  { this.selectedProduct = p; }
    public int     getQuantity()                  { return quantity; }
    public void    setQuantity(int q)             { this.quantity = q; }
    public String  getPhone()                     { return phone; }
    public void    setPhone(String p)             { this.phone = p; }
    public String  getAddress()                   { return address; }
    public void    setAddress(String a)           { this.address = a; }
    public String  getPaymentMethod()             { return paymentMethod; }
    public void    setPaymentMethod(String m)     { this.paymentMethod = m; }
    public boolean isCheckoutFromCart()           { return checkoutFromCart; }
    public void    setCheckoutFromCart(boolean b) { this.checkoutFromCart = b; }
    public String  getPendingSupplyProductId()    { return pendingSupplyProductId; }
    public void    setPendingSupplyProductId(String id) { this.pendingSupplyProductId = id; }
}
