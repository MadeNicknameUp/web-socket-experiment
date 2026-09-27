import axios from "axios";
import config from "./app-config";

const instance = axios.create({
    baseURL: config.ApiBaseUrl,
    headers: {
        'Content-Type': 'application/json'
    }
})

export default setupYourInterceptors(instance)