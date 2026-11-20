import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PublicLayout.vue'),
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('@/views/public/HomeView.vue'),
      },
      {
        path: 'track/:trackingNumber?',
        name: 'tracking',
        component: () => import('@/views/public/TrackingView.vue'),
      },
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'admin-dashboard',
        component: () => import('@/views/admin/DashboardView.vue'),
      },
      {
        path: 'shipments',
        name: 'admin-shipments',
        component: () => import('@/views/admin/ShipmentsView.vue'),
      },
      {
        path: 'shipments/create',
        name: 'admin-create-shipment',
        component: () => import('@/views/admin/CreateShipmentView.vue'),
      },
      {
        path: 'shipments/:id',
        name: 'admin-shipment-details',
        component: () => import('@/views/admin/ShipmentDetailsView.vue'),
      },
      {
        path: 'agents',
        name: 'admin-agents',
        component: () => import('@/views/admin/AgentsView.vue'),
      },
      {
        path: 'company',
        name: 'admin-company',
        component: () => import('@/views/admin/CompanySettingsView.vue'),
      },
    ],
  },
  {
    path: '/agent',
    component: () => import('@/layouts/AgentLayout.vue'),
    meta: { requiresAuth: true, role: 'DELIVERY_AGENT' },
    redirect: '/agent/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'agent-dashboard',
        component: () => import('@/views/agent/DashboardView.vue'),
      },
      {
        path: 'shipments',
        name: 'agent-shipments',
        component: () => import('@/views/agent/AssignedShipmentsView.vue'),
      },
      {
        path: 'shipments/:id',
        name: 'agent-shipment-details',
        component: () => import('@/views/agent/ShipmentDetailsView.vue'),
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// router.beforeEach(async (to) => {
//   const authStore = useAuthStore();

//   if (!authStore.initialized) {
//     await authStore.fetchCurrentUser();
//   }

//   if (to.meta.guestOnly && authStore.isAuthenticated) {
//     return authStore.isAdmin ? '/admin/dashboard' : '/agent/dashboard';
//   }

//   if (to.meta.requiresAuth) {
//     if (!authStore.isAuthenticated) {
//       return { path: '/login', query: { redirect: to.fullPath } };
//     }
//     if (to.meta.role === 'ADMIN' && !authStore.isAdmin) {
//       return '/agent/dashboard';
//     }
//     if (to.meta.role === 'DELIVERY_AGENT' && !authStore.isAgent) {
//       return '/admin/dashboard';
//     }
//   }

//   return true;
// });

export default router;
