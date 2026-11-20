<script setup>
import { AlertTriangle } from 'lucide-vue-next';

defineProps({
  open: Boolean,
  title: { type: String, default: 'Confirm Action' },
  message: { type: String, required: true },
  confirmText: { type: String, default: 'Confirm' },
  danger: { type: Boolean, default: true },
  loading: Boolean,
});

defineEmits(['confirm', 'cancel']);
</script>

<template>
  <div
    v-if="open"
    class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4 backdrop-blur-[1px]"
  >
    <div class="card w-full max-w-md p-6">
      <div class="flex items-start gap-3">
        <div
          :class="danger ? 'bg-red-50 text-[#DC2626]' : 'bg-blue-50 text-[#2563EB]'"
          class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg"
        >
          <AlertTriangle class="h-5 w-5" />
        </div>
        <div>
          <h3 class="text-base font-semibold text-[#0F172A]">{{ title }}</h3>
          <p class="mt-1 text-sm text-[#64748B]">{{ message }}</p>
        </div>
      </div>

      <div class="mt-6 flex justify-end gap-3">
        <button type="button" class="btn-secondary" :disabled="loading" @click="$emit('cancel')">
          Cancel
        </button>
        <button
          type="button"
          :class="danger ? 'btn-danger' : 'btn-primary'"
          :disabled="loading"
          @click="$emit('confirm')"
        >
          {{ loading ? 'Processing...' : confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>
