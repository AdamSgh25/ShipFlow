import apiClient from './axios';

export const deliveryAgentApi = {
  getAgents(params) {
    return apiClient.get('/api/delivery-agents', { params });
  },
  getAgentById(id) {
    return apiClient.get(`/api/delivery-agents/${id}`);
  },
  createAgent(payload) {
    return apiClient.post('/api/delivery-agents', payload);
  },
  updateAgent(id, payload) {
    return apiClient.put(`/api/delivery-agents/${id}`, payload);
  },
  toggleAgentStatus(id, active) {
    return apiClient.patch(`/api/delivery-agents/${id}/status`, { active });
  },
};
