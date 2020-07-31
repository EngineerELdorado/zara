package com.zara.Zara.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@SQLDelete(sql = "UPDATE commission_accounts SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
@Table(name = "commission_accounts")
@Getter
@Setter
public class CommissionAccount implements Serializable {

    private static final long serialVersionUID = 3780981136827374402L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @OneToOne
    private Account account;

    @CreationTimestamp
    @Setter(value = AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private Date createdAt;

    @Column(nullable = false)
    private BigDecimal balance;

    @UpdateTimestamp
    @Setter(value = AccessLevel.NONE)
    private Date updatedAt;

    private Date deletedAt;

    @Column()
    private boolean main = false;

    @PreRemove
    protected void onDelete() {
        deletedAt = new Date();
    }
}

