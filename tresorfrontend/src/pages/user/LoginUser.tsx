import { useNavigate } from 'react-router-dom';
import React, {useState} from "react";
import {postUserLogin} from "../../comunication/FetchUser";
import {useLogin} from "../../context/login-context";

/**
 * LoginUser
 * @author Peter Rutschmann
 */

function LoginUser() {
    const { loginValues, updateLogin } = useLogin();
    const navigate = useNavigate();
    const [errorMessage, setErrorMessage] = useState('');

    const handleSubmit = async (e: any) => {
        e.preventDefault();
        console.log(loginValues);
        setErrorMessage('');

        try {
            await postUserLogin(loginValues);
            navigate('/');
            // TODO: change error type
        } catch (error: any) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
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

export default LoginUser;