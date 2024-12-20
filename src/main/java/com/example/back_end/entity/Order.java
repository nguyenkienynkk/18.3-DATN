package com.example.back_end.entity;

import com.example.back_end.infrastructure.constant.DeliveryStatusType;
import com.example.back_end.infrastructure.constant.OrderStatusType;
import com.example.back_end.infrastructure.constant.PaymentMethodType;
import com.example.back_end.infrastructure.constant.PaymentMode;
import com.example.back_end.infrastructure.constant.PaymentStatusType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "\"order\"")
public class Order extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_address_id")
    private Address pickupAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @Column(name = "order_guid")
    private UUID orderGuid;

    @Column(name = "pickup_in_store")
    private Boolean pickupInStore;

    @Enumerated
    @Column(name = "order_status_id")
    private OrderStatusType orderStatusId;

    @Enumerated
    @Column(name = "payment_status_id")
    private PaymentStatusType paymentStatusId;

    @Enumerated
    @Column(name = "payment_method_id")
    private PaymentMethodType paymentMethodId;

    @Column(name = "bill_code")
    private String billCode;

    @Enumerated
    @Column(name = "payment_mode")
    private PaymentMode paymentMode;

    @Enumerated
    private DeliveryStatusType deliveryStatusType;

    @Column(name = "order_subtotal", precision = 18, scale = 2)
    private BigDecimal orderSubtotal;

    @Column(name = "order_subtotal_discount", precision = 18, scale = 2)
    private BigDecimal orderSubtotalDiscount;

    @Column(name = "order_shipping", precision = 18, scale = 2)
    private BigDecimal orderShipping;

    @Column(name = "order_discount", precision = 18, scale = 2)
    private BigDecimal orderDiscount;

    @Column(name = "order_total", precision = 18, scale = 2)
    private BigDecimal orderTotal;

    @Column(name = "refunded_amount", precision = 18, scale = 2)
    private BigDecimal refundedAmount;

    @Column(name = "paid_date_utc")
    private Instant paidDateUtc;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE)
    private List<OrderStatusHistory> statusHistories;

}