<template>
  <Sidebar
    v-model="sidebarVisible"
    :modal="false"
    :dismissable="false"
    :showCloseIcon="false"
    class="w-64 fixed left-0 top-0 h-full bg-white"
  >
    <template #header>
      <div class="p-4 bg-tennis-dark text-white rounded">
        <h2 class="text-lg font-semibold">Menu</h2>
      </div>
    </template>

    <div class="p-4">
      <PanelMenu :model="menuItems" class="border-none" />
    </div>
  </Sidebar>
</template>

<script setup lang="ts">
import { ref, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import Sidebar from "primevue/sidebar";
import PanelMenu from "primevue/panelmenu";

const route = useRoute();
const router = useRouter();

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits(["update:modelValue"]);

const sidebarVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

const menuItems = [
  {
    label: "Dashboard",
    icon: "pi pi-home",
    command: () => router.push("/matches"),
    class: route.path === "/matches" ? "bg-tennis-light text-tennis-dark" : "",
  },
  {
    label: "Minhas Partidas",
    icon: "pi pi-calendar",
    items: [
      {
        label: "Agendar Partida",
        icon: "pi pi-plus",
        command: () => router.push("/matches/new"),
      },
      {
        label: "Partidas Agendadas",
        icon: "pi pi-list",
        command: () => router.push("/matches"),
      },
      {
        label: "Histórico",
        icon: "pi pi-history",
        command: () => router.push("/matches/history"),
      },
    ],
  },
  {
    label: "Notificações",
    icon: "pi pi-bell",
    command: () => router.push("/notifications"),
    class:
      route.path === "/notifications" ? "bg-tennis-light text-tennis-dark" : "",
  },
  {
    label: "Perfil",
    icon: "pi pi-user",
    command: () => router.push("/profile"),
    class: route.path === "/profile" ? "bg-tennis-light text-tennis-dark" : "",
  },
];
</script>

<style scoped>
:deep(.p-sidebar) {
  @apply shadow-lg;
}

:deep(.p-panelmenu .p-panelmenu-header-link) {
  @apply p-3;
}

:deep(.p-panelmenu .p-panelmenu-content .p-menuitem-link) {
  @apply p-3;
}

:deep(.p-panelmenu .p-panelmenu-header-link:focus),
:deep(.p-panelmenu .p-menuitem-link:focus) {
  @apply shadow-none;
}

:deep(.p-panelmenu .p-panelmenu-header-link:hover),
:deep(.p-panelmenu .p-menuitem-link:hover) {
  @apply bg-tennis-light bg-opacity-50;
}
</style>
