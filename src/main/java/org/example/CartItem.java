package org.example;

/**
 * Один элемент корзины: товар + количество.
 */
public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product  = product;
        this.quantity = quantity;
    }

    public void addQuantity(int delta) {
        this.quantity = Math.max(1, this.quantity + delta);
    }

    public double totalPrice() {
        return product.getPrice() * quantity;
    }

    public String toLine() {
        return String.format("%s *%s*\n   %d шт. × %.0f = *%.0f сум*",
            product.getEmoji(), product.getName(),
            quantity, product.getPrice(), totalPrice());
    }

    public Product getProduct()        { return product; }
    public int     getQuantity()       { return quantity; }
    public void    setQuantity(int q)  { this.quantity = q; }
}
