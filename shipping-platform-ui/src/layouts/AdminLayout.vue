<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import {
  LayoutDashboard,
  Package,
  Users,
  Building2,
  LogOut,
  Menu,
  X,
  Plus,
} from 'lucide-vue-next';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const router = useRouter();
const mobileMenuOpen = ref(false);

const navItems = [
  { name: 'Dashboard', to: '/admin/dashboard', icon: LayoutDashboard },
  { name: 'Shipments', to: '/admin/shipments', icon: Package },
  { name: 'Delivery Agents', to: '/admin/agents', icon: Users },
  { name: 'Company Profile', to: '/admin/company', icon: Building2 },
];

async function handleLogout() {
  await authStore.logout();
  router.push('/login');
}
</script>

<template>
  <div class="flex min-h-screen bg-[#F8FAFC]">
    <aside class="hidden w-64 flex-col border-r border-[#E2E8F0] bg-white lg:flex">
      <div class="flex h-16 items-center gap-2.5 border-b border-[#E2E8F0] px-6">
        <div class="flex h-8 w-8 items-center justify-center rounded-lg bg-[#2563EB] text-white">
          <Package class="h-4 w-4" />
        </div>
        <div class="truncate">
          <p class="text-sm font-bold text-[#0F172A]">ShipFlow Admin</p>
          <p class="truncate text-xs text-[#64748B]">{{ authStore.user?.companyName || 'Company Portal' }}</p>
        </div>
      </div>

      <div class="p-4">
        <RouterLink to="/admin/shipments/create" class="btn-primary w-full">
          <Plus class="h-4 w-4" />
          New Shipment
        </RouterLink>
      </div>

      <nav class="flex-1 space-y-1 px-3">
        <RouterLink
          v-for="item in navItems"
          :key="item.to"
          :to="item.to"
          active-class="bg-blue-50 text-[#2563EB] font-semibold"
          class="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-[#64748B] transition-colors hover:bg-slate-50 hover:text-[#0F172A]"
        >
          <component :is="item.icon" class="h-4 w-4" />
          {{ item.name }}
        </RouterLink>
      </nav>

      <div class="border-t border-[#E2E8F0] p-4">
        <div class="mb-3 px-2">
          <p class="truncate text-sm font-medium text-[#0F172A]">{{ authStore.user?.fullName }}</p>
          <p class="truncate text-xs text-[#64748B]">{{ authStore.user?.email }}</p>
        </div>
        <button type="button" class="btn-secondary w-full justify-start text-[#DC2626]" @click="handleLogout">
          <LogOut class="h-4 w-4" />
          Sign Out
        </button>
      </div>
    </aside>

    <div class="flex flex-1 flex-col">
      <header class="flex h-16 items-center justify-between border-b border-[#E2E8F0] bg-white px-4 lg:hidden">
        <span class="font-bold text-[#0F172A]">ShipFlow Admin</span>
        <button type="button" class="rounded-lg p-2 text-[#64748B]" @click="mobileMenuOpen = !mobileMenuOpen">
          <X v-if="mobileMenuOpen" class="h-5 w-5" />
          <Menu v-else class="h-5 w-5" />
        </button>
      </header>

      <div v-if="mobileMenuOpen" class="border-b border-[#E2E8F0] bg-white p-4 lg:hidden">
        <nav class="space-y-1">
          <RouterLink
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-[#0F172A]"
            @click="mobileMenuOpen = false"
          >
            <component :is="item.icon" class="h-4 w-4" />
            {{ item.name }}
          </RouterLink>
          <button type="button" class="flex w-full items-center gap-3 px-3 py-2 text-sm font-medium text-[#DC2626]" @click="handleLogout">
            <LogOut class="h-4 w-4" />
            Sign Out
          </button>
        </nav>
      </div>

      <main class="flex-1 p-4 sm:p-6 lg:p-8">
        <div class="mx-auto max-w-7xl">
          <RouterView />
        </div>
      </main>
    </div>
  </div>
</template>
