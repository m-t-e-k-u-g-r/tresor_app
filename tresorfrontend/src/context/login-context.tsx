import {createContext, ReactNode, useContext, useState} from "react";
import {Login} from "../types/user";

interface LoginContextType {
    loginValues: Login;
    updateLogin: (login: Login) => void;
}

const LoginContext = createContext<LoginContextType | undefined>(undefined);

export function LoginProvider({ children }: { children: ReactNode }) {
    const [loginValues, setLoginValues] = useState<Login>({
        email: "",
        password: "",
    });

    function updateLogin(login: Login) {
        setLoginValues(login);
    }

    return (
        <LoginContext.Provider value={{ loginValues, updateLogin }}>
            {children}
        </LoginContext.Provider>
    )
}

export function useLogin() {
    const context = useContext(LoginContext);
    if (!context) throw new Error("useLogin() must be used inside <LoginProvider>");
    return context;
}
