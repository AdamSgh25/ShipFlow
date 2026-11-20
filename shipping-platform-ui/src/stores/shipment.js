import { defineStore } from 'pinia';
import { ref } from 'vue';
import { shipmentApi } from '@/api/shipment.api';
import { useNotificationStore } from './notification';

export const useShipmentStore = defineStore('shipment', () => {
  const shipments = ref([]);
  const currentShipment = ref(null);
  const pagination = ref({ page: 0, size: 10, totalElements: 0, totalPages: 0 });
  const loading = ref(false);
  const error = ref(null);

  const notify = useNotificationStore();

  async function fetchShipments(params = {}, silent = false) {
    if (!silent) loading.value = true;
    error.value = null;
    try {
      const { data } = await shipmentApi.getShipments(params);
      const list = data.content || data;
      shipments.value = list;
      if (data.totalElements !== undefined) {
        pagination.value = {
          page: data.number ?? params.page ?? 0,
          size: data.size ?? params.size ?? 10,
          totalElements: data.totalElements,
          totalPages: data.totalPages,
        };
      }
    } catch (err) {
      error.value = err.userMessage;
    } finally {
      if (!silent) loading.value = false;
    }
  }

  async function fetchAgentShipments(params = {}, silent = false) {
    if (!silent) loading.value = true;
    error.value = null;
    try {
      const { data } = await shipmentApi.getAgentShipments(params);
      const prevMap = new Map(shipments.value.map((s) => [s.id, s.status]));
      const nextList = data.content || data;

      if (silent && prevMap.size > 0) {
        nextList.forEach((item) => {
          const oldStatus = prevMap.get(item.id);
          if (oldStatus && oldStatus !== item.status) {
            notify.info(
              'Shipment Updated',
              `${item.trackingNumber} changed from ${oldStatus} to ${item.status}`
            );
          }
        });
      }

      shipments.value = nextList;
      if (data.totalElements !== undefined) {
        pagination.value = {
          page: data.number ?? params.page ?? 0,
          size: data.size ?? params.size ?? 10,
          totalElements: data.totalElements,
          totalPages: data.totalPages,
        };
      }
    } catch (err) {
      error.value = err.userMessage;
    } finally {
      if (!silent) loading.value = false;
    }
  }

  async function fetchShipmentById(id, silent = false) {
    if (!silent) loading.value = true;
    error.value = null;
    try {
      const oldStatus = currentShipment.value?.status;
      const { data } = await shipmentApi.getShipmentById(id);
      if (silent && oldStatus && oldStatus !== data.status) {
        notify.info('Status Updated', `Shipment ${data.trackingNumber} is now ${data.status}`);
      }
      currentShipment.value = data;
      return data;
    } catch (err) {
      error.value = err.userMessage;
      throw err;
    } finally {
      if (!silent) loading.value = false;
    }
  }

  return {
    shipments,
    currentShipment,
    pagination,
    loading,
    error,
    fetchShipments,
    fetchAgentShipments,
    fetchShipmentById,
  };
});
