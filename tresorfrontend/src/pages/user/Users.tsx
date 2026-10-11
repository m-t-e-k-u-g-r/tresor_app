import '../../App.css';
import React, {useEffect, useState} from "react";
import {getUsers} from "../../comunication/FetchUser";
import {User} from "../../types/user";
import {LoginProps} from "../../types/login-props";

/**
 * Users
 * @author Peter Rutschmann
 */
const Users = ({ loginValues }: LoginProps) => {
    const [users, setUsers] = useState<User[]>([]);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const users = await getUsers();
                console.log(users);
                setUsers(users);
                // TODO: change error type
            } catch (error: any) {
                console.error('Failed to fetch to server:', error.message);
                setErrorMessage(error.message);
            }
        };
        fetchUsers();
    }, [loginValues]);

    return (
        <>
            <h1>Client list</h1>
            <ul>
                {users.map(user => (
                    <li key={user.email}>{user.firstName} {user.lastName} - {user.email}</li>
                ))}
            </ul>
            {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
        </>
    );
};

export default Users;