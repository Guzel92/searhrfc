package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserChangeRolesResponseDTO {
    private long id;
    private String login;
    // add roles any user response
}
