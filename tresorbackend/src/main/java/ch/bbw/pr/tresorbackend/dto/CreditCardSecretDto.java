package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardSecretDto extends SecretDto {
    @NotEmpty
    private String cardType;
    @NotEmpty
    private String cardNumber;
    @NotEmpty
    private String expiration;
    @NotEmpty
    private String cvv;
}
