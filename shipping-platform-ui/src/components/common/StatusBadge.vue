<script setup>
import { computed } from 'vue';
import { SHIPMENT_STATUS } from '@/utils/constants';

const props = defineProps({
  status: { type: String, required: true },
});

const meta = computed(
  () =>
    SHIPMENT_STATUS[props.status] || {
      label: props.status,
      bg: 'bg-slate-100',
      text: 'text-slate-700',
      dot: 'bg-slate-500',
    }
);
</script>

<template>
  <span
    :class="[meta.bg, meta.text]"
    class="inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium"
  >
    <span :class="meta.dot" class="h-1.5 w-1.5 rounded-full" />
    {{ meta.label }}
  </span>
</template>
