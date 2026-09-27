import { useCreateRoom } from "../http/query/query-hooks.js";

export const CreateRoom = () => {

    function onClick(roomName, hostName) {
        console.log(roomName, hostName);
    }

    return (
        <section className="create-room-section">
            <form className="create-room-form">
                <input id="roomName" type="text" placeholder="Room Name" />
                <input id="hostName" type="text" placeholder="Host Name" />
                <button type="submit" onClick={ () => onClick(
                    document.getElementById('roomName').value, 
                    document.getElementById('hostName').value
                    )}>Create Room</button>
            </form>
        </section>
    ) 
}