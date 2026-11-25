package ch.bbw.pr.tresorbackend.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private Integer userId;
    private String email;
    private boolean isAdmin;
    private String token;
}
