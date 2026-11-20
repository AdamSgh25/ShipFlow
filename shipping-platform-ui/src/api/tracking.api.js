import apiClient from './axios';

export const trackingApi = {
  trackShipment(trackingNumber) {
    return apiClient.get(`/api/shipments/track/${encodeURIComponent(trackingNumber.trim())}`);
  },
};
