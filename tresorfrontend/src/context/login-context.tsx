import {createContext, ReactNode, useContext, useState} from "react";
import {Login} from "../types/user";
import {LoginResponse} from "../types/login-response";
import {getApiUrl} from "../comunication/api-url";
import {useSecurity} from "./security-context";
import {useNavigate} from "react-router-dom";
import {SignupRequest} from "../types/signup-request";

interface LoginContextType {
    checkLogin: () => Promise<void>;
    loginValues: Login;
    updateLogin: (login: Login) => void;
    signup: (request: SignupRequest) => Promise<boolean>;
    login: () => Promise<boolean>;
    userId: number | null;
    isAdmin: boolean;
}

const LoginContext = createContext<LoginContextType | undefined>(undefined);

export function LoginProvider({ children }: { children: ReactNode }) {
    const { csrfToken } = useSecurity();
    const [userId, setUserId] = useState<number | null>(null);
    const [isAdmin, setIsAdmin] = useState<boolean>(false);
    const [loginValues, setLoginValues] = useState<Login>({
        email: "",
        password: "",
    });
    const navigate = useNavigate();

    async function checkLogin() {
        if (userId != null) return;

        const API_URL = getApiUrl();
        const response = await fetch(`${API_URL}/auth/check`);
        if (response.ok) {
            const data: LoginResponse = await response.json();
            setUserId(data.userId);
            setIsAdmin(data.admin);
            return;
        }

        navigate("/");
    }

    function updateLogin(login: Login) {
        setLoginValues(login);
    }

    async function signup(req: SignupRequest): Promise<boolean> {
        if (req.password !== req.passwordConfirmation) throw Error("Invalid password confirmation");
        const API_URL = getApiUrl();

        try {
            if (csrfToken == null) return false;
            const response = await fetch(`${API_URL}/users/new`, {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    firstName: req.firstName,
                    lastName: req.lastName,
                    email: req.email,
                    password: req.password,
                    passwordConfirmation: req.passwordConfirmation
                })
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.error(errorData);
                return false;
            }
            return true;
        } catch (error: any) {
            console.error('Failed to post user:', error.message);
            return false
        }
    }

    async function login(): Promise<boolean> {
        const API_URL = getApiUrl();
        try {
            if (csrfToken == null) return false;
            const response = await fetch(`${API_URL}/users/login`, {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    email: loginValues.email,
                    password: loginValues.password
                })
            });
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
            const data: LoginResponse = await response.json();
            console.log('User successfully logged in:', data);
            setUserId(data.userId);
            setIsAdmin(data.admin);
            return true;
        } catch (error: any) {
            console.error('Failed to login user:', error.message);
            return false;
        }
    }

    return (
        <LoginContext.Provider value={{ checkLogin, loginValues, updateLogin, signup, login, userId, isAdmin }}>
            {children}
        </LoginContext.Provider>
    )
}

export function useLogin() {
    const context = useContext(LoginContext);
    if (!context) throw new Error("useLogin() must be used inside <LoginProvider>");
    return context;
}
