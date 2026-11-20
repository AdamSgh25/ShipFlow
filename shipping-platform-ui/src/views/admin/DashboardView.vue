<script setup>
import { ref, onMounted, computed } from 'vue';
import {
  Package,
  Clock,
  Truck,
  CheckCircle2,
  XCircle,
  Users,
  Plus,
  AlertCircle,
} from 'lucide-vue-next';
import { shipmentApi } from '@/api/shipment.api';
import StatusBadge from '@/components/common/StatusBadge.vue';
import { formatDateTime } from '@/utils/formatters';
import { SHIPMENT_STATUS } from '@/utils/constants';
import { useAutoRefresh } from '@/composables/useAutoRefresh';

const stats = ref(null);
const recentShipments = ref([]);
const loading = ref(true);
const error = ref(null);

async function loadDashboard(silent = false) {
  if (!silent) loading.value = true;
  error.value = null;
  try {
    const [statsRes, shipmentsRes] = await Promise.all([
      shipmentApi.getDashboardStats(),
      shipmentApi.getShipments({ page: 0, size: 6 }),
    ]);
    stats.value = statsRes.data;
    recentShipments.value = shipmentsRes.data.content || shipmentsRes.data || [];
  } catch (err) {
    if (!silent) error.value = err.userMessage;
  } finally {
    if (!silent) loading.value = false;
  }
}

useAutoRefresh(() => loadDashboard(true));
onMounted(() => loadDashboard());

const kpiCards = computed(() => [
  { label: 'Total Shipments', value: stats.value?.totalShipments ?? 0, icon: Package, color: 'text-[#2563EB] bg-blue-50' },
  { label: 'Pending', value: stats.value?.pendingShipments ?? 0, icon: Clock, color: 'text-slate-700 bg-slate-100' },
  { label: 'In Transit', value: stats.value?.inTransitShipments ?? 0, icon: Truck, color: 'text-indigo-600 bg-indigo-50' },
  { label: 'Delivered', value: stats.value?.deliveredShipments ?? 0, icon: CheckCircle2, color: 'text-[#16A34A] bg-green-50' },
  { label: 'Failed Deliveries', value: stats.value?.failedDeliveries ?? 0, icon: XCircle, color: 'text-[#DC2626] bg-red-50' },
  { label: 'Delivery Agents', value: stats.value?.totalAgents ?? 0, icon: Users, color: 'text-[#F59E0B] bg-amber-50' },
]);

const distributionBars = computed(() => {
  const dist = stats.value?.statusDistribution || {};
  const total = Object.values(dist).reduce((a, b) => a + Number(b || 0), 0) || 1;
  return Object.entries(dist).map(([status, count]) => ({
    status,
    label: SHIPMENT_STATUS[status]?.label || status,
    count,
    percent: Math.round((Number(count) / total) * 100),
  }));
});
</script>

<template>
  <div class="space-y-6">
    <div class="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
      <div>
        <h1 class="text-2xl font-bold text-[#0F172A]">Dashboard Overview</h1>
        <p class="mt-1 text-sm text-[#64748B]">Real-time company shipping operations & metrics</p>
      </div>
      <RouterLink to="/admin/shipments/create" class="btn-primary">
        <Plus class="h-4 w-4" />
        Create Shipment
      </RouterLink>
    </div>

    <div v-if="error" class="card flex items-center gap-3 border-red-200 bg-red-50 p-4 text-sm text-[#DC2626]">
      <AlertCircle class="h-5 w-5" />
      <span>{{ error }}</span>
    </div>

    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
      <template v-if="loading">
        <div v-for="n in 6" :key="n" class="card h-28 animate-pulse p-5">
          <div class="h-4 w-24 rounded bg-slate-200" />
          <div class="mt-4 h-7 w-14 rounded bg-slate-200" />
        </div>
      </template>
      <template v-else>
        <div v-for="kpi in kpiCards" :key="kpi.label" class="card p-5">
          <div class="flex items-center justify-between">
            <span class="text-xs font-medium text-[#64748B]">{{ kpi.label }}</span>
            <div :class="kpi.color" class="rounded-lg p-2">
              <component :is="kpi.icon" class="h-4 w-4" />
            </div>
          </div>
          <p class="mt-3 text-2xl font-bold text-[#0F172A]">{{ kpi.value }}</p>
        </div>
      </template>
    </div>

    <div class="grid gap-6 lg:grid-cols-3">
      <div class="card p-6 lg:col-span-2">
        <h2 class="text-base font-semibold text-[#0F172A]">Shipment Status Distribution</h2>
        <div v-if="distributionBars.length" class="mt-5 space-y-4">
          <div v-for="bar in distributionBars" :key="bar.status">
            <div class="mb-1.5 flex justify-between text-sm">
              <span class="font-medium text-[#0F172A]">{{ bar.label }}</span>
              <span class="text-[#64748B]">{{ bar.count }} ({{ bar.percent }}%)</span>
            </div>
            <div class="h-2.5 w-full overflow-hidden rounded-full bg-slate-100">
              <div
                class="h-full rounded-full bg-[#2563EB] transition-all duration-300"
                :style="{ width: `${bar.percent}%` }"
              />
            </div>
          </div>
        </div>
        <p v-else class="mt-6 text-sm text-[#64748B]">No distribution metrics available yet.</p>
      </div>

      <div class="card p-6">
        <h2 class="text-base font-semibold text-[#0F172A]">Recent Activities</h2>
        <ul v-if="stats?.recentActivities?.length" class="mt-4 space-y-4">
          <li
            v-for="(act, idx) in stats.recentActivities"
            :key="idx"
            class="border-b border-[#E2E8F0] pb-3 last:border-none last:pb-0"
          >
            <p class="text-sm text-[#0F172A]">{{ act.message }}</p>
            <p class="mt-1 text-xs text-[#64748B]">{{ formatDateTime(act.timestamp) }}</p>
          </li>
        </ul>
        <p v-else class="mt-6 text-sm text-[#64748B]">No recent activities logged.</p>
      </div>
    </div>

    <div class="card overflow-hidden">
      <div class="flex items-center justify-between border-b border-[#E2E8F0] px-6 py-4">
        <h2 class="text-base font-semibold text-[#0F172A]">Recent Shipments</h2>
        <RouterLink to="/admin/shipments" class="text-sm font-medium text-[#2563EB] hover:underline">
          View all
        </RouterLink>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm">
          <thead class="border-b border-[#E2E8F0] bg-[#F8FAFC] text-xs font-semibold uppercase text-[#64748B]">
            <tr>
              <th class="px-6 py-3.5">Tracking #</th>
              <th class="px-6 py-3.5">Recipient</th>
              <th class="px-6 py-3.5">City</th>
              <th class="px-6 py-3.5">Agent</th>
              <th class="px-6 py-3.5">Status</th>
              <th class="px-6 py-3.5">Created</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-[#E2E8F0]">
            <tr v-for="item in recentShipments" :key="item.id" class="hover:bg-slate-50">
              <td class="px-6 py-4 font-mono font-medium text-[#2563EB]">
                <RouterLink :to="`/admin/shipments/${item.id}`" class="hover:underline">
                  {{ item.trackingNumber }}
                </RouterLink>
              </td>
              <td class="px-6 py-4 text-[#0F172A]">{{ item.recipientName }}</td>
              <td class="px-6 py-4 text-[#64748B]">{{ item.city }}</td>
              <td class="px-6 py-4 text-[#64748B]">{{ item.assignedAgentName || 'Unassigned' }}</td>
              <td class="px-6 py-4"><StatusBadge :status="item.status" /></td>
              <td class="px-6 py-4 text-[#64748B]">{{ formatDateTime(item.createdAt) }}</td>
            </tr>
            <tr v-if="!loading && recentShipments.length === 0">
              <td colspan="6" class="px-6 py-8 text-center text-[#64748B]">No shipments found.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
