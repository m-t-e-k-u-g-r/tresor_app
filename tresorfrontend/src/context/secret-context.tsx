import {createContext, ReactNode, useContext, useState} from "react";
import {Secret} from "../types/secret";
import {getApiUrl} from "../comunication/api-url";
import {useLogin} from "./login-context";
import {SecretType} from "../types/secret-type";

interface SecretContextType {
    secrets: Secret[];
    loadSecrets: () => void;
    addSecret: (kind: SecretType, secret: Secret) => Promise<void>;
}

const SecretContext = createContext<SecretContextType | undefined>(undefined);

export function SecretProvider({ children }: { children: ReactNode }) {
    const [secrets, setSecrets] = useState<Secret[]>([]);
    const { loginValues } = useLogin();

    async function loadSecrets() {
        const API_URL = getApiUrl();
        const credentials = loginValues;

        try {
            const response = await fetch(`${API_URL}/secrets/byemail`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: credentials.email,
                    password: credentials.password
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
            const data = await response.json();
            console.log('Secrets successfully got:', data);
            setSecrets(data)
        } catch (e: any) {
            console.error('Failed to get secrets:', e.message);
            throw new Error('Failed to get secrets. ' || e.message);
        }
    }

    async function addSecret(kind: SecretType, secret: Secret) {
        const API_URL = getApiUrl();
        const credentials = loginValues;

        try {
            const response = await fetch(`${API_URL}/secrets`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: credentials.email,
                    password: credentials.password,
                    secret: {
                        ...secret,
                        type: SecretType[kind]
                    }
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }

            const data: Secret = await response.json();
            console.log('Secret successfully posted:', data);
            setSecrets(prev => [...prev, data])
        } catch (error: any) {
            console.error('Error posting secret:', error.message);
            throw new Error('Failed to save secret. ' || error.message);
        }
    }

    return (
        <SecretContext.Provider value={{ secrets, loadSecrets, addSecret }} >
            {children}
        </SecretContext.Provider>
    )
}

export function useSecret() {
    const context = useContext(SecretContext);
    if (!context) throw new Error("useSecret() must be used inside <SecretProvider>");
    return context;
}
