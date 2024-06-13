package org.burgerapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.burgerapp.entity.enums.ERole;
import org.burgerapp.entity.enums.EStatus;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Entity
@Table(name = "tbl_auth")
public class Auth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String username;
    @Column(unique = true)
    String email;
    String password;
    String code;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    ERole eRole = ERole.USER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    EStatus eStatus= EStatus.PENDING;
}
