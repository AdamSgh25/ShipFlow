<script setup>
import { Package } from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
</script>

<template>
  <div class="flex min-h-screen flex-col bg-[#F8FAFC]">
    <header class="sticky top-0 z-30 border-b border-[#E2E8F0] bg-white/95 backdrop-blur">
      <div class="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6">
        <RouterLink to="/" class="flex items-center gap-2.5">
          <div class="flex h-9 w-9 items-center justify-center rounded-lg bg-[#2563EB] text-white">
            <Package class="h-5 w-5" />
          </div>
          <span class="text-lg font-bold tracking-tight text-[#0F172A]">ShipFlow</span>
        </RouterLink>

        <nav class="flex items-center gap-6">
          <RouterLink to="/" class="text-sm font-medium text-[#64748B] hover:text-[#0F172A]">
            Home
          </RouterLink>
          <RouterLink to="/track" class="text-sm font-medium text-[#64748B] hover:text-[#0F172A]">
            Track Parcel
          </RouterLink>
          <RouterLink
            v-if="authStore.isAuthenticated"
            :to="authStore.isAdmin ? '/admin/dashboard' : '/agent/dashboard'"
            class="btn-primary !py-2"
          >
            Dashboard
          </RouterLink>
          <RouterLink v-else to="/login" class="btn-secondary !py-2">
            Staff Login
          </RouterLink>
        </nav>
      </div>
    </header>

    <main class="flex-1">
      <RouterView />
    </main>

    <footer class="border-t border-[#E2E8F0] bg-white py-8">
      <div class="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 px-4 text-sm text-[#64748B] sm:flex-row sm:px-6">
        <p>© {{ new Date().getFullYear() }} ShipFlow Logistics Platform. All rights reserved.</p>
        <div class="flex gap-6">
          <RouterLink to="/track" class="hover:text-[#0F172A]">Track Shipment</RouterLink>
          <RouterLink to="/login" class="hover:text-[#0F172A]">Partner Portal</RouterLink>
        </div>
      </div>
    </footer>
  </div>
</template>
