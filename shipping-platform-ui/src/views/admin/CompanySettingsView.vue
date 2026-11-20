<script setup>
import { reactive, ref, onMounted } from 'vue';
import { Building2, Save } from 'lucide-vue-next';
import { companyApi } from '@/api/company.api';
import { useNotificationStore } from '@/stores/notification';
import { formatDate } from '@/utils/formatters';

const notify = useNotificationStore();
const loading = ref(true);
const saving = ref(false);
const meta = ref({ createdAt: null, plan: 'Standard' });

const form = reactive({
  name: '',
  logoUrl: '',
  email: '',
  phone: '',
  address: '',
});

onMounted(async () => {
  try {
    const { data } = await companyApi.getProfile();
    Object.assign(form, {
      name: data.name || '',
      logoUrl: data.logoUrl || '',
      email: data.email || '',
      phone: data.phone || '',
      address: data.address || '',
    });
    meta.value = { createdAt: data.createdAt, plan: data.plan || 'Enterprise' };
  } catch (err) {
    notify.error('Failed to load company profile', err.userMessage);
  } finally {
    loading.value = false;
  }
});

async function handleSave() {
  saving.value = true;
  try {
    await companyApi.updateProfile(form);
    notify.success('Profile Updated', 'Company settings saved.');
  } catch (err) {
    notify.error('Update Failed', err.userMessage);
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <div class="mx-auto max-w-3xl space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-[#0F172A]">Company Profile</h1>
      <p class="mt-1 text-sm text-[#64748B]">Manage your shipping organization's public and account details</p>
    </div>

    <div v-if="loading" class="card h-64 animate-pulse p-6" />

    <form v-else @submit.prevent="handleSave" class="card space-y-6 p-6">
      <div class="flex items-center gap-4 border-b border-[#E2E8F0] pb-6">
        <img
          v-if="form.logoUrl"
          :src="form.logoUrl"
          alt="Company Logo"
          class="h-16 w-16 rounded-xl border border-[#E2E8F0] object-cover"
        />
        <div
          v-else
          class="flex h-16 w-16 items-center justify-center rounded-xl bg-blue-50 text-[#2563EB]"
        >
          <Building2 class="h-8 w-8" />
        </div>
        <div>
          <h2 class="text-lg font-bold text-[#0F172A]">{{ form.name || 'Company Name' }}</h2>
          <p class="text-xs text-[#64748B]">
            Plan: {{ meta.plan }} • Member since {{ formatDate(meta.createdAt) }}
          </p>
        </div>
      </div>

      <div class="grid gap-4 sm:grid-cols-2">
        <div>
          <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Company Name *</label>
          <input v-model="form.name" required class="input-field" />
        </div>
        <div>
          <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Logo URL</label>
          <input v-model="form.logoUrl" type="url" placeholder="https://..." class="input-field" />
        </div>
        <div>
          <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Contact Email *</label>
          <input v-model="form.email" type="email" required class="input-field" />
        </div>
        <div>
          <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Phone Number *</label>
          <input v-model="form.phone" required class="input-field" />
        </div>
        <div class="sm:col-span-2">
          <label class="mb-1.5 block text-sm font-medium text-[#0F172A]">Headquarters Address *</label>
          <input v-model="form.address" required class="input-field" />
        </div>
      </div>

      <div class="flex justify-end border-t border-[#E2E8F0] pt-4">
        <button type="submit" class="btn-primary" :disabled="saving">
          <Save class="h-4 w-4" />
          {{ saving ? 'Saving...' : 'Save Company Profile' }}
        </button>
      </div>
    </form>
  </div>
</template>
