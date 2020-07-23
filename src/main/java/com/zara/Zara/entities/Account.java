package com.zara.Zara.entities;

import com.zara.Zara.enums.AccountType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE accounts SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String accountNumber;
    @Column(nullable = false)
    private String pin;

    @NotNull(message = "Missing required field userId")
    @OneToOne(targetEntity = User.class, fetch = LAZY, optional = false, cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true, updatable = false, nullable = false)
    private User user;
    @Column(nullable = false)
    private BigDecimal balance;
    @ManyToOne
    private Currency currency;

    @ManyToOne
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

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
