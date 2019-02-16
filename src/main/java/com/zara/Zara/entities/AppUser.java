package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "account_number", unique = true)
    @NotEmpty
    @NotNull
    private String accountNumber;
    @Column(name = "agent_number")
    private String agentNumber;
    @NotEmpty
    private String fullName;
    @NotEmpty
    @Column(unique = true)
    private String phone;
    //@NotEmpty
    private String pin;
    private Double balance;
    private Date dob;
    private Date createdOn;
    private boolean isVerified;
    private String tempPin;
    public  boolean needToChangePin;
    public boolean isLocked;
    @OneToOne
    public AppUser lockedBy;
    @OneToOne
    public AppUser unlockedBy;
    public Date lockedOn;
    public Date unlockedOn;
    private String verificationCode;
    @Transient
    @JsonIgnore
    private String role;
    @ManyToMany
    private Collection<Role>roles;
    

}
