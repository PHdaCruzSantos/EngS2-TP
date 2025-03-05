import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth.store";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("../views/auth/LoginView.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/register",
      name: "register",
      component: () => import("../views/auth/RegisterView.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/",
      redirect: "/matches",
    },
    {
      path: "/matches",
      name: "matches",
      component: () => import("../views/matches/MatchesView.vue"),
      meta: { requiresAuth: false },
    },
    // {
    //   path: "/profile",
    //   name: "profile",
    //   // component: () => import("@/views/profile/ProfileView.vue"),
    //   meta: { requiresAuth: true },
    // },
    // {
    //   path: "/notifications",
    //   name: "notifications",
    //   // component: () => import("@/views/notifications/NotificationsView.vue"),
    //   meta: { requiresAuth: true },
    // },
  ],
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  if (requiresAuth && !authStore.isAuthenticated) {
    next("/");
  } else {
    next();
  }
});

export default router;
