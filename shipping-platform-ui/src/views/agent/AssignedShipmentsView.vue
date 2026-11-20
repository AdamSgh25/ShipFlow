<script setup>
import { reactive, onMounted } from 'vue';
import { Search, MapPin, Phone } from 'lucide-vue-next';
import { useShipmentStore } from '@/stores/shipment';
import StatusBadge from '@/components/common/StatusBadge.vue';
import PaginationBar from '@/components/common/PaginationBar.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import { SHIPMENT_STATUS } from '@/utils/constants';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const store = useShipmentStore();

const filters = reactive({
  search: '',
  status: '',
  page: 0,
  size: 10,
});

function load(silent = false) {
  return store.fetchAgentShipments(
    {
      page: filters.page,
      size: filters.size,
      ...(filters.search ? { search: filters.search } : {}),
      ...(filters.status ? { status: filters.status } : {}),
    },
    silent
  );
}

useAutoRefresh(() => load(true));
onMounted(() => load());

function applyFilters() {
  filters.page = 0;
  load();
}
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-[#0F172A]">Assigned Shipments</h1>
      <p class="mt-1 text-sm text-[#64748B]">View pickup/delivery addresses and update parcel progress</p>
    </div>

    <div class="card p-4">
      <form @submit.prevent="applyFilters" class="flex flex-col gap-3 sm:flex-row">
        <div class="relative flex-1">
          <Search class="pointer-events-none absolute left-3 top-2.5 h-4 w-4 text-[#64748B]" />
          <input
            v-model="filters.search"
            type="text"
            placeholder="Search tracking number..."
            class="input-field !pl-9"
          />
        </div>
        <select v-model="filters.status" class="input-field sm:w-52">
          <option value="">All Statuses</option>
          <option v-for="(meta, key) in SHIPMENT_STATUS" :key="key" :value="key">
            {{ meta.label }}
          </option>
        </select>
        <button type="submit" class="btn-primary">Search</button>
      </form>
    </div>

    <EmptyState
      v-if="!store.loading && store.shipments.length === 0"
      title="No assigned shipments"
      description="You have no shipments matching the selected filters."
    />

    <div v-else class="space-y-3">
      <div
        v-for="s in store.shipments"
        :key="s.id"
        class="card flex flex-col justify-between gap-4 p-5 sm:flex-row sm:items-center"
      >
        <div class="space-y-1.5">
          <div class="flex flex-wrap items-center gap-2.5">
            <span class="font-mono text-base font-bold text-[#2563EB]">{{ s.trackingNumber }}</span>
            <StatusBadge :status="s.status" />
          </div>
          <p class="text-sm font-medium text-[#0F172A]">Recipient: {{ s.recipientName }}</p>
          <p class="flex items-center gap-1.5 text-xs text-[#64748B]">
            <MapPin class="h-3.5 w-3.5 shrink-0" />
            Delivery: {{ s.deliveryAddress }}, {{ s.city }}
          </p>
          <p v-if="s.recipientPhone" class="flex items-center gap-1.5 text-xs text-[#64748B]">
            <Phone class="h-3.5 w-3.5 shrink-0" />
            {{ s.recipientPhone }}
          </p>
        </div>

        <RouterLink :to="`/agent/shipments/${s.id}`" class="btn-primary shrink-0">
          Manage & Update
        </RouterLink>
      </div>

      <div class="card overflow-hidden">
        <PaginationBar
          :page="store.pagination.page"
          :total-pages="store.pagination.totalPages"
          :total-elements="store.pagination.totalElements"
          @change="(p) => { filters.page = p; load(); }"
        />
      </div>
    </div>
  </div>
</template>
