import '../../App.css';
import React, {useEffect, useState} from 'react';
import {getSecretsforUser} from "../../comunication/FetchSecrets";
import {Secret} from "../../types/secret";
import {useLogin} from "../../context/login-context";

/**
 * Secrets
 * @author Peter Rutschmann
 */
const Secrets = () => {
    const { loginValues } = useLogin();
    const [secrets, setSecrets] = useState<Secret[]>([]);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const fetchSecrets = async () => {
            setErrorMessage('');
            if( ! loginValues.email){
                console.error('Secrets: No valid email, please do login first:' + loginValues);
                setErrorMessage("No valid email, please do login first.");
            } else {
                try {
                    const data = await getSecretsforUser(loginValues);
                    console.log(data);
                    setSecrets(data);
                    // TODO switch type of error
                } catch (error: any) {
                    console.error('Failed to fetch to server:', error.message);
                    setErrorMessage(error.message);
                }
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