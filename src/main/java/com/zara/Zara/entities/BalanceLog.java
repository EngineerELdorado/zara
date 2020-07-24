package com.zara.Zara.entities;

import lombok.AccessLevel;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "balance_logs")
@SQLDelete(sql = "UPDATE balance_logs SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
public class BalanceLog implements Serializable {

    private static final long serialVersionUID = 4941561381885600007L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Transaction transaction;
    @ManyToOne
    private Account account;

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
