<script setup>
import { CheckCircle2, MapPin, Clock } from 'lucide-vue-next';
import StatusBadge from '@/components/common/StatusBadge.vue';
import { formatDateTime } from '@/utils/formatters';

defineProps({
  history: { type: Array, default: () => [] },
});
</script>

<template>
  <div class="flow-root">
    <ul role="list" class="-mb-8">
      <li v-for="(event, idx) in history" :key="idx" class="relative pb-8">
        <span
          v-if="idx !== history.length - 1"
          class="absolute left-4 top-4 -ml-px h-full w-0.5 bg-[#E2E8F0]"
          aria-hidden="true"
        />
        <div class="relative flex items-start space-x-3.5">
          <div>
            <span
              :class="idx === 0 ? 'bg-[#2563EB] text-white ring-4 ring-blue-50' : 'bg-slate-100 text-[#64748B]'"
              class="flex h-8 w-8 items-center justify-center rounded-full"
            >
              <CheckCircle2 class="h-4 w-4" />
            </span>
          </div>
          <div class="min-w-0 flex-1 pt-0.5">
            <div class="flex flex-wrap items-center justify-between gap-2">
              <StatusBadge :status="event.status" />
              <span class="inline-flex items-center gap-1 text-xs text-[#64748B]">
                <Clock class="h-3.5 w-3.5" />
                {{ formatDateTime(event.timestamp) }}
              </span>
            </div>
            <p class="mt-2 text-sm text-[#0F172A]">{{ event.description }}</p>
            <p v-if="event.location" class="mt-1 inline-flex items-center gap-1 text-xs text-[#64748B]">
              <MapPin class="h-3.5 w-3.5" />
              {{ event.location }}
            </p>
          </div>
        </div>
      </li>
    </ul>
  </div>
</template>
