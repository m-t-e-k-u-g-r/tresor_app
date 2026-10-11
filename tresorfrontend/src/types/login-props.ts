import {Login} from "./user";
import {Dispatch, SetStateAction} from "react";

export interface LoginProps {
    loginValues: Login;
}

export interface LoginUserProps extends LoginProps {
    setLoginValues: Dispatch<SetStateAction<Login>>;
}