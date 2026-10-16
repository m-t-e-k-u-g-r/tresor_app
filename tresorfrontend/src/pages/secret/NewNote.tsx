import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {NoteSecret} from "../../types/secret";
import {SecretType} from "../../types/secret-type";
import {useSecret} from "../../context/secret-context";

/**
 * NewNote
 * @author Peter Rutschmann
 */
function NewNote() {
    const { addSecret } = useSecret();
    const initialState: NoteSecret = {
        title: "",
        content: "",
    };
    const [noteValues, setNoteValues] = useState<NoteSecret>(initialState);
    const [errorMessage, setErrorMessage] = useState('');

    const navigate = useNavigate();

    // TODO: change error type
    const handleSubmit = async (e: any) => {
        e.preventDefault();
        setErrorMessage('');
        try {
            await addSecret(SecretType.NOTE, noteValues);
            setNoteValues(initialState);
            navigate('/secret/secrets');
            // TODO: change error type
        } catch (error: any) {
            console.error('Failed to fetch to server:', error.message);
            setErrorMessage(error.message);
        }
    };

    return (
        <div>
            <h2>Add new note secret</h2>
            <form onSubmit={handleSubmit}>
                <section>
                    <aside>
                        <div>
                            <label>title:</label>
                            <input
                                type="text"
                                value={noteValues.title}
                                onChange={(e) =>
                                    setNoteValues(prevValues => ({...prevValues, title: e.target.value}))}
                                required
                                placeholder="Please enter a title *"
                            />
                        </div>
                        <div>
                            <label>content:</label>
                            <textarea
                                rows={4}
                                style={{
                                    resize: 'both', // Ermöglicht Größenänderung in beide Richtungen
                                    width: '24%', // Standardbreite (kann angepasst werden)
                                    minWidth: '190px', // Minimale Breite
                                    minHeight: '100px', // Minimale Höhe
                                }}
                                value={noteValues.content}
                                onChange={(e) =>
                                    setNoteValues(prevValues =>
                                        ({...prevValues, content: e.target.value}))}
                                required
                                placeholder="Please enter a content *"
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

export default NewNote;
