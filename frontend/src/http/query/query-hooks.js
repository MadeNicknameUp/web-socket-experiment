import { useQuery, useMutation } from '@tanstack/react-query'
import { createRoom, joinRoom } from "../request/api-requests.js"

const CREATE_ROOM_QUERY_KEY = 'createRoom'
const JOIN_ROOM_QUERY_KEY = 'joinRoom'

export const useCreateRoom = ({ roomName, hostName }) => {
    return useQuery({
        queryKey: [CREATE_ROOM_QUERY_KEY],
        queryFn: () => createRoom(roomName, hostName)
    })
}

export const useJoinRoom = ({ roomId, userName }) => {
    return useQuery({
        queryKey: [JOIN_ROOM_QUERY_KEY],
        queryFn: () => joinRoom(roomId, userName)
    })
}