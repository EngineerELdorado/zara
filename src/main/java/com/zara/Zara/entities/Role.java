package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity(name = "ROLES")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(unique = true, name = "NAME")
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<AppUser>users;
}
