import {createContext, ReactNode, useContext, useState} from "react";
import {Login} from "../types/user";
import {LoginResponse} from "../types/login-response";
import {getApiUrl} from "../comunication/api-url";
import {useNavigate} from "react-router-dom";

interface LoginContextType {
    checkLogin: () => void;
    loginValues: Login;
    updateLogin: (login: Login) => void;
    signup: (firstName: string, lastName: string, passwordConfirmation: string) => Promise<void>;
    login: () => Promise<void>;
    userId: number | null;
}

const LoginContext = createContext<LoginContextType | undefined>(undefined);

export function LoginProvider({ children }: { children: ReactNode }) {
    const [userId, setUserId] = useState<number | null>(null);
    const [loginValues, setLoginValues] = useState<Login>({
        email: "",
        password: "",
    });
    const navigate = useNavigate();

    function checkLogin() {
        if (userId != null) return;
        navigate("/");
    }

    function updateLogin(login: Login) {
        setLoginValues(login);
    }

    async function signup(firstName: string, lastName: string, passwordConfirmation: string) {
        if (loginValues.password !== passwordConfirmation) return;
        const API_URL = getApiUrl();

        try {
            const response = await fetch(`${API_URL}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    firstName: firstName,
                    lastName: lastName,
                    email: loginValues.email,
                    password: loginValues.password,
                    passwordConfirmation: passwordConfirmation
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
        } catch (error: any) {
            console.error('Failed to post user:', error.message);
            throw new Error('Failed to save user. ' || error.message);
        }
    }

    async function login() {
        const API_URL = getApiUrl();
        try {
            const response = await fetch(`${API_URL}/users/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: loginValues.email,
                    password: loginValues.password
                })
            });
            console.log(response);
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
            const data: LoginResponse = await response.json();
            console.log('User successfully logged in:', data);
            setUserId(data.userId);
        } catch (error: any) {
            console.error('Failed to login user:', error.message);
            throw new Error('Failed to login user. ' + error.message);
        }
    }

    return (
        <LoginContext.Provider value={{ checkLogin, loginValues, updateLogin, signup, login, userId }}>
            {children}
        </LoginContext.Provider>
    )
}

export function useLogin() {
    const context = useContext(LoginContext);
    if (!context) throw new Error("useLogin() must be used inside <LoginProvider>");
    return context;
}
