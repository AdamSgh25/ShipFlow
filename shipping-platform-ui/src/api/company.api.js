import apiClient from './axios';

export const companyApi = {
  getProfile() {
    return apiClient.get('/api/company/profile');
  },
  updateProfile(payload) {
    return apiClient.put('/api/company/profile', payload);
  },
};
