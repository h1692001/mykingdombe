package com.mykingdom.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterCommand {
    private String email;
    private String fullname;
    private String password;
    private String dob;
    private String gender;
    private String phone;
}
