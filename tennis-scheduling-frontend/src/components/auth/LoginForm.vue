<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Login
        </h2>
      </div>
      <form class="mt-8 space-y-6" @submit.prevent="handleLogin">
        <div class="rounded-md shadow-sm -space-y-px">
          <div>
            <label for="email" class="sr-only">Email</label>
            <input
              id="email"
              v-model="email"
              type="email"
              required
              class="input-field"
              placeholder="Email"
            />
          </div>
          <div>
            <label for="password" class="sr-only">Senha</label>
            <input
              id="password"
              v-model="password"
              type="password"
              required
              class="input-field mt-2"
              placeholder="Senha"
            />
          </div>
        </div>

        <div>
          <button type="submit" class="btn-primary w-full">Entrar</button>
        </div>
      </form>

      <div class="text-center">
        <router-link
          to="/register"
          class="text-tennis-dark hover:text-tennis-green"
        >
          Não tem uma conta? Registre-se
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth.store";

const router = useRouter();
const authStore = useAuthStore();

const email = ref("");
const password = ref("");

const handleLogin = async () => {
  try {
    await authStore.login(email.value, password.value);
    router.push("/matches");
  } catch (error: any) {
    alert(error.response?.data?.message || "Erro ao fazer login");
  }
};
</script>
