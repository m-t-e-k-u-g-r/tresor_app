import { useNavigate } from 'react-router-dom';
import React, {useState} from "react";
import {useLogin} from "../../context/login-context";

/**
 * LoginUser
 * @author Peter Rutschmann
 */

export default function LoginUser() {
    const { loginValues, updateLogin, login } = useLogin();
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');

    const handleSubmit = async (e: any) => {
        e.preventDefault();
        console.log(loginValues);
        setErrorMessage('');

        const success = await login();
        if (success) {
            navigate('/');
        } else {
            setErrorMessage("Failed to log in");
        }
    };

    return (
        <div>
            <h2>Login user</h2>
            <form onSubmit={handleSubmit}>
                <section>
                    <aside>
                        <div>
                            <label>Email:</label>
                            <input
                                type="text"
                                value={loginValues.email}
                                onChange={(e) => updateLogin({ ...loginValues, email: e.target.value })}
                                required
                                placeholder="Please enter your email *"
                            />
                        </div>
                        <div>
                            <label>Password:</label>
                            <input
                                type="text"
                                value={loginValues.password}
                                onChange={(e) => updateLogin({ ...loginValues, password: e.target.value })}
                                required
                                placeholder="Please enter your password *"
                            />
                        </div>
                    </aside>
                </section>
                <button type="submit">Login</button>
                <p>{errorMessage}</p>
            </form>
        </div>
    );
}
