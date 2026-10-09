package ch.bbw.pr.tresorbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.function.Function;

@Getter
@Setter
public abstract class SecretDto {

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
