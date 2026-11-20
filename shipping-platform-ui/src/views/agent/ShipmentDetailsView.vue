<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { ArrowLeft, MapPin, Phone, CheckCircle } from 'lucide-vue-next';
import { useShipmentStore } from '@/stores/shipment';
import { shipmentApi } from '@/api/shipment.api';
import { useNotificationStore } from '@/stores/notification';
import StatusBadge from '@/components/common/StatusBadge.vue';
import TrackingTimeline from '@/components/tracking/TrackingTimeline.vue';
import { AGENT_ACTION_STATUSES, SHIPMENT_STATUS } from '@/utils/constants';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const route = useRoute();
const store = useShipmentStore();
const notify = useNotificationStore();

const updatingStatus = ref(false);
const selectedStatus = ref('');
const statusForm = reactive({
  location: '',
  notes: '',
});

const load = (silent = false) => store.fetchShipmentById(route.params.id, silent);
useAutoRefresh(() => load(true));
onMounted(() => load());

async function submitStatusUpdate(statusCode) {
  updatingStatus.value = true;
  selectedStatus.value = statusCode;
  try {
    await shipmentApi.updateShipmentStatus(route.params.id, {
      status: statusCode,
      location: statusForm.location.trim() || null,
      notes: statusForm.notes.trim() || null,
    });
    await load();
    statusForm.notes = '';
    notify.success(
      'Status Confirmed',
      `Shipment updated to ${SHIPMENT_STATUS[statusCode]?.label || statusCode}.`
    );
  } catch (err) {
    notify.error('Status Update Failed', err.userMessage);
  } finally {
    updatingStatus.value = false;
    selectedStatus.value = '';
  }
}
</script>

<template>
  <div v-if="store.currentShipment" class="space-y-6">
    <div class="flex items-center gap-3">
      <RouterLink to="/agent/shipments" class="btn-secondary !p-2">
        <ArrowLeft class="h-4 w-4" />
      </RouterLink>
      <div>
        <div class="flex items-center gap-3">
          <h1 class="font-mono text-2xl font-bold text-[#0F172A]">
            {{ store.currentShipment.trackingNumber }}
          </h1>
          <StatusBadge :status="store.currentShipment.status" />
        </div>
        <p class="text-sm text-[#64748B]">Package: {{ store.currentShipment.packageDescription }}</p>
      </div>
    </div>

    <div class="grid gap-6 lg:grid-cols-3">
      <div class="card space-y-5 p-6 lg:col-span-2">
        <div>
          <h2 class="text-base font-semibold text-[#0F172A]">Update Delivery Status</h2>
          <p class="mt-0.5 text-xs text-[#64748B]">
            Select the verified milestone below. Updates are saved immediately upon server confirmation.
          </p>
        </div>

        <div class="grid gap-4 sm:grid-cols-2">
          <div>
            <label class="mb-1 block text-sm font-medium text-[#0F172A]">Current Hub / Location</label>
            <input
              v-model="statusForm.location"
              type="text"
              placeholder="e.g. Central Sorting Hub"
              class="input-field"
              :disabled="updatingStatus"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-[#0F172A]">Delivery Notes</label>
            <input
              v-model="statusForm.notes"
              type="text"
              placeholder="e.g. Left with receptionist"
              class="input-field"
              :disabled="updatingStatus"
            />
          </div>
        </div>

        <div class="grid gap-2.5 sm:grid-cols-2 md:grid-cols-3">
          <button
            v-for="code in AGENT_ACTION_STATUSES"
            :key="code"
            type="button"
            :disabled="updatingStatus || store.currentShipment.status === code"
            :class="[
              code === 'DELIVERED'
                ? 'border-green-200 bg-green-50/70 text-[#16A34A] hover:bg-green-100'
                : code === 'FAILED_DELIVERY'
                  ? 'border-red-200 bg-red-50/70 text-[#DC2626] hover:bg-red-100'
                  : 'border-[#E2E8F0] bg-white text-[#0F172A] hover:border-[#2563EB] hover:text-[#2563EB]',
            ]"
            class="flex items-center justify-center gap-2 rounded-lg border px-3.5 py-2.5 text-sm font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-50"
            @click="submitStatusUpdate(code)"
          >
            <CheckCircle class="h-4 w-4" />
            <span>
              {{
                updatingStatus && selectedStatus === code
                  ? 'Updating...'
                  : SHIPMENT_STATUS[code]?.label
              }}
            </span>
          </button>
        </div>

        <div class="grid gap-4 border-t border-[#E2E8F0] pt-5 sm:grid-cols-2 text-sm">
          <div class="rounded-lg bg-[#F8FAFC] p-4">
            <p class="text-xs font-semibold uppercase text-[#64748B]">Pickup Location</p>
            <p class="mt-1 font-medium text-[#0F172A]">{{ store.currentShipment.senderName }}</p>
            <p class="mt-1 flex items-start gap-1.5 text-[#64748B]">
              <MapPin class="mt-0.5 h-4 w-4 shrink-0" />
              {{ store.currentShipment.pickupAddress }}
            </p>
          </div>

          <div class="rounded-lg bg-[#F8FAFC] p-4">
            <p class="text-xs font-semibold uppercase text-[#64748B]">Delivery Destination</p>
            <p class="mt-1 font-medium text-[#0F172A]">{{ store.currentShipment.recipientName }}</p>
            <p class="mt-1 flex items-start gap-1.5 text-[#64748B]">
              <MapPin class="mt-0.5 h-4 w-4 shrink-0" />
              {{ store.currentShipment.deliveryAddress }}, {{ store.currentShipment.city }}
            </p>
            <p v-if="store.currentShipment.recipientPhone" class="mt-1.5 flex items-center gap-1.5 font-medium text-[#2563EB]">
              <Phone class="h-3.5 w-3.5" />
              {{ store.currentShipment.recipientPhone }}
            </p>
          </div>
        </div>
      </div>

      <div class="card p-6">
        <h2 class="mb-4 text-base font-semibold text-[#0F172A]">Tracking History</h2>
        <TrackingTimeline :history="store.currentShipment.history || []" />
      </div>
    </div>
  </div>
</template>
