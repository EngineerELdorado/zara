package com.zara.Zara.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity(name = "developers")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Developer {

    private Long id;
    private String email;
    private String name;
    @Column(unique = true)
    private String apiKey;
    private String status;
}
