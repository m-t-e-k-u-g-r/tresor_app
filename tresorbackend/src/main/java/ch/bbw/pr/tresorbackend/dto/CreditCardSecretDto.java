package ch.bbw.pr.tresorbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
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
    public SecretType getType() {
        return SecretType.CREDIT_CARD;
    }

    @Override
    protected CreditCardSecretDto copy() {
        return CreditCardSecretDto.builder()
                .id(this.getId())
                .cardType(this.getCardType())
                .cardNumber(this.getCardNumber())
                .expiration(this.getExpiration())
                .cvv(this.getCvv())
                .build();
    }
}
