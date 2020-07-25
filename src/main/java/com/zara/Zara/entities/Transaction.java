package com.zara.Zara.entities;

import com.zara.Zara.enums.TransactionType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "transactions")
@SQLDelete(sql = "UPDATE transactions SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 5075429827673941257L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Account senderAccount;
    @ManyToOne
    @NotNull
    private Account receiverAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TransactionType type;
    @NotNull
    private BigDecimal senderAmount;
    @NotNull
    private BigDecimal senderAmountInUsd;
    @NotNull
    private BigDecimal senderAmountInReceiverCurrency;
    @OneToOne
    private Currency senderCurrency;

    @NotNull
    private BigDecimal chargesInSenderCurrency;
    @NotNull
    private BigDecimal chargesInUsd;
    @NotNull
    private BigDecimal chargesInReceiverCurrency;

    @NotNull
    private BigDecimal receiverAmount;
    @NotNull
    private BigDecimal receiverAmountInUsd;

    @NotNull
    private BigDecimal receiverAmountInSenderCurrency;

    @NotNull
    private BigDecimal fxRate;

    @NotNull
    @OneToOne
    private Currency receiverCurrency;
    @Column(unique = true)
    private String transactionNumber;

    @CreationTimestamp
    @Setter(value = AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Setter(value = AccessLevel.NONE)
    private Date updatedAt;

    private Date deletedAt;

    @PreRemove
    protected void onDelete() {
        deletedAt = new Date();
    }
}
