/**
 * Fetch methodes for user api calls
 * @author Peter Rutschmann
 */
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
