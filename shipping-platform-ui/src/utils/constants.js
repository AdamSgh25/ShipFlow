export const SHIPMENT_STATUS = {
  PENDING: {
    label: 'Pending',
    bg: 'bg-slate-100',
    text: 'text-slate-700',
    dot: 'bg-slate-500',
  },
  PICKED_UP: {
    label: 'Picked Up',
    bg: 'bg-blue-50',
    text: 'text-[#2563EB]',
    dot: 'bg-[#2563EB]',
  },
  IN_TRANSIT: {
    label: 'In Transit',
    bg: 'bg-indigo-50',
    text: 'text-indigo-700',
    dot: 'bg-indigo-600',
  },
  ARRIVED_AT_HUB: {
    label: 'Arrived at Hub',
    bg: 'bg-sky-50',
    text: 'text-sky-700',
    dot: 'bg-sky-600',
  },
  OUT_FOR_DELIVERY: {
    label: 'Out for Delivery',
    bg: 'bg-amber-50',
    text: 'text-[#F59E0B]',
    dot: 'bg-[#F59E0B]',
  },
  DELIVERED: {
    label: 'Delivered',
    bg: 'bg-green-50',
    text: 'text-[#16A34A]',
    dot: 'bg-[#16A34A]',
  },
  FAILED_DELIVERY: {
    label: 'Failed Delivery',
    bg: 'bg-red-50',
    text: 'text-[#DC2626]',
    dot: 'bg-[#DC2626]',
  },
  CANCELLED: {
    label: 'Cancelled',
    bg: 'bg-zinc-100',
    text: 'text-zinc-600',
    dot: 'bg-zinc-500',
  },
};

export const AGENT_ACTION_STATUSES = [
  'PICKED_UP',
  'IN_TRANSIT',
  'ARRIVED_AT_HUB',
  'OUT_FOR_DELIVERY',
  'DELIVERED',
  'FAILED_DELIVERY',
];
