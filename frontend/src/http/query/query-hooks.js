import { useMutation } from '@tanstack/react-query'
import { createRoom, joinRoom } from "../request/api-requests.js"

export const useCreateRoom = () => {
    return useMutation({
        mutationFn: ({ roomName, hostName }) =>
            createRoom(roomName, hostName)
    });
}

export const useJoinRoom = () => {
    return useMutation({
        mutationFn: ({ roomId, userName }) =>
            joinRoom(roomId, userName)
    })
}