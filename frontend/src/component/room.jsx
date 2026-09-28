import {useState} from "react";

export const Room = () => {

    const [message, setMessage] = useState()

    return (
        <section className="room-section">
            <form className="room-section-form" onSubmit={}>
                <input
                    className="message-input"
                    type="json"
                    placeholder="Start typping..."
                    value={message}
                    onChange={(event) => setMessage(event.target.value)}
                />
                <button
                    type="submit"
                    className="submit-message-button"
                >

                </button>
            </form>
        </section>
    );
}