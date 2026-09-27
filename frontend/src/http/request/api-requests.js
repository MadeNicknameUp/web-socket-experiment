import { httpClientInsatce } from "src/config/axios.js";

const httpClient = httpClientInstance();

export const createRoom = async (roomName, hostName) => {
    return httpClient.post(
        '/api/rooms', 
        { roomName, hostName }
    )
}

export const joinRoom = async (roomId, userName) => {
    return httpClient.post(
        `/api/rooms/${roomId}/participants`, 
        { userName }
    )
}