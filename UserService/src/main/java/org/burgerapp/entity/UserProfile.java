package org.burgerapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "tbl_user")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long authId;
    String username;
    String name;
    String surname;
    String email;
    String phone;
    String address;
    Double balance;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    EStatus status=EStatus.PENDING;


}
