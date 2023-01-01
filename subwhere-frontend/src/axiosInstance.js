import axios from 'axios';

let axioss = axios.create({
  baseURL: `https://api.ottitor.shop`,
  headers: { 'Content-Type': 'application/json' },
});

export const axiosInstance = axioss;
