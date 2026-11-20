<script setup>
import { useRouter } from 'vue-router';
import { LayoutDashboard, Truck, LogOut, Package } from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();

async function handleLogout() {
  await authStore.logout();
  router.push('/login');
}
</script>

<template>
  <div class="min-h-screen bg-[#F8FAFC]">
    <header class="sticky top-0 z-30 border-b border-[#E2E8F0] bg-white">
      <div class="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6">
        <div class="flex items-center gap-6">
          <RouterLink to="/agent/dashboard" class="flex items-center gap-2.5">
            <div class="flex h-8 w-8 items-center justify-center rounded-lg bg-[#2563EB] text-white">
              <Package class="h-4 w-4" />
            </div>
            <span class="font-bold text-[#0F172A]">Agent Portal</span>
          </RouterLink>

          <nav class="hidden items-center gap-2 sm:flex">
            <RouterLink
              to="/agent/dashboard"
              active-class="bg-blue-50 text-[#2563EB]"
              class="inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium text-[#64748B] hover:text-[#0F172A]"
            >
              <LayoutDashboard class="h-4 w-4" />
              Overview
            </RouterLink>
            <RouterLink
              to="/agent/shipments"
              active-class="bg-blue-50 text-[#2563EB]"
              class="inline-flex items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium text-[#64748B] hover:text-[#0F172A]"
            >
              <Truck class="h-4 w-4" />
              Assigned Shipments
            </RouterLink>
          </nav>
        </div>

        <div class="flex items-center gap-4">
          <span class="hidden text-sm text-[#64748B] md:inline">{{ authStore.user?.fullName }}</span>
          <button type="button" class="btn-secondary !py-1.5 text-[#DC2626]" @click="handleLogout">
            <LogOut class="h-4 w-4" />
            <span class="hidden sm:inline">Logout</span>
          </button>
        </div>
      </div>
    </header>

    <main class="mx-auto max-w-6xl p-4 sm:p-6 lg:p-8">
      <RouterView />
    </main>
  </div>
</template>
