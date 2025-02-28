import { defineStore } from "pinia";
import { authService } from "../services/auth.service";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: authService.getCurrentUser(),
    returnUrl: null as string | null,
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    token: (state) => state.user?.token,
  },

  actions: {
    async login(email: string, password: string) {
      try {
        const user = await authService.login(email, password);
        this.user = user;
        return user;
      } catch (error) {
        throw error;
      }
    },

    async register(name: string, email: string, password: string) {
      try {
        const response = await authService.register({ name, email, password });
        return response.data;
      } catch (error) {
        throw error;
      }
    },

    logout() {
      authService.logout();
      this.user = null;
    },
  },
});
