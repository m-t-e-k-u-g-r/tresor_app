import {createContext, ReactNode, useContext, useState} from "react";
import {CreateSecret, Secret} from "../types/secret";
import {getApiUrl} from "../comunication/api-url";
import {useSecurity} from "./security-context";
import {SecretType} from "../types/secret-type";

interface SecretContextType {
    secrets: Secret[];
    loadSecrets: () => Promise<void>;
    addSecret: (kind: SecretType, secret: CreateSecret) => Promise<void>;
    deleteSecret: (kind: SecretType, id: number) => Promise<void>;
}

const SecretContext = createContext<SecretContextType | undefined>(undefined);

export function SecretProvider({ children }: { children: ReactNode }) {
    const [secrets, setSecrets] = useState<Secret[]>([]);
    const { csrfToken } = useSecurity();

    async function loadSecrets() {
        const API_URL = getApiUrl();

        try {
            if (csrfToken == null) return;
            const response = await fetch(`${API_URL}/secrets`, {
                method: 'GET',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
            if (response.status === 204) {
                console.log('No secrets retrieved');
                return;
            }
            const data: Secret[] = await response.json();
            console.log('Secrets successfully got:', data);
            setSecrets(data)
        } catch (e: any) {
            console.error('Failed to get secrets:', e.message);
            throw new Error('Failed to get secrets. ' || e.message);
        }
    }

    async function addSecret(kind: SecretType, secret: CreateSecret) {
        const API_URL = getApiUrl();

        try {
            if (csrfToken == null) return;
            const response = await fetch(`${API_URL}/secrets/new`, {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    ...secret,
                    type: SecretType[kind]
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

    async function deleteSecret(kind: SecretType, id: number) {
        const API_URL = getApiUrl();

        try {
            if (csrfToken == null) return;
            const type = SecretType[kind];
            const response = await fetch(`${API_URL}/secrets/${type}/${id}`, {
                method: 'DELETE',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                },
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
        } catch (error: any) {
            console.error('Error deleting secret:', error.message);
            throw new Error('Failed to delete secret. ' || error.message);
        }
    }

    return (
        <SecretContext.Provider value={{ secrets, loadSecrets, addSecret, deleteSecret }} >
            {children}
        </SecretContext.Provider>
    )
}

export function useSecret() {
    const context = useContext(SecretContext);
    if (!context) throw new Error("useSecret() must be used inside <SecretProvider>");
    return context;
}
