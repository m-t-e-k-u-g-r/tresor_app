package ch.bbw.pr.tresorbackend.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private Integer userId;
    private boolean isAdmin;
}
