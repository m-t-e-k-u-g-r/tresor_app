package ch.bbw.pr.tresorbackend.dto;

public enum SecretType {
    CREDENTIAL,
    CREDIT_CARD,
    NOTE;

    public static SecretType parseSecretType(String typeString) {
        try {
            return SecretType.valueOf(typeString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
