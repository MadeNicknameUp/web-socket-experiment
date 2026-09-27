import axios from "axios";

export const createRoom = async (roomName, hostName) => {
    return axios.post(
        '/api/rooms', 
        { roomName, hostName }
    )
}

export const joinRoom = async (roomId, userName) => {
    return axios.post(
        `/api/rooms/${roomId}/participants`, 
        { userName }
    )
}