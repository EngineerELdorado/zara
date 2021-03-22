package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Entity(name = "USERS")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @NotEmpty
    @Column(name = "FULL_NAME")
    private String fullName;
    @NotEmpty
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "USERNAME", unique = true)
    private String username;
    @NotEmpty
    @Column(name = "PIN")
    private String pin;
    @Column(name = "CREATED_ON")
    private Date createdOn;
    @Column(name = "IS_VERIFIED")
    private boolean isVerified;
    @Column(name = "TEMP_PIN")
    private String tempPin;
    @Column(name = "NEED_TO_CHANGE_PIN")
    public  boolean needToChangePin;
    @Column(name = "IS_LOCKED")
    public boolean isLocked;
    @OneToOne
    public AppUser lockedBy;
    @OneToOne
    public AppUser unlockedBy;
    @Column(name = "LOCKED_ON")
    public Date lockedOn;
    @Column(name = "UNLOCKED_ON")
    public Date unlockedOn;
    @Column(name = "VERIFICATION_CODE")
    private String verificationCode;
    @ManyToMany
    private Collection<Role>roles;
}
