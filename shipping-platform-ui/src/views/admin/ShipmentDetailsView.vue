<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, Edit3, XCircle, Save } from 'lucide-vue-next';
import { useShipmentStore } from '@/stores/shipment';
import { shipmentApi } from '@/api/shipment.api';
import { deliveryAgentApi } from '@/api/deliveryAgent.api';
import { useNotificationStore } from '@/stores/notification';
import StatusBadge from '@/components/common/StatusBadge.vue';
import TrackingTimeline from '@/components/tracking/TrackingTimeline.vue';
import ConfirmDialog from '@/components/common/ConfirmDialog.vue';
import { formatDateTime, formatCurrency } from '@/utils/formatters';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const route = useRoute();
const router = useRouter();
const store = useShipmentStore();
const notify = useNotificationStore();

const agents = ref([]);
const editing = ref(false);
const saving = ref(false);
const showCancelDialog = ref(false);
const cancelling = ref(false);

const editForm = reactive({
  recipientName: '',
  recipientPhone: '',
  deliveryAddress: '',
  city: '',
  deliveryNotes: '',
  assignedAgentId: '',
});

async function loadDetails(silent = false) {
  const data = await store.fetchShipmentById(route.params.id, silent);
  if (data && !editing.value) {
    Object.assign(editForm, {
      recipientName: data.recipientName,
      recipientPhone: data.recipientPhone,
      deliveryAddress: data.deliveryAddress,
      city: data.city,
      deliveryNotes: data.deliveryNotes || '',
      assignedAgentId: data.assignedAgentId || '',
    });
  }
}

useAutoRefresh(() => loadDetails(true));

onMounted(async () => {
  await loadDetails();
  try {
    const { data } = await deliveryAgentApi.getAgents({ active: true });
    agents.value = data.content || data || [];
  } catch {
    agents.value = [];
  }
});

async function saveEdits() {
  saving.value = true;
  try {
    await shipmentApi.updateShipment(route.params.id, {
      ...store.currentShipment,
      ...editForm,
      assignedAgentId: editForm.assignedAgentId || null,
    });
    notify.success('Shipment Updated', 'Changes saved successfully.');
    editing.value = false;
    await loadDetails();
  } catch (err) {
    notify.error('Update Failed', err.userMessage);
  } finally {
    saving.value = false;
  }
}

async function handleCancelShipment() {
  cancelling.value = true;
  try {
    await shipmentApi.cancelShipment(route.params.id);
    notify.success('Shipment Cancelled', 'The shipment has been cancelled.');
    showCancelDialog.value = false;
    router.push('/admin/shipments');
  } catch (err) {
    notify.error('Cancel Failed', err.userMessage);
  } finally {
    cancelling.value = false;
  }
}
</script>

<template>
  <div v-if="store.currentShipment" class="space-y-6">
    <div class="flex flex-wrap items-center justify-between gap-4">
      <div class="flex items-center gap-3">
        <RouterLink to="/admin/shipments" class="btn-secondary !p-2">
          <ArrowLeft class="h-4 w-4" />
        </RouterLink>
        <div>
          <div class="flex items-center gap-3">
            <h1 class="font-mono text-2xl font-bold text-[#0F172A]">
              {{ store.currentShipment.trackingNumber }}
            </h1>
            <StatusBadge :status="store.currentShipment.status" />
          </div>
          <p class="text-sm text-[#64748B]">
            Created {{ formatDateTime(store.currentShipment.createdAt) }}
          </p>
        </div>
      </div>

      <div
        v-if="!['DELIVERED', 'CANCELLED'].includes(store.currentShipment.status)"
        class="flex items-center gap-3"
      >
        <button type="button" class="btn-secondary" @click="editing = !editing">
          <Edit3 class="h-4 w-4" />
          {{ editing ? 'Cancel Edit' : 'Edit Shipment' }}
        </button>
        <button type="button" class="btn-danger" @click="showCancelDialog = true">
          <XCircle class="h-4 w-4" />
          Cancel Shipment
        </button>
      </div>
    </div>

    <form v-if="editing" @submit.prevent="saveEdits" class="card space-y-4 p-6">
      <h2 class="text-base font-semibold text-[#0F172A]">Edit Shipment Details</h2>
      <div class="grid gap-4 sm:grid-cols-2">
        <div>
          <label class="mb-1 block text-sm font-medium">Recipient Name</label>
          <input v-model="editForm.recipientName" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Recipient Phone</label>
          <input v-model="editForm.recipientPhone" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">City</label>
          <input v-model="editForm.city" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Delivery Address</label>
          <input v-model="editForm.deliveryAddress" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Assigned Delivery Agent</label>
          <select v-model="editForm.assignedAgentId" class="input-field">
            <option value="">Unassigned</option>
            <option v-for="a in agents" :key="a.id" :value="a.id">{{ a.fullName }}</option>
          </select>
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Delivery Notes</label>
          <input v-model="editForm.deliveryNotes" class="input-field" />
        </div>
      </div>
      <div class="flex justify-end">
        <button type="submit" class="btn-primary" :disabled="saving">
          <Save class="h-4 w-4" />
          {{ saving ? 'Saving...' : 'Save Changes' }}
        </button>
      </div>
    </form>

    <div class="grid gap-6 lg:grid-cols-3">
      <div class="card space-y-4 p-6 lg:col-span-2">
        <h2 class="text-base font-semibold text-[#0F172A]">Shipment & Contact Details</h2>
        <div class="grid gap-4 sm:grid-cols-2 text-sm">
          <div>
            <p class="text-[#64748B]">Sender</p>
            <p class="font-medium text-[#0F172A]">{{ store.currentShipment.senderName }}</p>
            <p class="text-[#64748B]">{{ store.currentShipment.senderPhone }}</p>
            <p class="mt-1 text-[#0F172A]">{{ store.currentShipment.pickupAddress }}</p>
          </div>
          <div>
            <p class="text-[#64748B]">Recipient</p>
            <p class="font-medium text-[#0F172A]">{{ store.currentShipment.recipientName }}</p>
            <p class="text-[#64748B]">{{ store.currentShipment.recipientPhone }}</p>
            <p class="mt-1 text-[#0F172A]">
              {{ store.currentShipment.deliveryAddress }}, {{ store.currentShipment.city }}
            </p>
          </div>
          <div>
            <p class="text-[#64748B]">Package</p>
            <p class="font-medium text-[#0F172A]">
              {{ store.currentShipment.packageDescription }} ({{ store.currentShipment.packageWeight }} kg)
            </p>
          </div>
          <div>
            <p class="text-[#64748B]">Shipping Price & Agent</p>
            <p class="font-medium text-[#0F172A]">
              {{ formatCurrency(store.currentShipment.shippingPrice) }} •
              {{ store.currentShipment.assignedAgentName || 'Unassigned' }}
            </p>
          </div>
        </div>
      </div>

      <div class="card p-6">
        <h2 class="mb-4 text-base font-semibold text-[#0F172A]">Tracking History</h2>
        <TrackingTimeline :history="store.currentShipment.history || []" />
      </div>
    </div>

    <ConfirmDialog
      :open="showCancelDialog"
      title="Cancel Shipment"
      message="Are you sure you want to cancel this shipment?"
      confirm-text="Yes, Cancel"
      :loading="cancelling"
      @cancel="showCancelDialog = false"
      @confirm="handleCancelShipment"
    />
  </div>
</template>
