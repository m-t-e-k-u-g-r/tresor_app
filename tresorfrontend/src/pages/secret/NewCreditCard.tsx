import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {SecretType} from "../../types/secret-type";
import {CreateCreditCardSecret} from "../../types/secret";
import {useSecret} from "../../context/secret-context";

/**
 * NewCreditCard
 * @author Peter Rutschmann
 */
function NewCreditCard() {
    const { addSecret, editSecret, editCreditCard } = useSecret();
    const initialState: CreateCreditCardSecret = editCreditCard ?? {
        cardType: "",
        cardNumber: "",
        expiration: "",
        cvv: ""
    };
    const [creditCardValues, setCreditCardValues] = useState<CreateCreditCardSecret>(initialState);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    // TODO change error type
    const handleSubmit = async (e: any) => {
        e.preventDefault();
        setErrorMessage('');
        try {
            if (editCreditCard) {
                await editSecret(SecretType.CREDIT_CARD, editCreditCard.id, creditCardValues)
            } else {
                await addSecret(SecretType.CREDIT_CARD, creditCardValues);
            }
            setCreditCardValues(initialState);
            navigate('/secret/secrets');
        } catch (error: any) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
        }
    };

    function header() {
        if (editCreditCard == null) {
            return <h2>Add new credit-card secret</h2>
        } else {
            return <h2>Update credit-card secret { editCreditCard.id }</h2>
        }
    }

    return (
        <div>
            { header() }
            <form onSubmit={handleSubmit}>
                <section>
                    <aside>
                        <div>
                            <label>card type:</label>
                            <select
                                value={creditCardValues.cardType}
                                onChange={(e) =>
                                    setCreditCardValues((prevValues) => ({
                                        ...prevValues,
                                        cardType: e.target.value,
                                    }))}
                                required
                            >
                                <option value="" disabled>
                                    Please select card type
                                </option>
                                <option value="Visa">Visa</option>
                                <option value="Mastercard">Mastercard</option>
                            </select>
                        </div>
                        <div>
                            <label>cardnumber:</label>
                            <input
                                type="text"
                                value={creditCardValues.cardNumber}
                                onChange={(e) =>
                                    setCreditCardValues(prevValues => ({...prevValues, cardNumber: e.target.value}))}
                                required
                                placeholder="Please enter cardnumber"
                            />
                        </div>
                        <div>
                            <label>expiration (mm/yy):</label>
                            <input
                                type="text"
                                value={creditCardValues.expiration}
                                onChange={(e) =>
                                    setCreditCardValues(prevValues => ({...prevValues, expiration: e.target.value}))}
                                required
                                placeholder="Please enter expiration"
                            />
                        </div>
                        <div>
                            <label>cvv:</label>
                            <input
                                type="text"
                                value={creditCardValues.cvv}
                                onChange={(e) =>
                                    setCreditCardValues(prevValues => ({...prevValues, cvv: e.target.value}))}
                                required
                                placeholder="Please enter cvv"
                            />
                        </div>
                        <button type="submit">Save secret</button>
                        {errorMessage && <p style={{color: 'red'}}>{errorMessage}</p>}
                    </aside>
                </section>
            </form>
        </div>
    );
}

export default NewCreditCard;
