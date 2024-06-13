package org.burgerapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany
    @JoinTable(name = "cartitem_items",
            joinColumns = @JoinColumn(name = "cartitem_id"),
            inverseJoinColumns = @JoinColumn(name = "items_id"))
    private Set<Items> items = new HashSet<>();

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id")
    @JsonIgnore // JSON serileştirme sırasında alışveriş sepeti ilişkisini engeller
    private ShoppingCart shoppingCart;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(id, cartItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
