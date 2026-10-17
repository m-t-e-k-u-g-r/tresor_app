BEGIN TRANSACTION;

ALTER TABLE credential_secret
    DROP CONSTRAINT credential_secret_user_id_fkey;

ALTER TABLE credential_secret
    ADD CONSTRAINT credential_secret_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES "user"(id)
            ON DELETE CASCADE;


ALTER TABLE creditcard_secret
    DROP CONSTRAINT creditcard_secret_user_id_fkey;

ALTER TABLE creditcard_secret
    ADD CONSTRAINT creditcard_secret_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES "user"(id)
            ON DELETE CASCADE;


ALTER TABLE note_secret
    DROP CONSTRAINT note_secret_user_id_fkey;

ALTER TABLE note_secret
    ADD CONSTRAINT note_secret_user_id_fkey
        FOREIGN KEY (user_id) REFERENCES "user"(id)
            ON DELETE CASCADE;

COMMIT;