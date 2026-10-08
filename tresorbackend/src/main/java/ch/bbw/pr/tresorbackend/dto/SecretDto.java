package ch.bbw.pr.tresorbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.function.Function;

@Getter
@Setter
public abstract class SecretDto {
    public SecretDto transform(Function<String, String> function) throws IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() != String.class) {
                continue;
            }

            field.setAccessible(true);

            String value = (String) field.get(this);
            if (value == null) {
                continue;
            }

            String transformed = function.apply(value);
            field.set(this, transformed);
        }
        return this;
    }
}
