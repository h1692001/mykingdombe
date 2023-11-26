package com.mykingdom.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mykingdom.security.Permisson.*;


@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN(Set.of(ADMIN_DELETE,
            ADMIN_UPDATE,
            ADMIN_READ,
            ADMIN_CREATE,
            USER_CREATE,
            USER_UPDATE,
            USER_READ,
            USER_DELETE
    )),
    USER(Set.of(
            USER_CREATE,
            USER_UPDATE,
            USER_READ,
            USER_DELETE
    ));

    private final Set<Permisson> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
