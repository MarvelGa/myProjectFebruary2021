package com.hybris.ciklum.model;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = true)
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;


    @ToString.Exclude
    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    private int quantity;
}
