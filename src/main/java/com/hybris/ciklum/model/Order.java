package com.hybris.ciklum.model;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "orders")
public class Order {

    public enum OrderStatus {
        NEW, PENDING, COMPLETE, CANCELED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @ToString.Exclude
    @Column(name = "created_at")
    private String createdAt;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();
}
