import {useJoinRoom} from "../http/query/query-hooks.js";
import {useParams} from "react-router";

export const JoinRoom = () => {

    const { roomId } = useParams();

    const joinRoomMutation = useJoinRoom()

    return <h1>Join Room Component</h1>;
}