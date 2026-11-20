import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNotificationStore = defineStore('notification', () => {
  const toasts = ref([]);

  function addToast({ type = 'info', title, message, duration = 4500 }) {
    const id = Date.now() + Math.random();
    toasts.value.push({ id, type, title, message });
    if (duration > 0) {
      setTimeout(() => removeToast(id), duration);
    }
  }

  function removeToast(id) {
    toasts.value = toasts.value.filter((t) => t.id !== id);
  }

  const success = (title, message = '') => addToast({ type: 'success', title, message });
  const error = (title, message = '') => addToast({ type: 'error', title, message, duration: 6000 });
  const info = (title, message = '') => addToast({ type: 'info', title, message });

  return { toasts, addToast, removeToast, success, error, info };
});
