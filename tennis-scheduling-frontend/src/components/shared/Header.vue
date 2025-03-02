<template>
  <Menubar class="bg-tennis-dark border-none rounded-none">
    <template #start>
      <div class="flex items-center">
        <Button
          v-if="authStore.isAuthenticated"
          icon="pi pi-bars"
          @click="toggleSidebar"
          class="p-button-text p-button-rounded text-white"
        />
        <h1 class="text-xl font-semibold text-white ml-3">Tennis Scheduling</h1>
      </div>
    </template>

    <!-- Menu para usuário não autenticado -->
    <template #end>
      <div v-if="!authStore.isAuthenticated" class="flex items-center gap-4">
        <Button
          label="Login"
          icon="pi pi-sign-in"
          class="p-button-text text-white"
          @click="router.push('/login')"
        />
        <Button
          label="Criar Conta"
          icon="pi pi-user-plus"
          class="p-button-outlined text-white border-white hover:bg-white hover:text-tennis-dark"
          @click="router.push('/register')"
        />
      </div>

      <!-- Menu para usuário autenticado -->
      <div v-else class="flex items-center gap-4">
        <!-- Btn toggle menu -->

        <div
          class="flex items-center mr-2 cursor-pointer hover:opacity-80 transition-opacity"
          @click="toggleMenu"
        >
          <Avatar
            :label="userInitials"
            class="mr-2 bg-tennis-light text-tennis-dark"
            shape="circle"
          />
          <span class="text-white hidden md:inline">
            {{ authStore.userName }}
          </span>
          <i class="pi pi-chevron-down text-white ml-2"></i>
        </div>
        <Menu ref="menu" :model="menuItems" :popup="true">
          <template #target> </template>
        </Menu>

        <Badge
          v-if="unreadNotifications"
          :value="unreadNotifications"
          class="p-overlay-badge"
        >
          <i class="pi pi-bell text-white text-xl cursor-pointer"></i>
        </Badge>
      </div>
    </template>
  </Menubar>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth.store";
import Menubar from "primevue/menubar";
import Menu from "primevue/menu";
import Avatar from "primevue/avatar";
import Badge from "primevue/badge";
import Button from "primevue/button";

const router = useRouter();
const authStore = useAuthStore();
const menu = ref();

const emit = defineEmits(["toggle-sidebar"]);

// Debug para verificar o estado de autenticação
onMounted(() => {
  console.log("Header mounted - Auth state:", authStore.isAuthenticated);
  console.log("Header mounted - User:", authStore.user);
});

// Watch para monitorar mudanças no estado de autenticação
watch(
  () => authStore.isAuthenticated,
  (newValue) => {
    console.log("Auth state changed:", newValue);
  }
);

const userInitials = computed(() => {
  const name = authStore.userName || "";
  if (!name) return "U";
  return name
    .split(" ")
    .map((word: string) => word[0])
    .join("")
    .toUpperCase();
});

const toggleSidebar = () => {
  emit("toggle-sidebar");
};

const toggleMenu = (event: Event) => {
  menu.value?.toggle(event);
};

const menuItems = [
  {
    label: "Meu Perfil",
    icon: "pi pi-user",
    command: () => router.push("/profile"),
  },
  {
    separator: true,
  },
  {
    label: "Sair",
    icon: "pi pi-power-off",
    command: () => {
      authStore.logout();
      router.push("/login");
    },
  },
];

const unreadNotifications = ref(0);
</script>

<style scoped>
:deep(.p-menubar) {
  @apply px-4 py-2;
}

:deep(.p-menu) {
  @apply min-w-[200px];
}

:deep(.p-button.p-button-outlined:hover) {
  @apply bg-white border-white text-tennis-dark;
}

:deep(.p-menu-overlay) {
  @apply mt-2;
}
</style>
