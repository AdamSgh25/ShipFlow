<script setup>
import { computed, onMounted } from 'vue';
import { Package, Clock, Truck, CheckCircle2, ArrowRight } from 'lucide-vue-next';
import { useShipmentStore } from '@/stores/shipment';
import StatusBadge from '@/components/common/StatusBadge.vue';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const store = useShipmentStore();

const load = (silent = false) => store.fetchAgentShipments({ page: 0, size: 50 }, silent);
useAutoRefresh(() => load(true));
onMounted(() => load());

const counts = computed(() => {
  const list = store.shipments;
  return {
    assigned: list.length,
    pendingPickups: list.filter((s) => s.status === 'PENDING').length,
    inTransit: list.filter((s) =>
      ['PICKED_UP', 'IN_TRANSIT', 'ARRIVED_AT_HUB', 'OUT_FOR_DELIVERY'].includes(s.status)
    ).length,
    completed: list.filter((s) => s.status === 'DELIVERED').length,
  };
});

const activeQueue = computed(() =>
  store.shipments.filter((s) => !['DELIVERED', 'CANCELLED'].includes(s.status)).slice(0, 8)
);
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
      <div>
        <h1 class="text-2xl font-bold text-[#0F172A]">Delivery Overview</h1>
        <p class="mt-1 text-sm text-[#64748B]">Your active route assignments and delivery metrics</p>
      </div>
      <RouterLink to="/agent/shipments" class="btn-primary">
        All Assigned Shipments
        <ArrowRight class="h-4 w-4" />
      </RouterLink>
    </div>

    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <div class="card p-5">
        <div class="flex items-center justify-between">
          <span class="text-xs font-medium text-[#64748B]">Assigned Shipments</span>
          <Package class="h-4 w-4 text-[#2563EB]" />
        </div>
        <p class="mt-3 text-2xl font-bold text-[#0F172A]">{{ counts.assigned }}</p>
      </div>

      <div class="card p-5">
        <div class="flex items-center justify-between">
          <span class="text-xs font-medium text-[#64748B]">Pending Pickups</span>
          <Clock class="h-4 w-4 text-[#F59E0B]" />
        </div>
        <p class="mt-3 text-2xl font-bold text-[#0F172A]">{{ counts.pendingPickups }}</p>
      </div>

      <div class="card p-5">
        <div class="flex items-center justify-between">
          <span class="text-xs font-medium text-[#64748B]">In Transit</span>
          <Truck class="h-4 w-4 text-indigo-600" />
        </div>
        <p class="mt-3 text-2xl font-bold text-[#0F172A]">{{ counts.inTransit }}</p>
      </div>

      <div class="card p-5">
        <div class="flex items-center justify-between">
          <span class="text-xs font-medium text-[#64748B]">Completed Deliveries</span>
          <CheckCircle2 class="h-4 w-4 text-[#16A34A]" />
        </div>
        <p class="mt-3 text-2xl font-bold text-[#0F172A]">{{ counts.completed }}</p>
      </div>
    </div>

    <div class="card overflow-hidden">
      <div class="border-b border-[#E2E8F0] px-6 py-4">
        <h2 class="text-base font-semibold text-[#0F172A]">Active Deliveries Queue</h2>
      </div>
      <div class="divide-y divide-[#E2E8F0]">
        <div
          v-for="s in activeQueue"
          :key="s.id"
          class="flex flex-col justify-between gap-4 p-5 sm:flex-row sm:items-center"
        >
          <div>
            <div class="flex items-center gap-3">
              <span class="font-mono font-bold text-[#2563EB]">{{ s.trackingNumber }}</span>
              <StatusBadge :status="s.status" />
            </div>
            <p class="mt-1 text-sm font-medium text-[#0F172A]">
              {{ s.recipientName }} • {{ s.deliveryAddress }}, {{ s.city }}
            </p>
          </div>
          <RouterLink :to="`/agent/shipments/${s.id}`" class="btn-secondary !py-2">
            Update Status
          </RouterLink>
        </div>
        <p v-if="!store.loading && activeQueue.length === 0" class="p-8 text-center text-sm text-[#64748B]">
          No active deliveries pending right now.
        </p>
      </div>
    </div>
  </div>
</template>
