import { onMounted, onUnmounted, ref } from 'vue';

export function useAutoRefresh(fetchFn, intervalMs = Number(import.meta.env.VITE_POLLING_INTERVAL_MS) || 15000) {
  const isPolling = ref(false);
  const lastUpdated = ref(null);
  let timer = null;

  const execute = async () => {
    if (document.hidden || isPolling.value) return;
    isPolling.value = true;
    try {
      await fetchFn(true);
      lastUpdated.value = new Date();
    } finally {
      isPolling.value = false;
    }
  };

  const start = () => {
    stop();
    timer = setInterval(execute, intervalMs);
  };

  const stop = () => {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  };

  onMounted(() => {
    lastUpdated.value = new Date();
    start();
  });

  onUnmounted(() => {
    stop();
  });

  return { isPolling, lastUpdated, start, stop };
}
