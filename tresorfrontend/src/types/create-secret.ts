import {Secret} from "./secret";

export interface CreateSecretRequest {
    email: string;
    encryptPassword: string;
    secret: Secret;
}
