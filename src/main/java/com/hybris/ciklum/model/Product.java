package com.hybris.ciklum.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString(exclude = {"orderItems"})
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Table(name = "products_status")
    public enum ProductsStatus {
        IN_STOCK("in_stock"), OUT_OF_STOCK("out_of_stock"), RUNNING_LOW("running_low");

        ProductsStatus(String name) {
        }
    }

    @NotBlank(message = "The 'Name' is required")
    private String name;

    @NotNull(message = "The 'Price' is required")
    private Long price;

    @Enumerated(EnumType.STRING)
    private ProductsStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();
}
