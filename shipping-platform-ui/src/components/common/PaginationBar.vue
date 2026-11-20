<script setup>
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';

defineProps({
  page: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  totalElements: { type: Number, required: true },
});

defineEmits(['change']);
</script>

<template>
  <div
    v-if="totalPages > 0"
    class="flex flex-col items-center justify-between gap-3 border-t border-[#E2E8F0] bg-white px-4 py-3 sm:flex-row"
  >
    <p class="text-sm text-[#64748B]">
      Page <span class="font-medium text-[#0F172A]">{{ page + 1 }}</span> of
      <span class="font-medium text-[#0F172A]">{{ totalPages }}</span> ({{ totalElements }} total)
    </p>
    <div class="flex items-center gap-2">
      <button
        type="button"
        class="btn-secondary !px-3 !py-1.5"
        :disabled="page <= 0"
        @click="$emit('change', page - 1)"
      >
        <ChevronLeft class="h-4 w-4" />
        Previous
      </button>
      <button
        type="button"
        class="btn-secondary !px-3 !py-1.5"
        :disabled="page + 1 >= totalPages"
        @click="$emit('change', page + 1)"
      >
        Next
        <ChevronRight class="h-4 w-4" />
      </button>
    </div>
  </div>
</template>
