import { useCreateRoom } from "../http/query/query-hooks.js";
import {useNavigate} from "react-router";
import { useState } from "react";

export const CreateRoom = () => {

    const [roomName, setRoomName] = useState("");
    const [hostName, setHostName] = useState("");

    const createRoomMutation = useCreateRoom();

    const navigate = useNavigate();

    function handleSubmit(event) {
        event.preventDefault();

        createRoomMutation.mutate({
            roomName,
            hostName
        }, {
            onSuccess: (data) => {
                navigate(`/rooms/${data.roomId}`)
            }
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
                    disabled={createRoomMutation.isPending}
                >{createRoomMutation.isPending ? "Creating..." : "Create Room"}</button>
            </form>

            {createRoomMutation.isError && (
                <p className="errorMessage">Something went wrong.</p>
            )}
        </section>
    ) 
}