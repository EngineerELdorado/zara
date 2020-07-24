package com.zara.Zara.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?", check = ResultCheckStyle.COUNT)
@Where(clause = "deleted_at IS NULL")
public class User implements Serializable {

    private static final long serialVersionUID = 8695541412706860427L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private boolean onboarded;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(unique = true)
    private String phoneNumber;
    private String pin;
    private Date dob;
    private String password;
    @Transient
    private String token;

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
