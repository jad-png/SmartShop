package com.jyoxin.smartshop.entity;

import com.jyoxin.smartshop.entity.enums.Tier;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "clients")
@SQLDelete(sql = "UPDATE clients SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_tier", nullable = false, length = 20)
    @Builder.Default
    private Tier loyaltyTier = Tier.BASIC;


    @Embedded
    @Builder.Default
    private ClientStats stats = ClientStats.empty();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    /**
     * Record a confirmed order in the stats.
     * 
     * @param orderAmount Total TTC amount
     */
    public void recordOrder(BigDecimal orderAmount) {
        if (this.stats == null) {
            this.stats = ClientStats.empty();
        }
        this.stats.recordOrder(orderAmount);
    }
}