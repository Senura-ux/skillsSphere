// src/services/userService.js
import api from "./api";
import axios from "axios";

const UserService = {
  register: (userData) => {
    return api.post("/users/register", userData);
  },
  
  login: (credentials) => {
    return api.post("/users/login", credentials);
  },
  
  getCurrentUser: async () => {
    const token = localStorage.getItem('token');
    return axios.get('/api/users/me', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  },
  
  updateProfile: (userId, userData) => {
    return api.put(`/users/${userId}`, userData);
  },
  
  getUserProfile: (userId) => {
    return api.get(`/users/${userId}`);
  },
  
  getUserByUsername: (username) => {
    return api.get(`/users/username/${username}`);
  },
  
  getUserPosts: (userId) => {
    return api.get(`/users/${userId}/posts`);
  }
};

export default UserService;