import {Login} from "../types/user";
import {CreateSecretRequest} from "../types/create-secret";
import {SecretType} from "../types/secret-type";

/**
 * Fetch methodes for secret api calls
 * @author Peter Rutschmann
 */
export interface FetchSecretsProps extends CreateSecretRequest {
    kind: SecretType
}

//Post secret to server
export const postSecret = async ({ kind, email, encryptPassword, secret }: FetchSecretsProps) => {
    const protocol = process.env.REACT_APP_API_PROTOCOL; // "http"
    const host = process.env.REACT_APP_API_HOST; // "localhost"
    const port = process.env.REACT_APP_API_PORT; // "8080"
    const path = process.env.REACT_APP_API_PATH; // "/api"
    const portPart = port ? `:${port}` : ''; // port is optional
    const API_URL = `${protocol}://${host}${portPart}${path}`;

    try {
        const response = await fetch(`${API_URL}/secrets/${kind.toString()}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                encryptPassword: encryptPassword,
                content: secret
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Server response failed.');
        }

        const data = await response.json();
        console.log('Secret successfully posted:', data);
        return data;
        // TODO: change error type
    } catch (error: any) {
        console.error('Error posting secret:', error.message);
        throw new Error('Failed to save secret. ' || error.message);
    }
};

//get all secrets for a user identified by its email
export const getSecretsforUser = async (loginValues: Login) => {
    const protocol = process.env.REACT_APP_API_PROTOCOL; // "http"
    const host = process.env.REACT_APP_API_HOST; // "localhost"
    const port = process.env.REACT_APP_API_PORT; // "8080"
    const path = process.env.REACT_APP_API_PATH; // "/api"
    const portPart = port ? `:${port}` : ''; // port is optional
    const API_URL = `${protocol}://${host}${portPart}${path}`;

    try {
        const response = await fetch(`${API_URL}/secrets/byemail`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: loginValues.email,
                encryptPassword: loginValues.password
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Server response failed.');
        }
        const data = await response.json();
        console.log('Secret successfully got:', data);
        return data;
    } catch (error: any) {
        console.error('Failed to get secrets:', error.message);
        throw new Error('Failed to get secrets. ' || error.message);
    }
};