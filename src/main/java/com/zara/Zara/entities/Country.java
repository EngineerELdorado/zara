package com.zara.Zara.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE countries SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(unique = true)
    private String code;

    @CreationTimestamp
    @Setter(value = AccessLevel.NONE)
    @Column(updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Setter(value = AccessLevel.NONE)
    private Date updatedAt;

    private Date deletedAt;
}
