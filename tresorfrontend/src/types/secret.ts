
export type Secret = CredentialSecret | CreditCardSecret | NoteSecret;
export type CreateSecret = CreateCredentialSecret | CreateCreditCardSecret | CreateNoteSecret;

interface SecretId {
    id: number;
}

export interface CredentialSecret extends SecretId {
    username: string;
    password: string;
    url: string;
}
export type CreateCredentialSecret = Omit<CredentialSecret, "id">;

export interface CreditCardSecret extends SecretId {
    cardType: string;
    cardNumber: string;
    expiration: string;
    cvv: string;
}
export type CreateCreditCardSecret = Omit<CreditCardSecret, "id">;

export interface NoteSecret extends SecretId {
    title: string;
    content: string;
}
export type CreateNoteSecret = Omit<NoteSecret, "id">;
