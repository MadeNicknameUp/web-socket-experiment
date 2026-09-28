import { BrowserRouter, Routes, Route } from "react-router"
import { CreateRoom } from "../component/create-room.jsx"
import { JoinRoom } from "../component/join-room.jsx"

export const Router = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route index element={<CreateRoom />} />
                <Route path="/rooms/:roomId" element={<JoinRoom />} />
            </Routes>
        </BrowserRouter>
    )
}