import { httpClient } from "../../config/axios.js";

export const createRoom = async (roomName, hostName) => {
    const response = await httpClient.post(
        '/api/rooms', 
        { roomName, hostName }
    )

    return response.data
}

export const joinRoom = async (roomId, userName) => {
    const response = await httpClient.post(
        `/api/rooms/${roomId}/participants`, 
        { userName }
    )

    return response.data
}