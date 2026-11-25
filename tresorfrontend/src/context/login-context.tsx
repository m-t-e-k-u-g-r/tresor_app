import {createContext, ReactNode, useContext, useState} from "react";
import {Login} from "../types/user";
import {LoginResponse} from "../types/login-response";
import {getApiUrl} from "../comunication/api-url";
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
    jwt: string | null;
    logout: () => void;
}

const LoginContext = createContext<LoginContextType | undefined>(undefined);

export function LoginProvider({ children }: { children: ReactNode }) {
    const [jwt, setJwt] = useState<string | null>(null);
    const [userId, setUserId] = useState<number | null>(null);
    const [isAdmin, setIsAdmin] = useState<boolean>(false);
    const [loginValues, setLoginValues] = useState<Login>({
        email: "",
        password: "",
    });
    const navigate = useNavigate();

    async function checkLogin() {
        if (userId != null) return;
        let JWT = jwt;
        if (JWT == null) {
            const saved = localStorage.getItem('jwt')
            if (saved != null) {
                setJwt(saved);
                JWT = saved;
            }
        }

        const API_URL = getApiUrl();
        const response = await fetch(`${API_URL}/auth/check`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${JWT}`
            },
        });
        if (response.ok) {
            const data: LoginResponse = await response.json();
            setUserId(data.userId);
            setIsAdmin(data.admin);
            setLoginValues({
                email: data.email,
                password: "",
            })
            return;
        } else {
            setJwt(null);
        }

        navigate("/");
    }

    function updateLogin(login: Login) {
        setLoginValues(login);
    }

    function logout() {
        setJwt(null);
        setUserId(null);
        setIsAdmin(false);
        setLoginValues({
            email: "",
            password: "",
        });
        localStorage.removeItem("jwt");
        navigate("/");
    }

    async function signup(req: SignupRequest): Promise<boolean> {
        if (!req.captchaToken) throw Error("No reCAPTCHA token");
        if (req.password !== req.passwordConfirmation) throw Error("Invalid password confirmation");
        const API_URL = getApiUrl();

        try {
            const response = await fetch(`${API_URL}/users/new`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    firstName: req.firstName,
                    lastName: req.lastName,
                    email: req.email,
                    password: req.password,
                    passwordConfirmation: req.passwordConfirmation,
                    recaptchaToken: req.captchaToken
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
        console.log('login request received')
        try {
            const baseAuth = btoa(`${loginValues.email}:${loginValues.password}`);
            const response = await fetch(`${API_URL}/users/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Basic ${baseAuth}`
                }
            });
            console.log('respone', response.status)
            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Server response failed.');
            }
            const data: LoginResponse = await response.json();
            console.log('User successfully logged in:', data);
            setJwt(data.token);
            localStorage.setItem('jwt', data.token);
            setUserId(data.userId);
            setLoginValues({...loginValues, email: data.email });
            setIsAdmin(data.admin);
            return true;
        } catch (error: any) {
            console.error('Failed to login user:', error.message);
            return false;
        }
    }

    return (
        <LoginContext.Provider value={{ checkLogin, loginValues, updateLogin, signup, login, userId, isAdmin, jwt, logout }}>
            {children}
        </LoginContext.Provider>
    )
}

export function useLogin() {
    const context = useContext(LoginContext);
    if (!context) throw new Error("useLogin() must be used inside <LoginProvider>");
    return context;
}
