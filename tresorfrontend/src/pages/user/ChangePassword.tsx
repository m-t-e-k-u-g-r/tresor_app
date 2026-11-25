import {useLogin} from "../../context/login-context";
import {getApiUrl} from "../../comunication/api-url";
import React, {ChangeEvent, useState} from "react";
import {evaluatePasswordStrength} from "../../password-checker";
import PasswordStrength from "../../password-strength";


export default function ChangePassword() {
    const { loginValues } = useLogin();
    const [errorMessage, setErrorMessage] = useState<string>('');
    const [passwords, setPasswords] = useState<{ oldPassword: string, newPassword: string, confirmPassword: string }>({ oldPassword: '', newPassword: '', confirmPassword: '' });
    const [passwordStrength, setPasswordStrength] = useState<number>(0);
    const [passwordConfirmStrength, setPasswordConfirmStrength] = useState<number>(0);

    function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        const { name, value } = e.target;

        setPasswords(prev => ({
            ...prev,
            [name]: value
        }));
    }

    const handlePasswordChange = (e: ChangeEvent<HTMLInputElement>) => {
        const password = e.target.value;

        setPasswords(prev => ({
            ...prev,
            newPassword: password
        }));

        const result = evaluatePasswordStrength(password);
        setPasswordStrength(result);

        if (passwords.confirmPassword) {
            const matches = password === passwords.confirmPassword;

            setPasswordConfirmStrength(
                matches ? evaluatePasswordStrength(passwords.confirmPassword) : 0
            );
        }

        setErrorMessage('');
    };

    const handlePasswordConfirmChange = (e: ChangeEvent<HTMLInputElement>) => {
        const confirmPwd = e.target.value;

        setPasswords(prev => ({
            ...prev,
            confirmPassword: confirmPwd
        }));

        setErrorMessage('');

        let result = evaluatePasswordStrength(confirmPwd);

        if (confirmPwd !== passwords.newPassword) {
            result = 0;
            setErrorMessage('Passwords do not match');
        }

        setPasswordConfirmStrength(result);
    };

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        const { oldPassword, newPassword, confirmPassword } = passwords;

        if (oldPassword === newPassword)
            return alert('New password cannot be the same as the old password');

        if (newPassword !== confirmPassword)
            return alert('New password and confirm password do not match');

        const { email } = loginValues;

        const response = await fetch(getApiUrl() + '/auth/change-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, oldPassword, newPassword }),
        });

        switch (response.status) {
            case 200:
                setErrorMessage('');
                setPasswords({
                    oldPassword: '',
                    newPassword: '',
                    confirmPassword: ''
                });
                break;
            case 403:
                setErrorMessage('Invalid password');
                break;
            default:
                setErrorMessage('An error occurred');
                break;
        }
    }

    async function handleDeleteAccount() {
        const response = await fetch(getApiUrl() + '/auth/account/delete', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                Authorization: 'Bearer ' + localStorage.getItem('token')
            }
        })
        if (response.ok) {
            localStorage.removeItem('token');
            window.location.href = '/';
        } else {
            setErrorMessage('An error occurred');
        }
    }

    return (
        <div>
            <h2>Change password</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="oldPassword">Old password</label>
                    <input
                        type="password"
                        id="oldPassword"
                        name="oldPassword"
                        value={passwords.oldPassword}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <div>
                        <label htmlFor="newPassword">New password</label>
                        <input
                            type="password"
                            id="newPassword"
                            name="newPassword"
                            value={passwords.newPassword}
                            onChange={handlePasswordChange}
                            required
                        />
                        <PasswordStrength strength={passwordStrength} />
                    </div>
                    <div>
                        <label htmlFor="confirmPassword">Confirm new password</label>
                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            value={passwords.confirmPassword}
                            onChange={handlePasswordConfirmChange}
                            required
                        />
                        <PasswordStrength strength={passwordConfirmStrength} />
                    </div>
                </div>

                <button type="submit">Change password</button>
            </form>
            <p>{errorMessage}</p>

            <h2>Delete Account</h2>
            <p>Warning: this can not be undone!!!</p>
            <button onClick={handleDeleteAccount}>Delete</button>
        </div>
    )
}