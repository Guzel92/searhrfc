package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRegisterResponseDTO {
    private long id;
    private String login;
    private String token;
    // TODO: think about roles
}
