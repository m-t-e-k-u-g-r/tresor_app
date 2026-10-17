import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {SecretType} from "../../types/secret-type";
import {CreateCredentialSecret} from "../../types/secret";
import {useLogin} from "../../context/login-context";
import {useSecret} from "../../context/secret-context";

/**
 * NewCredential
 * @author Peter Rutschmann
 */
function NewCredential() {
    const { loginValues } = useLogin();
    const { addSecret, editSecret, editCredential } = useSecret();
    const initialState: CreateCredentialSecret = editCredential ?? {
        username: "",
        password: "",
        url: ""
    };
    const [credentialValues, setCredentialValues] = useState<CreateCredentialSecret>(initialState);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    // TODO: change error type
    const handleSubmit = async (e: any) => {
        e.preventDefault();
        setErrorMessage('');
        console.log(loginValues)
        try {
            if (editCredential) {
                await editSecret(SecretType.CREDENTIAL, editCredential.id, credentialValues);
            } else {
                await addSecret(SecretType.CREDENTIAL, credentialValues);
            }
            setCredentialValues(initialState);
            navigate('/secret/secrets');
        } catch (error: any) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
        }
    };

    function header() {
        if (editCredential == null) {
            return <h2>Add new credential secret</h2>
        } else {
            return <h2>Update credential secret { editCredential.id }</h2>
        }
    }

    return (
        <div>
            { header() }
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
