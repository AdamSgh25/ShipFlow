import apiClient from './axios';

export const authApi = {
  login(credentials) {
    return apiClient.post('/api/auth/login', credentials);
  },
  refresh() {
    return apiClient.post('/api/auth/refresh');
  },
  logout() {
    return apiClient.post('/api/auth/logout');
  },
  me() {
    return apiClient.get('/api/auth/me');
  },
};
