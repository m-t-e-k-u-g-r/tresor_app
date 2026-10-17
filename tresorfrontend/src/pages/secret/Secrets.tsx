import '../../App.css';
import React, {useEffect} from 'react';
import {useLogin} from "../../context/login-context";
import {useSecret} from "../../context/secret-context";
import {CredentialSecret, CreditCardSecret, NoteSecret, Secret} from "../../types/secret";
import {SquarePen, Trash2} from "lucide-react";
import {SecretType} from "../../types/secret-type";

/**
 * Secrets
 * @author Peter Rutschmann
 */
const Secrets = () => {
    const { checkLogin } = useLogin();
    const { secrets, loadSecrets, setEditSecret, deleteSecret } = useSecret();
    const credentials = secrets.filter(isCredentialSecret);
    const creditCards = secrets.filter(isCreditCardSecret);
    const notes = secrets.filter(isNoteSecret);

    function isCredentialSecret(secret: Secret): secret is CredentialSecret {
        return 'username' in secret && 'password' in secret && 'url' in secret;
    }

    function isCreditCardSecret(secret: Secret): secret is CreditCardSecret {
        return 'cardType' in secret && 'cardNumber' in secret && 'expiration' in secret && 'cvv' in secret;
    }

    function isNoteSecret(secret: Secret): secret is NoteSecret {
        return 'title' in secret && 'content' in secret;
    }

    useEffect(() => {
        const load = async () => {
            await checkLogin();
            await loadSecrets();
        }
        load();
    }, []);

    return (
        <>
            <h1>my secrets</h1>

            <div>
                <div className="table-container">
                    <h2>Credentials</h2>
                    <table border={1}>
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Username</th>
                            <th>Password</th>
                            <th>URL</th>
                            <th>Edit</th>
                            <th>Delete</th>
                        </tr>
                        </thead>
                        <tbody>
                        {credentials?.length > 0 ? (
                            credentials.map((secret: CredentialSecret) => (
                                <tr key={secret.id}>
                                    <td>{secret.id}</td>
                                    <td>{secret.username}</td>
                                    <td>{secret.password}</td>
                                    <td>{secret.url}</td>
                                    <td><button onClick={() => setEditSecret(SecretType.CREDENTIAL, secret)}>
                                        <SquarePen color="yellow" size={15} />
                                    </button></td>
                                    <td><button onClick={() => deleteSecret(SecretType.CREDENTIAL, secret.id)}>
                                        <Trash2 color="red" size={15} />
                                    </button></td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={5}>No credentials available</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>

                <div className="table-container">
                    <h2>Credit Cards</h2>
                    <table border={1}>
                        <thead>
                        <tr>
                            <th>Id</th>
                            <th>Card Type</th>
                            <th>Card Number</th>
                            <th>Expiration</th>
                            <th>CVV</th>
                            <th>Delete</th>
                        </tr>
                        </thead>
                        <tbody>
                        {creditCards?.length > 0 ? (
                            creditCards.map((secret: CreditCardSecret) => (
                                <tr key={secret.id}>
                                    <td>{secret.id}</td>
                                    <td>{secret.cardType}</td>
                                    <td>{secret.cardNumber}</td>
                                    <td>{secret.expiration}</td>
                                    <td>{secret.cvv}</td>
                                    <td><button onClick={() => setEditSecret(SecretType.CREDIT_CARD, secret)}>
                                        <SquarePen color="yellow" size={15} />
                                    </button></td>
                                    <td><button onClick={() => deleteSecret(SecretType.CREDIT_CARD, secret.id)}>
                                        <Trash2 color="red" size={15} />
                                    </button></td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={6}>No credit cards available</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>

                <div className="table-container">
                    <h2>Notes</h2>
                    <table border={1}>
                        <thead>
                            <tr>
                                <th>Id</th>
                                <th>Title</th>
                                <th>Content</th>
                                <th>Delete</th>
                            </tr>
                        </thead>
                        <tbody>
                        {notes?.length > 0 ? (
                            notes.map((secret: NoteSecret) => (
                                <tr key={secret.id}>
                                    <td>{secret.id}</td>
                                    <td>{secret.title}</td>
                                    <td>{secret.content}</td>
                                    <td><button onClick={() => setEditSecret(SecretType.NOTE, secret)}>
                                        <SquarePen color="yellow" size={15} />
                                    </button></td>
                                    <td><button onClick={() => deleteSecret(SecretType.NOTE, secret.id)}>
                                        <Trash2 color="red" size={15} />
                                    </button></td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={4}>No notes available</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        </>

    );
};

export default Secrets;