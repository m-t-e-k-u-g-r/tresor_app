
export interface SignupRequest {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    passwordConfirmation: string;
}

export interface SignupForm extends SignupRequest {
    errorMessage: string;
}
