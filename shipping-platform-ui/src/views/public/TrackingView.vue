<script setup>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Search, Building2, Calendar, MapPin, AlertCircle, RefreshCw } from 'lucide-vue-next';
import { trackingApi } from '@/api/tracking.api';
import StatusBadge from '@/components/common/StatusBadge.vue';
import TrackingTimeline from '@/components/tracking/TrackingTimeline.vue';
import { formatDate } from '@/utils/formatters';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const route = useRoute();
const router = useRouter();

const queryNumber = ref(route.params.trackingNumber || '');
const shipment = ref(null);
const loading = ref(false);
const error = ref(null);

async function fetchTracking(silent = false) {
  const code = (route.params.trackingNumber || '').trim();
  if (!code) {
    shipment.value = null;
    return;
  }
  if (!silent) {
    loading.value = true;
    error.value = null;
  }
  try {
    const { data } = await trackingApi.trackShipment(code);
    shipment.value = data;
    error.value = null;
  } catch (err) {
    if (!silent) {
      shipment.value = null;
      error.value =
        err.response?.status === 404
          ? `No shipment found with tracking number "${code}".`
          : err.userMessage;
    }
  } finally {
    if (!silent) loading.value = false;
  }
}

useAutoRefresh(() => {
  if (route.params.trackingNumber) return fetchTracking(true);
});

function submitSearch() {
  if (!queryNumber.value.trim()) return;
  router.push({ name: 'tracking', params: { trackingNumber: queryNumber.value.trim() } });
}

watch(
  () => route.params.trackingNumber,
  (val) => {
    queryNumber.value = val || '';
    fetchTracking();
  }
);

onMounted(() => {
  if (route.params.trackingNumber) fetchTracking();
});
</script>

<template>
  <div class="mx-auto max-w-4xl px-4 py-10 sm:px-6">
    <div class="card p-6">
      <h1 class="text-xl font-bold text-[#0F172A]">Track Your Shipment</h1>
      <form @submit.prevent="submitSearch" class="mt-4 flex flex-col gap-3 sm:flex-row">
        <div class="relative flex-1">
          <Search class="pointer-events-none absolute left-3.5 top-2.5 h-5 w-5 text-[#64748B]" />
          <input
            v-model="queryNumber"
            type="text"
            required
            placeholder="Enter tracking number"
            class="input-field !pl-11"
          />
        </div>
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Searching...' : 'Track Shipment' }}
        </button>
      </form>
    </div>

    <div v-if="loading" class="card mt-6 animate-pulse space-y-4 p-6">
      <div class="h-6 w-48 rounded bg-slate-200" />
      <div class="grid gap-4 sm:grid-cols-3">
        <div class="h-16 rounded bg-slate-100" />
        <div class="h-16 rounded bg-slate-100" />
        <div class="h-16 rounded bg-slate-100" />
      </div>
    </div>

    <div v-else-if="error" class="card mt-6 flex items-center gap-3 border-red-200 bg-red-50/50 p-6 text-[#DC2626]">
      <AlertCircle class="h-5 w-5 shrink-0" />
      <p class="text-sm font-medium">{{ error }}</p>
    </div>

    <div v-else-if="shipment" class="mt-6 space-y-6">
      <div class="card p-6">
        <div class="flex flex-wrap items-center justify-between gap-4 border-b border-[#E2E8F0] pb-4">
          <div>
            <p class="text-xs font-medium uppercase tracking-wider text-[#64748B]">Tracking Number</p>
            <h2 class="mt-0.5 font-mono text-xl font-bold text-[#0F172A]">
              {{ shipment.trackingNumber }}
            </h2>
          </div>
          <div class="flex items-center gap-3">
            <StatusBadge :status="shipment.status" />
            <button
              type="button"
              class="btn-secondary !p-2"
              title="Refresh status"
              @click="fetchTracking()"
            >
              <RefreshCw class="h-4 w-4" />
            </button>
          </div>
        </div>

        <div class="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <div class="rounded-lg bg-[#F8FAFC] p-3.5">
            <p class="flex items-center gap-1.5 text-xs text-[#64748B]">
              <Building2 class="h-3.5 w-3.5" /> Sender Company
            </p>
            <p class="mt-1 text-sm font-semibold text-[#0F172A]">{{ shipment.companyName || '—' }}</p>
          </div>

          <div class="rounded-lg bg-[#F8FAFC] p-3.5">
            <p class="flex items-center gap-1.5 text-xs text-[#64748B]">
              <Calendar class="h-3.5 w-3.5" /> Created Date
            </p>
            <p class="mt-1 text-sm font-semibold text-[#0F172A]">{{ formatDate(shipment.createdAt) }}</p>
          </div>

          <div class="rounded-lg bg-[#F8FAFC] p-3.5">
            <p class="flex items-center gap-1.5 text-xs text-[#64748B]">
              <Calendar class="h-3.5 w-3.5" /> Est. Delivery
            </p>
            <p class="mt-1 text-sm font-semibold text-[#0F172A]">
              {{ formatDate(shipment.estimatedDeliveryDate) }}
            </p>
          </div>

          <div class="rounded-lg bg-[#F8FAFC] p-3.5">
            <p class="flex items-center gap-1.5 text-xs text-[#64748B]">
              <MapPin class="h-3.5 w-3.5" /> Current Location
            </p>
            <p class="mt-1 text-sm font-semibold text-[#0F172A]">
              {{ shipment.currentLocation || 'In Processing' }}
            </p>
          </div>
        </div>
      </div>

      <div class="card p-6">
        <h3 class="mb-6 text-base font-semibold text-[#0F172A]">Shipment Tracking History</h3>
        <TrackingTimeline v-if="shipment.history?.length" :history="shipment.history" />
        <p v-else class="text-sm text-[#64748B]">No tracking events recorded yet.</p>
      </div>
    </div>
  </div>
</template>
