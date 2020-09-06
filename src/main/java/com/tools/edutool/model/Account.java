package com.tools.edutool.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.time.Instant;
import java.util.Collection;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
//@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String accountNumber;
    private String accountType;
    private String bankName;
    @OneToOne(fetch = LAZY)
    private User user;

}
