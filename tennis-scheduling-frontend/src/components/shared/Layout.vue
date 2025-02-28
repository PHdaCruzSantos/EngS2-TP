<template>
  <div class="min-h-screen bg-gray-50">
    <Header @toggle-sidebar="toggleSidebar" />

    <div class="flex">
      <Sidebar v-if="authStore.isAuthenticated" v-model="sidebarVisible" />

      <main
        class="flex-1 p-8 transition-all duration-300"
        :class="{ 'lg:ml-64': sidebarVisible && authStore.isAuthenticated }"
      >
        <slot></slot>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useAuthStore } from "@/stores/auth.store";
import Header from "./Header.vue";
import Sidebar from "./Sidebar.vue";

const authStore = useAuthStore();
const sidebarVisible = ref(true);

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value;
};
</script>
