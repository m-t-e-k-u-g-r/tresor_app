
export type Secret = CredentialSecret | CreditCardSecret | NoteSecret;

export interface CredentialSecret {
    username: string;
    password: string;
    url: string;
}

export interface CreditCardSecret {
    cardType: string;
    cardNumber: string;
    expiration: string;
    cvv: string;
}

export interface NoteSecret {
    title: string;
    content: string;
}
