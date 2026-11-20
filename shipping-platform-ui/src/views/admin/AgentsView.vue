<script setup>
import { ref, reactive, onMounted } from 'vue';
import { UserPlus, Edit2, Power, PackageCheck } from 'lucide-vue-next';
import { deliveryAgentApi } from '@/api/deliveryAgent.api';
import { useNotificationStore } from '@/stores/notification';
import StatusBadge from '@/components/common/StatusBadge.vue';

const notify = useNotificationStore();
const agents = ref([]);
const loading = ref(true);
const showModal = ref(false);
const editingAgent = ref(null);
const selectedAgentDetails = ref(null);
const saving = ref(false);

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  password: '',
  vehicleType: 'Van',
});

async function fetchAgents() {
  loading.value = true;
  try {
    const { data } = await deliveryAgentApi.getAgents();
    agents.value = data.content || data || [];
  } catch (err) {
    notify.error('Failed to load agents', err.userMessage);
  } finally {
    loading.value = false;
  }
}

onMounted(fetchAgents);

function openCreate() {
  editingAgent.value = null;
  Object.assign(form, { fullName: '', email: '', phone: '', password: '', vehicleType: 'Van' });
  showModal.value = true;
}

function openEdit(agent) {
  editingAgent.value = agent;
  Object.assign(form, {
    fullName: agent.fullName,
    email: agent.email,
    phone: agent.phone,
    password: '',
    vehicleType: agent.vehicleType || 'Van',
  });
  showModal.value = true;
}

async function saveAgent() {
  saving.value = true;
  try {
    if (editingAgent.value) {
      await deliveryAgentApi.updateAgent(editingAgent.value.id, {
        fullName: form.fullName,
        email: form.email,
        phone: form.phone,
        vehicleType: form.vehicleType,
      });
      notify.success('Agent Updated', `${form.fullName} updated.`);
    } else {
      await deliveryAgentApi.createAgent(form);
      notify.success('Agent Added', `${form.fullName} can now log in.`);
    }
    showModal.value = false;
    await fetchAgents();
  } catch (err) {
    notify.error('Operation Failed', err.userMessage);
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(agent) {
  try {
    await deliveryAgentApi.toggleAgentStatus(agent.id, !agent.active);
    notify.success('Status Updated', `${agent.fullName} is now ${!agent.active ? 'Active' : 'Inactive'}.`);
    await fetchAgents();
  } catch (err) {
    notify.error('Update Failed', err.userMessage);
  }
}

async function inspectAgent(agent) {
  try {
    const { data } = await deliveryAgentApi.getAgentById(agent.id);
    selectedAgentDetails.value = data;
  } catch (err) {
    notify.error('Unable to load agent shipments', err.userMessage);
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
      <div>
        <h1 class="text-2xl font-bold text-[#0F172A]">Delivery Agents</h1>
        <p class="mt-1 text-sm text-[#64748B]">Manage fleet couriers, account access, and delivery performance</p>
      </div>
      <button type="button" class="btn-primary" @click="openCreate">
        <UserPlus class="h-4 w-4" />
        Add Delivery Agent
      </button>
    </div>

    <div class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="border-b border-[#E2E8F0] bg-[#F8FAFC] text-xs font-semibold uppercase text-[#64748B]">
            <tr>
              <th class="px-6 py-3.5">Agent</th>
              <th class="px-6 py-3.5">Phone / Vehicle</th>
              <th class="px-6 py-3.5">Assigned</th>
              <th class="px-6 py-3.5">Delivered</th>
              <th class="px-6 py-3.5">Success Rate</th>
              <th class="px-6 py-3.5">Status</th>
              <th class="px-6 py-3.5 text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#E2E8F0]">
            <tr v-for="agent in agents" :key="agent.id" class="hover:bg-slate-50">
              <td class="px-6 py-4">
                <p class="font-medium text-[#0F172A]">{{ agent.fullName }}</p>
                <p class="text-xs text-[#64748B]">{{ agent.email }}</p>
              </td>
              <td class="px-6 py-4 text-[#64748B]">
                <p class="text-[#0F172A]">{{ agent.phone }}</p>
                <p class="text-xs">{{ agent.vehicleType || 'Standard' }}</p>
              </td>
              <td class="px-6 py-4 font-medium text-[#0F172A]">{{ agent.assignedCount ?? 0 }}</td>
              <td class="px-6 py-4 font-medium text-[#16A34A]">{{ agent.deliveredCount ?? 0 }}</td>
              <td class="px-6 py-4 text-[#0F172A]">{{ agent.successRate ?? 100 }}%</td>
              <td class="px-6 py-4">
                <span
                  :class="agent.active ? 'bg-green-50 text-[#16A34A]' : 'bg-slate-100 text-slate-600'"
                  class="inline-flex rounded-full px-2.5 py-1 text-xs font-medium"
                >
                  {{ agent.active ? 'Active' : 'Inactive' }}
                </span>
              </td>
              <td class="px-6 py-4 text-right">
                <div class="inline-flex items-center gap-1.5">
                  <button
                    type="button"
                    class="rounded-lg p-1.5 text-[#64748B] hover:bg-slate-100 hover:text-[#2563EB]"
                    title="View assigned shipments"
                    @click="inspectAgent(agent)"
                  >
                    <PackageCheck class="h-4 w-4" />
                  </button>
                  <button
                    type="button"
                    class="rounded-lg p-1.5 text-[#64748B] hover:bg-slate-100 hover:text-[#0F172A]"
                    title="Edit agent"
                    @click="openEdit(agent)"
                  >
                    <Edit2 class="h-4 w-4" />
                  </button>
                  <button
                    type="button"
                    class="rounded-lg p-1.5 text-[#64748B] hover:bg-slate-100 hover:text-[#F59E0B]"
                    :title="agent.active ? 'Deactivate' : 'Activate'"
                    @click="toggleStatus(agent)"
                  >
                    <Power class="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && agents.length === 0">
              <td colspan="7" class="px-6 py-8 text-center text-[#64748B]">No delivery agents registered yet.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="selectedAgentDetails" class="card p-6">
      <div class="flex items-center justify-between border-b border-[#E2E8F0] pb-3">
        <h2 class="font-semibold text-[#0F172A]">
          Assigned Shipments — {{ selectedAgentDetails.fullName }}
        </h2>
        <button type="button" class="text-sm text-[#64748B] hover:text-[#0F172A]" @click="selectedAgentDetails = null">
          Close
        </button>
      </div>
      <div v-if="selectedAgentDetails.assignedShipments?.length" class="mt-4 divide-y divide-[#E2E8F0]">
        <div
          v-for="s in selectedAgentDetails.assignedShipments"
          :key="s.id"
          class="flex items-center justify-between py-3 text-sm"
        >
          <div>
            <span class="font-mono font-medium text-[#2563EB]">{{ s.trackingNumber }}</span>
            <span class="ml-3 text-[#0F172A]">{{ s.recipientName }} ({{ s.city }})</span>
          </div>
          <StatusBadge :status="s.status" />
        </div>
      </div>
      <p v-else class="mt-4 text-sm text-[#64748B]">No active shipments assigned to this agent.</p>
    </div>

    <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
      <form @submit.prevent="saveAgent" class="card w-full max-w-md space-y-4 p-6">
        <h2 class="text-lg font-bold text-[#0F172A]">
          {{ editingAgent ? 'Edit Delivery Agent' : 'Add Delivery Agent' }}
        </h2>
        <div>
          <label class="mb-1 block text-sm font-medium">Full Name *</label>
          <input v-model="form.fullName" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Email *</label>
          <input v-model="form.email" type="email" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Phone *</label>
          <input v-model="form.phone" required class="input-field" />
        </div>
        <div>
          <label class="mb-1 block text-sm font-medium">Vehicle Type</label>
          <select v-model="form.vehicleType" class="input-field">
            <option>Motorcycle</option>
            <option>Van</option>
            <option>Truck</option>
          </select>
        </div>
        <div v-if="!editingAgent">
          <label class="mb-1 block text-sm font-medium">Temporary Password *</label>
          <input v-model="form.password" type="password" required minlength="6" class="input-field" />
        </div>
        <div class="flex justify-end gap-3 pt-2">
          <button type="button" class="btn-secondary" @click="showModal = false">Cancel</button>
          <button type="submit" class="btn-primary" :disabled="saving">
            {{ saving ? 'Saving...' : 'Save Agent' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
