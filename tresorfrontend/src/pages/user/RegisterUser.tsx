import React, {ChangeEvent, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import {useLogin} from "../../context/login-context";
import {SignupForm} from "../../types/signup-request";
import ReCAPTCHA from "react-google-recaptcha";
import {evaluatePasswordStrength} from "../../password-checker";
import PasswordStrength from "../../password-strength";

/**
 * RegisterUser
 * @author Peter Rutschmann
 */
function RegisterUser() {
    const { signup } = useLogin();
    const navigate = useNavigate();
    const recaptcha_key = "6LcMHBAuAAAAAMovy_u21huzVxdYFxjmmo-D9BkW";

    const initialState: SignupForm = {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        passwordConfirmation: "",
        errorMessage: ""
    };
    const [captchaValue, setCaptchaValue] = useState<string | null>(null);
    const [credentials, setCredentials] = useState<SignupForm>(initialState);
    const [errorMessage, setErrorMessage] = useState('');
    const [validEmail, setValidEmail] = useState(false);
    const [passwordStrength, setPasswordStrength] = useState<number>(0);
    const [passwordConfirmStrength, setPasswordConfirmStrength] = useState<number>(0);

    const handleEmailChange = (e: ChangeEvent<HTMLInputElement>) => {
        setCredentials(prevValues => ({...prevValues, email: e.target.value}))

        if (e.target.validity.typeMismatch) {
            setValidEmail(false);
        } else {
            setValidEmail(true);
        }
    };

    const handlePasswordChange = (e: ChangeEvent<HTMLInputElement>) => {
        setCredentials(prevValues => ({...prevValues, password: e.target.value}))

        const result = evaluatePasswordStrength(e.target.value);
        setPasswordStrength(result);
    }

    const handlePasswordConfirmChange = (e: ChangeEvent<HTMLInputElement>) => {
        const confirmPwd = e.target.value;
        setCredentials(prevValues => ({...prevValues, passwordConfirmation: confirmPwd}))

        setErrorMessage('');

        let result = evaluatePasswordStrength(confirmPwd);
        if (confirmPwd !== credentials.password) {
            result = 0;
            setErrorMessage('Passwords do not match');
        }

        setPasswordConfirmStrength(result);
    }

    const handleSubmit = async (e: any) => {
        e.preventDefault();
        setErrorMessage('');

        if (!captchaValue) return setErrorMessage("Please complete the reCAPTCHA");

        if (!validEmail) return setErrorMessage('Please enter a valid email address.');

        //validate
        if(credentials.password !== credentials.passwordConfirmation)
            return setErrorMessage('Password and password-confirmation are not equal.');

        if (!credentials.password.match(/^(?=.*?[0-9])(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^0-9A-Za-z]).{8,32}$/))
            return setErrorMessage("Password is not strong enough");

        const success = await signup({ ...credentials, captchaToken: captchaValue });
        if (success) {
            setCredentials(initialState);
            navigate('/');
        } else {
            setErrorMessage("Failed to sign up")
        }
    };

    return (
        <div>
            <h2>Register user</h2>
            <form onSubmit={handleSubmit}>
                <section>
                    <aside>
                        <div>
                            <label>Firstname:</label>
                            <input
                                type="text"
                                value={credentials.firstName}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({...prevValues, firstName: e.target.value}))}
                                required
                                placeholder="Please enter your firstname *"
                            />
                        </div>
                        <div>
                            <label>Lastname:</label>
                            <input
                                type="text"
                                value={credentials.lastName}
                                onChange={(e) =>
                                    setCredentials(prevValues => ({...prevValues, lastName: e.target.value}))}
                                required
                                placeholder="Please enter your lastname *"
                            />
                        </div>
                        <div>
                            <label>Email:</label>
                            <input
                                type="email"
                                value={credentials.email}
                                onChange={(e) => handleEmailChange(e)}
                                required
                                placeholder="Please enter your email"
                            />
                        </div>
                    </aside>
                    <aside>
                        <div>
                            <label>Password:</label>
                            <input
                                type="text"
                                value={credentials.password}
                                onChange={(e) => handlePasswordChange(e)}
                                required
                                placeholder="Please enter your pwd *"
                            />
                            <PasswordStrength strength={passwordStrength}/>
                        </div>
                        <div>
                            <label>Password confirmation:</label>
                            <input
                                type="text"
                                value={credentials.passwordConfirmation}
                                onChange={(e) => handlePasswordConfirmChange(e)}
                                required
                                placeholder="Please confirm your pwd *"
                            />
                            <PasswordStrength strength={passwordConfirmStrength}/>
                        </div>
                        <ReCAPTCHA sitekey={recaptcha_key} onChange={(v) => setCaptchaValue(v)} />
                    </aside>
                </section>
                <button type="submit">Register</button>
                {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
            </form>
        </div>
    );
}

export default RegisterUser;
