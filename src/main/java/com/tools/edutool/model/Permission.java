package com.tools.edutool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    @ManyToOne(fetch = LAZY)
    private Role role;
    private String name;

}