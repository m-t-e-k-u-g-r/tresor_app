package ch.bbw.pr.tresorbackend.dto;

import ch.bbw.pr.tresorbackend.entities.User;

/**
 * ParsingResult
 *
 * @author Matteo Krüger
 */
public record ParsingResult(
        User user,
        SecretType type
) {}
