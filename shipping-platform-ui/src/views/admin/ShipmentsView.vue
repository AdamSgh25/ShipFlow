<script setup>
import { reactive, ref, onMounted } from 'vue';
import { Search, Plus, Eye, Trash2 } from 'lucide-vue-next';
import { useShipmentStore } from '@/stores/shipment';
import { shipmentApi } from '@/api/shipment.api';
import { useNotificationStore } from '@/stores/notification';
import StatusBadge from '@/components/common/StatusBadge.vue';
import PaginationBar from '@/components/common/PaginationBar.vue';
import EmptyState from '@/components/common/EmptyState.vue';
import ConfirmDialog from '@/components/common/ConfirmDialog.vue';
import { SHIPMENT_STATUS } from '@/utils/constants';
import { formatDate, formatCurrency } from '@/utils/formatters';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const store = useShipmentStore();
const notify = useNotificationStore();

const filters = reactive({
  search: '',
  status: '',
  date: '',
  page: 0,
  size: 10,
});

const cancelTarget = ref(null);
const cancelling = ref(false);

function load(silent = false) {
  const params = {
    page: filters.page,
    size: filters.size,
    ...(filters.search ? { search: filters.search } : {}),
    ...(filters.status ? { status: filters.status } : {}),
    ...(filters.date ? { date: filters.date } : {}),
  };
  return store.fetchShipments(params, silent);
}

useAutoRefresh(() => load(true));
onMounted(() => load());

function applyFilters() {
  filters.page = 0;
  load();
}

function resetFilters() {
  filters.search = '';
  filters.status = '';
  filters.date = '';
  filters.page = 0;
  load();
}

function onPageChange(newPage) {
  filters.page = newPage;
  load();
}

async function confirmCancel() {
  if (!cancelTarget.value) return;
  cancelling.value = true;
  try {
    await shipmentApi.cancelShipment(cancelTarget.value.id);
    notify.success('Shipment Cancelled', `${cancelTarget.value.trackingNumber} has been cancelled.`);
    cancelTarget.value = null;
    await load();
  } catch (err) {
    notify.error('Cancellation Failed', err.userMessage);
  } finally {
    cancelling.value = false;
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
      <div>
        <h1 class="text-2xl font-bold text-[#0F172A]">Shipments</h1>
        <p class="mt-1 text-sm text-[#64748B]">Manage, filter, and monitor all company shipments</p>
      </div>
      <RouterLink to="/admin/shipments/create" class="btn-primary">
        <Plus class="h-4 w-4" />
        Create Shipment
      </RouterLink>
    </div>

    <div class="card p-4">
      <form @submit.prevent="applyFilters" class="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
        <div class="relative">
          <Search class="pointer-events-none absolute left-3 top-2.5 h-4 w-4 text-[#64748B]" />
          <input
            v-model="filters.search"
            type="text"
            placeholder="Search tracking number..."
            class="input-field !pl-9"
          />
        </div>

        <select v-model="filters.status" class="input-field">
          <option value="">All Statuses</option>
          <option v-for="(meta, key) in SHIPMENT_STATUS" :key="key" :value="key">
            {{ meta.label }}
          </option>
        </select>

        <input v-model="filters.date" type="date" class="input-field" />

        <div class="flex gap-2">
          <button type="submit" class="btn-primary flex-1">Filter</button>
          <button type="button" class="btn-secondary" @click="resetFilters">Reset</button>
        </div>
      </form>
    </div>

    <div v-if="store.loading" class="card space-y-3 p-6">
      <div v-for="n in 5" :key="n" class="h-12 animate-pulse rounded bg-slate-100" />
    </div>

    <EmptyState
      v-else-if="store.shipments.length === 0"
      title="No shipments found"
      description="Create your first shipment or clear your active search filters."
    >
      <template #action>
        <RouterLink to="/admin/shipments/create" class="btn-primary">Create Shipment</RouterLink>
      </template>
    </EmptyState>

    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="border-b border-[#E2E8F0] bg-[#F8FAFC] text-xs font-semibold uppercase text-[#64748B]">
            <tr>
              <th class="px-5 py-3.5">Tracking #</th>
              <th class="px-5 py-3.5">Sender / Recipient</th>
              <th class="px-5 py-3.5">Destination</th>
              <th class="px-5 py-3.5">Agent</th>
              <th class="px-5 py-3.5">Price</th>
              <th class="px-5 py-3.5">Status</th>
              <th class="px-5 py-3.5">Created</th>
              <th class="px-5 py-3.5 text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#E2E8F0]">
            <tr v-for="s in store.shipments" :key="s.id" class="hover:bg-slate-50">
              <td class="px-5 py-4 font-mono font-medium text-[#2563EB]">
                <RouterLink :to="`/admin/shipments/${s.id}`" class="hover:underline">
                  {{ s.trackingNumber }}
                </RouterLink>
              </td>
              <td class="px-5 py-4">
                <p class="font-medium text-[#0F172A]">{{ s.recipientName }}</p>
                <p class="text-xs text-[#64748B]">From: {{ s.senderName }}</p>
              </td>
              <td class="px-5 py-4 text-[#0F172A]">{{ s.city }}</td>
              <td class="px-5 py-4 text-[#64748B]">{{ s.assignedAgentName || 'Unassigned' }}</td>
              <td class="px-5 py-4 font-medium text-[#0F172A]">{{ formatCurrency(s.shippingPrice) }}</td>
              <td class="px-5 py-4"><StatusBadge :status="s.status" /></td>
              <td class="px-5 py-4 text-[#64748B]">{{ formatDate(s.createdAt) }}</td>
              <td class="px-5 py-4 text-right">
                <div class="inline-flex items-center gap-2">
                  <RouterLink
                    :to="`/admin/shipments/${s.id}`"
                    class="rounded-lg p-1.5 text-[#64748B] hover:bg-slate-100 hover:text-[#0F172A]"
                    title="View details"
                  >
                    <Eye class="h-4 w-4" />
                  </RouterLink>
                  <button
                    v-if="['PENDING', 'PICKED_UP'].includes(s.status)"
                    type="button"
                    class="rounded-lg p-1.5 text-[#64748B] hover:bg-red-50 hover:text-[#DC2626]"
                    title="Cancel shipment"
                    @click="cancelTarget = s"
                  >
                    <Trash2 class="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <PaginationBar
        :page="store.pagination.page"
        :total-pages="store.pagination.totalPages"
        :total-elements="store.pagination.totalElements"
        @change="onPageChange"
      />
    </div>

    <ConfirmDialog
      :open="!!cancelTarget"
      title="Cancel Shipment"
      :message="`Are you sure you want to cancel shipment ${cancelTarget?.trackingNumber}? This action cannot be undone.`"
      confirm-text="Cancel Shipment"
      :loading="cancelling"
      @cancel="cancelTarget = null"
      @confirm="confirmCancel"
    />
  </div>
</template>
