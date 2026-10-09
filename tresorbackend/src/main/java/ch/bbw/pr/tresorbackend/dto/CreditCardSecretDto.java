package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardSecretDto extends SecretDto {
    @NotEmpty
    private String cardType;
    @NotEmpty
    private String cardNumber;
    @NotEmpty
    private String expiration;
    @NotEmpty
    private String cvv;

    @Override
    protected CreditCardSecretDto copy() {
        return new CreditCardSecretDto(
                this.getCardType(),
                this.getCardNumber(),
                this.getExpiration(),
                this.getCvv()
        );
    }
}
