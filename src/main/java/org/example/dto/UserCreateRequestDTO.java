package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserCreateRequestDTO {
    private String login;
    private String password;
    private String[] roles;
    // private boolean agreemrntSigned для галочки
}
