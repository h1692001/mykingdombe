package com.mykingdom.entity;

import com.mykingdom.security.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String fullname;
    @Column(name="avatar")
    private String avatar="https://res.cloudinary.com/dyvgnrswn/image/upload/v1684721106/mhqiehkoysbdiquyu88a.png";;
    private String email;
    private String password;
    private String gender;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private CartEntity cart;

    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private FavouriteEntity favourite;

    @OneToMany(mappedBy = "owner")
    private List<AddressEntity> addresses;
 
    @OneToMany(mappedBy = "owner")
    private List<BillEntity> bills;
}
