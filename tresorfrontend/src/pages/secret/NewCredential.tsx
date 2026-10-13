import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import {SecretType} from "../../types/secret-type";
import {CredentialSecret} from "../../types/secret";
import {useLogin} from "../../context/login-context";
import {useSecret} from "../../context/secret-context";

/**
 * NewCredential
 * @author Peter Rutschmann
 */
function NewCredential() {
    const { checkLogin, loginValues } = useLogin();
    const { addSecret } = useSecret();
    const initialState: CredentialSecret = {
        username: "",
        password: "",
        url: ""
    };
    const [credentialValues, setCredentialValues] = useState<CredentialSecret>(initialState);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    // TODO: change error type
    const handleSubmit = async (e: any) => {
        e.preventDefault();
        setErrorMessage('');
        console.log(loginValues)
        try {
            await addSecret(SecretType.CREDENTIAL, credentialValues);
            setCredentialValues(initialState);
            navigate('/secret/secrets');
        } catch (error: any) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
        }
    };

    useEffect(() => checkLogin(), [])

    return (
        <div>
            <h2>Add new credential secret</h2>
            <form onSubmit={handleSubmit}>
                <section>
                    <aside>
                        <div>
                            <label>username:</label>
                            <input
                                type="text"
                                value={credentialValues.username}
                                onChange={(e) =>
                                    setCredentialValues(prevValues => ({...prevValues, username: e.target.value}))}
                                required
                                placeholder="Please enter username"
                            />
                        </div>
                        <div>
                            <label>password:</label>
                            <input
                                type="text"
                                value={credentialValues.password}
                                onChange={(e) => {
                                    console.log(`pwd value: ${e.target.value}`)
                                    setCredentialValues(prevValues => ({...prevValues, password: e.target.value}))
                                }}
                                required
                                placeholder="Please enter password"
                            />
                        </div>
                        <div>
                            <label>url:</label>
                            <input
                                type="text"
                                value={credentialValues.url}
                                onChange={(e) =>
                                    setCredentialValues(prevValues => ({...prevValues, url: e.target.value}))}
                                required
                                placeholder="Please enter url"
                            />
                        </div>
                        <button type="submit">save secret</button>
                        {errorMessage && <p style={{color: 'red'}}>{errorMessage}</p>}
                    </aside>
                </section>
            </form>
        </div>
    );
}

export default NewCredential;
