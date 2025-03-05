import { defineStore } from "pinia";
import { authService } from "../services/auth.service";
import router from "../router";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    // Garantir que o estado inicial seja carregado do localStorage
    user: null as any | null,
    returnUrl: null as string | null,
  }),

  getters: {
    // Adaptado para a estrutura aninhada do usuário
    isAuthenticated: (state) => !!state.user?.token,
    token: (state) => state.user?.token,
    // Adicionar getter para nome do usuário para facilitar acesso
    userName: (state) => state.user?.user?.name || state.user?.name || "",
  },

  actions: {
    // Inicializar o estado de autenticação
    init() {
      const userData = localStorage.getItem("user");
      if (userData) {
        this.user = JSON.parse(userData);
        console.log("Auth store initialized with user:", this.user);
      }
    },

    async login(email: string, password: string) {
      try {
        const user = await authService.login(email, password);
        this.user = user; // Atualiza o estado do usuário no Pinia após o login
        console.log("User logged in:", this.user);
        if (user.token) {
          router.push(this.returnUrl || "/"); // Redireciona para a rota anterior ou para a home
        }
        return user;
      } catch (error) {
        throw error;
      }
    },

    async register(name: string, email: string, password: string) {
      try {
        const response = await authService.register({ name, email, password });
        if (response) {
          router.push("/login"); // Redireciona para a tela de login após o registro
        }
        return response;
      } catch (error) {
        throw error;
      }
    },

    logout() {
      authService.logout();
      this.user = null;
      console.log("User logged out");
    },
  },
});
