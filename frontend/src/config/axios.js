import axios from "axios";
import { config } from "./app-config";

export const httpClient = axios.create({
    baseURL: config.ApiBaseUrl,
    headers: {
        'Content-Type': 'application/json'
    }
});