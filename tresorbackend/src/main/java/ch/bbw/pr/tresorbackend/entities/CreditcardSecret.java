package ch.bbw.pr.tresorbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "creditcard_secret")
public class CreditcardSecret implements Secret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(name = "card_type", nullable = false, length = Integer.MAX_VALUE)
    private String cardType;

    @NotNull
    @Column(name = "card_number", nullable = false, length = Integer.MAX_VALUE)
    private String cardNumber;

    @NotNull
    @Column(name = "expiration", nullable = false, length = Integer.MAX_VALUE)
    private String expiration;

    @NotNull
    @Column(name = "cvv", nullable = false, length = Integer.MAX_VALUE)
    private String cvv;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}