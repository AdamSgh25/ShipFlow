<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Search, ShieldCheck, Clock, Building2, ArrowRight } from 'lucide-vue-next';

const router = useRouter();
const trackingInput = ref('');

function handleTrack() {
  if (!trackingInput.value.trim()) return;
  router.push({ name: 'tracking', params: { trackingNumber: trackingInput.value.trim() } });
}
</script>

<template>
  <div>
    <section class="border-b border-[#E2E8F0] bg-white py-16 sm:py-24">
      <div class="mx-auto max-w-4xl px-4 text-center sm:px-6">
        <h1 class="text-3xl font-bold tracking-tight text-[#0F172A] sm:text-5xl">
          Track Your Shipment
        </h1>
        <p class="mx-auto mt-4 max-w-2xl text-base text-[#64748B] sm:text-lg">
          Enter your tracking number below to view real-time delivery milestones, carrier details, and current parcel location—no account required.
        </p>

        <form @submit.prevent="handleTrack" class="mx-auto mt-8 flex max-w-xl flex-col gap-3 sm:flex-row">
          <div class="relative flex-1">
            <Search class="pointer-events-none absolute left-3.5 top-3 h-5 w-5 text-[#64748B]" />
            <input
              v-model="trackingInput"
              type="text"
              required
              placeholder="e.g. SHP-2026-884920"
              class="input-field !py-3 !pl-11 text-base"
            />
          </div>
          <button type="submit" class="btn-primary !px-6 !py-3 text-base">
            Track Shipment
            <ArrowRight class="h-4 w-4" />
          </button>
        </form>
      </div>
    </section>

    <section class="mx-auto max-w-6xl px-4 py-16 sm:px-6">
      <div class="text-center">
        <h2 class="text-2xl font-bold text-[#0F172A]">How It Works</h2>
        <p class="mt-2 text-sm text-[#64748B]">Three simple steps from dispatch to doorstep</p>
      </div>

      <div class="mt-10 grid gap-6 md:grid-cols-3">
        <div class="card p-6">
          <span class="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-blue-50 text-sm font-bold text-[#2563EB]">
            1
          </span>
          <h3 class="mt-4 text-base font-semibold text-[#0F172A]">Receive Tracking ID</h3>
          <p class="mt-2 text-sm text-[#64748B]">
            Once a shipping partner creates your dispatch order, a unique tracking number is generated automatically.
          </p>
        </div>

        <div class="card p-6">
          <span class="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-blue-50 text-sm font-bold text-[#2563EB]">
            2
          </span>
          <h3 class="mt-4 text-base font-semibold text-[#0F172A]">Verified Transit Scans</h3>
          <p class="mt-2 text-sm text-[#64748B]">
            Assigned delivery agents log verified checkpoints at pickup, hub arrival, and out-for-delivery stages.
          </p>
        </div>

        <div class="card p-6">
          <span class="inline-flex h-8 w-8 items-center justify-center rounded-lg bg-blue-50 text-sm font-bold text-[#2563EB]">
            3
          </span>
          <h3 class="mt-4 text-base font-semibold text-[#0F172A]">Instant Status Timeline</h3>
          <p class="mt-2 text-sm text-[#64748B]">
            Check your shipment timeline anytime with automatic status updates and estimated delivery dates.
          </p>
        </div>
      </div>
    </section>

    <section class="border-t border-[#E2E8F0] bg-white py-16">
      <div class="mx-auto max-w-6xl px-4 sm:px-6">
        <div class="grid gap-8 md:grid-cols-3">
          <div class="flex items-start gap-4">
            <div class="rounded-lg bg-blue-50 p-2.5 text-[#2563EB]">
              <Building2 class="h-5 w-5" />
            </div>
            <div>
              <h3 class="font-semibold text-[#0F172A]">Multi-Carrier Network</h3>
              <p class="mt-1 text-sm text-[#64748B]">
                Unified tracking across all registered logistics companies on a single platform.
              </p>
            </div>
          </div>

          <div class="flex items-start gap-4">
            <div class="rounded-lg bg-blue-50 p-2.5 text-[#2563EB]">
              <Clock class="h-5 w-5" />
            </div>
            <div>
              <h3 class="font-semibold text-[#0F172A]">Live Milestone Sync</h3>
              <p class="mt-1 text-sm text-[#64748B]">
                Shipment timelines refresh automatically as soon as delivery agents confirm updates.
              </p>
            </div>
          </div>

          <div class="flex items-start gap-4">
            <div class="rounded-lg bg-blue-50 p-2.5 text-[#2563EB]">
              <ShieldCheck class="h-5 w-5" />
            </div>
            <div>
              <h3 class="font-semibold text-[#0F172A]">Privacy Protected</h3>
              <p class="mt-1 text-sm text-[#64748B]">
                Public tracking displays only transit milestones—personal contact data remains strictly private.
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
