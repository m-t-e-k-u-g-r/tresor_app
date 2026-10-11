/**
 * Fetch methodes for user api calls
 * @author Peter Rutschmann
 */
import {Credentials} from "../types/credentials";
import {Login} from "../types/user";
import {getApiUrl} from "./api-url";


export const getUsers = async () => {
    const API_URL = getApiUrl();

    try {
        const response = await fetch(`${API_URL}/users`, {
            method: 'Get',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Server response failed.');
        }

        const data = await response.json();
        console.log('User successfully got:', data);
        return data;
        // TODO: replace error type
    } catch (error: any) {
        console.error('Failed to get user:', error.message);
        throw new Error('Failed to get user. ' || error.message);
    }
}

export const postUser = async (content: Credentials) => {
    const API_URL = getApiUrl();

    try {
        const response = await fetch(`${API_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                firstName: `${content.firstName}`,
                lastName: `${content.lastName}`,
                email: `${content.email}`,
                password: `${content.password}`,
                passwordConfirmation: `${content.passwordConfirmation}`
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Server response failed.');
        }
        const data = await response.json();
        console.log('User successfully posted:', data);
        return data;
    } catch (error: any) {
        console.error('Failed to post user:', error.message);
        throw new Error('Failed to save user. ' || error.message);
    }
};

export const postUserLogin = async (content: Login) => {
    const API_URL = getApiUrl();

    try {
        const response = await fetch(`${API_URL}/users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: `${content.email}`,
                password: `${content.password}`
            })
        });
        console.log(response);
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Server response failed.');
        }
        const data = await response.json();
        console.log('User successfully logged in:', data);
        return data;
    } catch (error: any) {
        console.error('Failed to login user:', error.message);
        throw new Error('Failed to login user. ' + error.message);
    }
};