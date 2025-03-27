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
    {
      path: "/matches/new",
      name: "new-match",
      component: () => import("../views/matches/NewMatchView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/matches/history",
      name: "history-match",
      component: () => import("../views/matches/MatchHistoryView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/matches/shedule",
      name: "shedule-match",
      component: () => import("../views/matches/MatchSheduleView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/user/info",
      name: "profile",
      component: () => import("../views/user/UserInfoView.vue"),
      meta: { requiresAuth: true },
    },
    {
      path: "/notifications",
      name: "notifications",
      component: () => import("../views/user/NotificationView.vue"),
      meta: { requiresAuth: true },
    },
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
