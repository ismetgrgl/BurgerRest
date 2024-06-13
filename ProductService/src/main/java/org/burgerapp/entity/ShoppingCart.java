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
@Table(name = "tbl_cart")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long authId;

    @Builder.Default
    Double totalPrice = 0.0;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // JSON serileştirme sırasında items ilişkisini engeller
    private Set<CartItem> items = new HashSet<>();




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public List<Product> getProductList() {
        List<Product> productList = new ArrayList<>();
        for (CartItem item : items) {
            productList.add(item.getProduct());
        }
        return productList;
    }

    public List<Items> getItemList() {
        List<Items> itemList = new ArrayList<>();
        for (CartItem item : items) {
            itemList.addAll(item.getItems());
        }
        return itemList;
    }
}
