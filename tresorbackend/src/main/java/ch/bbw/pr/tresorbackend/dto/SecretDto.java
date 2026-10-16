package ch.bbw.pr.tresorbackend.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Field;
import java.util.function.Function;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CredentialSecretDto.class, name = "CREDENTIAL"),
        @JsonSubTypes.Type(value = CreditCardSecretDto.class, name = "CREDIT_CARD"),
        @JsonSubTypes.Type(value = NoteSecretDto.class, name = "NOTE")
})
@SuperBuilder
@NoArgsConstructor
public abstract class SecretDto {
    private Long id;

    public abstract SecretType getType();

    protected abstract SecretDto copy();

    public SecretDto transform(Function<String, String> function) throws Exception {
        SecretDto result = copy();

        for (Field field : result.getClass().getDeclaredFields()) {
            if (field.getType() != String.class) {
                continue;
            }

            field.setAccessible(true);

            String value = (String) field.get(result);
            if (value == null) {
                continue;
            }

            String transformed = function.apply(value);
            field.set(result, transformed);
        }
        return result;
    }
}
