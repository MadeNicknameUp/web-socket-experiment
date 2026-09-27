import axios from "axios";
import { config } from "./app-config";

export const httpClientInstance = axios.create({
    baseURL: config.ApiBaseUrl,
    headers: {
        'Content-Type': 'application/json'
    }
});