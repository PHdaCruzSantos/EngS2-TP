<template>
  <div
    class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8"
  >
    <div class="max-w-md w-full space-y-8">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Criar Conta
        </h2>
      </div>
      <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
        <div class="rounded-md shadow-sm -space-y-px">
          <div>
            <label for="name" class="sr-only">Nome</label>
            <input
              id="name"
              v-model="name"
              type="text"
              required
              class="input-field"
              placeholder="Nome completo"
            />
          </div>
          <div>
            <label for="email" class="sr-only">Email</label>
            <input
              id="email"
              v-model="email"
              type="email"
              required
              class="input-field mt-2"
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
          <button type="submit" class="btn-primary w-full">Registrar</button>
        </div>
      </form>

      <div class="text-center">
        <router-link
          to="/login"
          class="text-tennis-dark hover:text-tennis-green"
        >
          Já tem uma conta? Faça login
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth.store"; //import the auth store

const router = useRouter();
const authStore = useAuthStore();

const name = ref("");
const email = ref("");
const password = ref("");

const handleRegister = async () => {
  try {
    await authStore.register(name.value, email.value, password.value);
    router.push("/login");
  } catch (error: any) {
    alert(error.response?.data?.message || "Erro ao criar conta");
  }
};
</script>
