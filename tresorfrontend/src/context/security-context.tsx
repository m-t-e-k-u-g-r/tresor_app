import {createContext, ReactNode, useContext, useEffect, useRef, useState} from "react";
import {getApiUrl} from "../comunication/api-url";
import {CsrfResponse} from "../types/csrf-response";

interface SecurityContextType {
    csrfToken: string | undefined;
}

const SecurityContext = createContext<SecurityContextType | undefined>(undefined);

export function SecurityProvider({ children }: { children: ReactNode }) {
    const [csrfToken, setCsrfToken] = useState<string | undefined>(undefined);
    const requestStarted = useRef(false);
    const [retries, setRetries] = useState<number>(0);

    useEffect(() => {
        if (csrfToken || requestStarted.current) return;

        requestStarted.current = true;
        getCsrfToken();
    }, [csrfToken])

    async function getCsrfToken() {
        if (retries > 5) {
            console.log('Failed to get CSRF token')
            return
        }
        const API_URL = getApiUrl();

        const response = await fetch(`${API_URL}/auth/csrf`, {
            method: 'GET',
        })

        if (response.ok) {
            const data: CsrfResponse = await response.json();
            setCsrfToken(data.token);
        } else {
            setRetries(prev => prev + 1);
            await getCsrfToken();
        }
    }

    return (
        <SecurityContext.Provider value={{ csrfToken }} >
            {children}
        </SecurityContext.Provider>
    )
}

export function useSecurity() {
    const context = useContext(SecurityContext);
    if (!context) throw new Error("useSecurity() must be used inside <SecurityProvider>");
    return context;
}
