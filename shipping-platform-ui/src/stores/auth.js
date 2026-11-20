import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { authApi } from '@/api/auth.api';

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null);
  const initialized = ref(false);
  const loading = ref(false);

  const isAuthenticated = computed(() => !!user.value);
  const isAdmin = computed(() => user.value?.role === 'ADMIN' || user.value?.role === 'ROLE_ADMIN');
  const isAgent = computed(
    () => user.value?.role === 'DELIVERY_AGENT' || user.value?.role === 'ROLE_DELIVERY_AGENT'
  );

  async function login(credentials) {
    loading.value = true;
    try {
      const { data } = await authApi.login(credentials);
      if (data.accessToken) {
        sessionStorage.setItem('access_token', data.accessToken);
      }
      user.value = data.user || (await fetchCurrentUser());
      initialized.value = true;
      return user.value;
    } finally {
      loading.value = false;
    }
  }

  async function fetchCurrentUser() {
    try {
      const { data } = await authApi.me();
      user.value = data;
      return data;
    } catch {
      user.value = null;
      sessionStorage.removeItem('access_token');
      return null;
    } finally {
      initialized.value = true;
    }
  }

  async function logout() {
    try {
      await authApi.logout();
    } catch {
      // Ignore network errors on logout
    } finally {
      user.value = null;
      sessionStorage.removeItem('access_token');
    }
  }

  return {
    user,
    initialized,
    loading,
    isAuthenticated,
    isAdmin,
    isAgent,
    login,
    fetchCurrentUser,
    logout,
  };
});
