package com.mykingdom.dtos;

import com.mykingdom.security.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserResponse {
    private Long id;
    private String fullname;
    private String avatar;
    private String email;
    private Role role;
}
