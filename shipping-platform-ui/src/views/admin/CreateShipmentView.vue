<script setup>
import { reactive, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowLeft } from 'lucide-vue-next';
import { shipmentApi } from '@/api/shipment.api';
import { deliveryAgentApi } from '@/api/deliveryAgent.api';
import { useNotificationStore } from '@/stores/notification';

const router = useRouter();
const notify = useNotificationStore();

const agents = ref([]);
const submitting = ref(false);

const form = reactive({
  senderName: '',
  senderPhone: '',
  recipientName: '',
  recipientPhone: '',
  pickupAddress: '',
  deliveryAddress: '',
  city: '',
  packageDescription: '',
  packageWeight: '',
  shippingPrice: '',
  deliveryNotes: '',
  assignedAgentId: '',
});

onMounted(async () => {
  try {
    const { data } = await deliveryAgentApi.getAgents({ active: true });
    agents.value = data.content || data || [];
  } catch {
    agents.value = [];
  }
});

async function handleSubmit() {
  submitting.value = true;
  try {
    const payload = {
      ...form,
      packageWeight: Number(form.packageWeight),
      shippingPrice: Number(form.shippingPrice),
      assignedAgentId: form.assignedAgentId || null,
    };
    const { data } = await shipmentApi.createShipment(payload);
    notify.success('Shipment Created', `Tracking number ${data.trackingNumber} assigned.`);
    router.push(`/admin/shipments/${data.id}`);
  } catch (err) {
    notify.error('Creation Failed', err.userMessage);
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <div class="mx-auto max-w-3xl space-y-6">
    <div class="flex items-center gap-3">
      <RouterLink to="/admin/shipments" class="btn-secondary !p-2">
        <ArrowLeft class="h-4 w-4" />
      </RouterLink>
      <div>
        <h1 class="text-2xl font-bold text-[#0F172A]">Create Shipment</h1>
        <p class="text-sm text-[#64748B]">Tracking number will be generated automatically upon creation</p>
      </div>
    </div>

    <form @submit.prevent="handleSubmit" class="card space-y-6 p-6">
      <div>
        <h2 class="text-sm font-semibold uppercase tracking-wider text-[#64748B]">Sender Information</h2>
        <div class="mt-4 grid gap-4 sm:grid-cols-2">
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Sender Name *</label>
            <input v-model="form.senderName" type="text" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Sender Phone *</label>
            <input v-model="form.senderPhone" type="tel" required class="input-field" />
          </div>
          <div class="sm:col-span-2">
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Pickup Address *</label>
            <input v-model="form.pickupAddress" type="text" required class="input-field" />
          </div>
        </div>
      </div>

      <div class="border-t border-[#E2E8F0] pt-6">
        <h2 class="text-sm font-semibold uppercase tracking-wider text-[#64748B]">Recipient Information</h2>
        <div class="mt-4 grid gap-4 sm:grid-cols-2">
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Recipient Name *</label>
            <input v-model="form.recipientName" type="text" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Recipient Phone *</label>
            <input v-model="form.recipientPhone" type="tel" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">City *</label>
            <input v-model="form.city" type="text" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Delivery Address *</label>
            <input v-model="form.deliveryAddress" type="text" required class="input-field" />
          </div>
        </div>
      </div>

      <div class="border-t border-[#E2E8F0] pt-6">
        <h2 class="text-sm font-semibold uppercase tracking-wider text-[#64748B]">Package & Assignment</h2>
        <div class="mt-4 grid gap-4 sm:grid-cols-3">
          <div class="sm:col-span-3">
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Package Description *</label>
            <input v-model="form.packageDescription" type="text" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Weight (kg) *</label>
            <input v-model="form.packageWeight" type="number" step="0.01" min="0.01" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Shipping Price ($) *</label>
            <input v-model="form.shippingPrice" type="number" step="0.01" min="0" required class="input-field" />
          </div>
          <div>
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Assign Delivery Agent</label>
            <select v-model="form.assignedAgentId" class="input-field">
              <option value="">Unassigned</option>
              <option v-for="agent in agents" :key="agent.id" :value="agent.id">
                {{ agent.fullName }}
              </option>
            </select>
          </div>
          <div class="sm:col-span-3">
            <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Delivery Notes</label>
            <textarea v-model="form.deliveryNotes" rows="3" class="input-field" />
          </div>
        </div>
      </div>

      <div class="flex justify-end gap-3 border-t border-[#E2E8F0] pt-4">
        <RouterLink to="/admin/shipments" class="btn-secondary">Cancel</RouterLink>
        <button type="submit" class="btn-primary" :disabled="submitting">
          {{ submitting ? 'Creating...' : 'Create Shipment' }}
        </button>
      </div>
    </form>
  </div>
</template>
