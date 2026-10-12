import '../../App.css';
import React, {useEffect, useState} from 'react';
import {useLogin} from "../../context/login-context";
import {useSecret} from "../../context/secret-context";

/**
 * Secrets
 * @author Peter Rutschmann
 */
const Secrets = () => {
    const { loginValues } = useLogin();
    const { secrets, loadSecrets } = useSecret();
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const fetchSecrets = () => {
            setErrorMessage('');
            if( ! loginValues.email){
                console.error('Secrets: No valid email, please do login first:' + loginValues);
                setErrorMessage("No valid email, please do login first.");
            } else {
                loadSecrets()
            }
        };
        fetchSecrets();
    }, [loginValues]);

    return (
        <>
            <h1>my secrets</h1>
            {errorMessage && <p style={{color: 'red'}}>{errorMessage}</p>}
             <form>
                <h2>secrets</h2>
                <table border={1}>
                    <thead>
                    <tr>
                        <th>secret id</th>
                        <th>user id</th>
                        <th>content</th>
                    </tr>
                    </thead>
                    <tbody>
                    {secrets?.length > 0 ? (
                        secrets.map(secret => (
                            // TODO: update secret mapping
                            <div>{secret.toString()}</div>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={3}>No secrets available</td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </form>
        </>
    );
};

export default Secrets;