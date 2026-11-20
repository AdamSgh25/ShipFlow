<script setup>
import { reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Package, AlertCircle } from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute();

const form = reactive({
  email: '',
  password: '',
});
const errorMessage = ref('');

async function handleSubmit() {
  errorMessage.value = '';
  try {
    const user = await authStore.login(form);
    if (route.query.redirect) {
      router.push(String(route.query.redirect));
    } else if (user?.role === 'ADMIN' || user?.role === 'ROLE_ADMIN') {
      router.push('/admin/dashboard');
    } else {
      router.push('/agent/dashboard');
    }
  } catch (err) {
    errorMessage.value = err.userMessage || 'Invalid email or password.';
  }
}
</script>

<template>
  <div class="flex min-h-screen items-center justify-center bg-[#F8FAFC] px-4">
    <div class="w-full max-w-md">
      <div class="mb-8 text-center">
        <RouterLink to="/" class="inline-flex items-center gap-2.5">
          <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-[#2563EB] text-white">
            <Package class="h-5 w-5" />
          </div>
          <span class="text-xl font-bold text-[#0F172A]">ShipFlow</span>
        </RouterLink>
        <h1 class="mt-6 text-2xl font-bold text-[#0F172A]">Sign in to your account</h1>
        <p class="mt-1.5 text-sm text-[#64748B]">Company Administrators & Delivery Agents</p>
      </div>

      <div class="card p-6 sm:p-8">
        <div
          v-if="errorMessage"
          class="mb-5 flex items-center gap-2.5 rounded-lg border border-red-200 bg-red-50 p-3.5 text-sm text-[#DC2626]"
        >
          <AlertCircle class="h-4 w-4 shrink-0" />
          <span>{{ errorMessage }}</span>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label for="email" class="mb-1.5 block text-sm font-medium text-[#0F172A]">Email address</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              autocomplete="email"
              placeholder="name@company.com"
              class="input-field"
            />
          </div>

          <div>
            <label for="password" class="mb-1.5 block text-sm font-medium text-[#0F172A]">Password</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              autocomplete="current-password"
              placeholder="••••••••"
              class="input-field"
            />
          </div>

          <button type="submit" class="btn-primary mt-2 w-full" :disabled="authStore.loading">
            {{ authStore.loading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>
      </div>

      <p class="mt-6 text-center text-sm text-[#64748B]">
        Looking to track a parcel?
        <RouterLink to="/track" class="font-medium text-[#2563EB] hover:underline">
          Go to Public Tracking
        </RouterLink>
      </p>
    </div>
  </div>
</template>
