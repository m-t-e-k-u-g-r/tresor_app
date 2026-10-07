package ch.bbw.pr.tresorbackend.entities;

import java.time.Instant;

public interface Secret {
    Long getId();
    User getUser();

    Instant getCreatedAt();
    Instant getUpdatedAt();
}
