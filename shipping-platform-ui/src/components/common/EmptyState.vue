<script setup>
import { PackageOpen } from 'lucide-vue-next';

defineProps({
  title: { type: String, default: 'No records found' },
  description: { type: String, default: 'Try adjusting your search or filter criteria.' },
});
</script>

<template>
  <div class="flex flex-col items-center justify-center rounded-xl border border-dashed border-[#E2E8F0] bg-white px-6 py-12 text-center">
    <div class="flex h-12 w-12 items-center justify-center rounded-full bg-slate-100 text-[#64748B]">
      <PackageOpen class="h-6 w-6" />
    </div>
    <h3 class="mt-4 text-sm font-semibold text-[#0F172A]">{{ title }}</h3>
    <p class="mt-1 max-w-sm text-sm text-[#64748B]">{{ description }}</p>
    <div v-if="$slots.action" class="mt-5">
      <slot name="action" />
    </div>
  </div>
</template>
