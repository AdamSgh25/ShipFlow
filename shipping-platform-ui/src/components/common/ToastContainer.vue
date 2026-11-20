<script setup>
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-vue-next';
import { useNotificationStore } from '@/stores/notification';

const store = useNotificationStore();
</script>

<template>
  <div class="fixed bottom-4 right-4 z-50 flex w-full max-w-sm flex-col gap-2 px-4 sm:px-0">
    <div
      v-for="toast in store.toasts"
      :key="toast.id"
      class="flex items-start gap-3 rounded-xl border border-[#E2E8F0] bg-white p-4 shadow-lg transition-all"
    >
      <CheckCircle2 v-if="toast.type === 'success'" class="mt-0.5 h-5 w-5 shrink-0 text-[#16A34A]" />
      <AlertCircle v-else-if="toast.type === 'error'" class="mt-0.5 h-5 w-5 shrink-0 text-[#DC2626]" />
      <Info v-else class="mt-0.5 h-5 w-5 shrink-0 text-[#2563EB]" />

      <div class="flex-1 text-sm">
        <p class="font-semibold text-[#0F172A]">{{ toast.title }}</p>
        <p v-if="toast.message" class="mt-0.5 text-[#64748B]">{{ toast.message }}</p>
      </div>

      <button
        type="button"
        class="text-[#64748B] hover:text-[#0F172A]"
        aria-label="Dismiss notification"
        @click="store.removeToast(toast.id)"
      >
        <X class="h-4 w-4" />
      </button>
    </div>
  </div>
</template>
