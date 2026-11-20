import apiClient from './axios';

export const shipmentApi = {
  getDashboardStats() {
    return apiClient.get('/api/dashboard/stats');
  },
  getShipments(params) {
    return apiClient.get('/api/shipments', { params });
  },
  getShipmentById(id) {
    return apiClient.get(`/api/shipments/${id}`);
  },
  createShipment(payload) {
    return apiClient.post('/api/shipments', payload);
  },
  updateShipment(id, payload) {
    return apiClient.put(`/api/shipments/${id}`, payload);
  },
  updateShipmentStatus(id, payload) {
    return apiClient.patch(`/api/shipments/${id}/status`, payload);
  },
  cancelShipment(id) {
    return apiClient.delete(`/api/shipments/${id}`);
  },
  getAgentShipments(params) {
    return apiClient.get('/api/agent/shipments', { params });
  },
};
