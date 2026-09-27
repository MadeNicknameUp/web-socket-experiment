import { useCreateRoom } from "../http/query/query-hooks.js";
import { useState } from "react";

export const CreateRoom = () => {

    const [roomName, setRoomName] = useState("");
    const [hostName, setHostName] = useState("");

    let mutation = useCreateRoom();

    function handleSubmit(event) {
        event.preventDefault();

        mutation.mutate({
            roomName,
            hostName
        });
    }

    return (
        <section className="create-room-section">
            <form className="create-room-form" onSubmit={handleSubmit}>
                <input
                    id="roomName"
                    type="text"
                    placeholder="Room Name"
                    value={roomName}
                    onChange={(event) => setRoomName(event.target.value)}
                />
                <input
                    id="hostName"
                    type="text"
                    placeholder="Host Name"
                    value={hostName}
                    onChange={(event) => setHostName(event.target.value)}
                />
                <button
                    type="submit"
                    disabled={mutation.isPending}
                >{mutation.isPending ? "Creating..." : "Create Room"}</button>
            </form>

            {mutation.isError && (
                <p className="errorMessage">Something went wrong.</p>
            )}

            {mutation.isSuccess && (
                <p className="successMessage">Room created: {mutation.data.roomId}</p>
            )}
        </section>
    ) 
}