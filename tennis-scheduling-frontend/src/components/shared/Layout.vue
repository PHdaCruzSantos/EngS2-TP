<template>
  <div class="min-h-screen bg-gray-50">
    <Header @toggle-sidebar="toggleSidebar" />

    <div class="flex">
      <!-- Mostramos o Sidebar sempre, mas v-if com condição -->
      <Sidebar v-model="sidebarVisible" v-if="authStore.isAuthenticated" />

      <main
        class="flex-1 p-8 transition-all duration-300"
        :class="{ 'lg:ml-64': sidebarVisible && authStore.isAuthenticated }"
      >
        <!-- Adicionando informações de debug que serão mostradas -->
        <div class="bg-yellow-100 p-4 mb-4 rounded-lg">
          <h2 class="font-bold">Informações de Debug:</h2>
          <p>Autenticado: {{ authStore.isAuthenticated ? "Sim" : "Não" }}</p>
          <p>Nome do usuário: {{ authStore.userName }}</p>
          <p>Sidebar visível: {{ sidebarVisible ? "Sim" : "Não" }}</p>
        </div>

        <slot>
          <!-- Default slot -->
          <h1 class="text-2xl font-semibold text-gray-800">Dashboard</h1>
          <p class="text-gray-600 mt-2">
            Welcome, <strong>{{ authStore.userName }}</strong
            >!
          </p>
        </slot>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useAuthStore } from "../../stores/auth.store";
import Header from "./Header.vue";
import Sidebar from "./Sidebar.vue";

const authStore = useAuthStore();
const sidebarVisible = ref(true);

// Inicializar o store ao montar o componente
onMounted(() => {
  // Inicializar o store para carregar os dados do localStorage
  console.log("Layout mounted - Auth state:", authStore.isAuthenticated);
  console.log("Layout mounted - User:", authStore.user);
});

// Adicionar watch para debugar
watch(
  () => authStore.isAuthenticated,
  (newValue) => {
    console.log("Auth state changed:", newValue);
    console.log("Current user:", authStore.user);
  }
);

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value;
  console.log("Sidebar toggled:", sidebarVisible.value);
};
</script>
