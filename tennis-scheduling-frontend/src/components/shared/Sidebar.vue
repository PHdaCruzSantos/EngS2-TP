<template>
  <aside
    v-show="sidebarVisible"
    class="fixed top-[64px] left-0 h-[calc(100vh-64px)] w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out z-20"
    :class="{
      '-translate-x-full': !sidebarVisible,
      'translate-x-0': sidebarVisible,
    }"
  >
    <div class="p-4 bg-tennis-green text-white">
      <h2 class="text-lg font-semibold">Menu</h2>
    </div>
    <div class="p-4">
      <PanelMenu :model="menuItems" class="border-none" />
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
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
console.log("Sidebar visibility changed:", sidebarVisible.value);

watch(
  () => props.modelValue,
  (newValue) => {
    sidebarVisible.value = newValue;
  }
);

const menuItems = [
  {
    label: "Dashboard",
    icon: "pi pi-home",
    command: () => router.push("/matches"),
    class:
      route.path === "/matches"
        ? "bg-tennis-dark text-tennis-dark border-spacing-1"
        : "",
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
      route.path === "/notifications" ? "bg-tennis-dark text-tennis-dark" : "",
  },
  {
    label: "Perfil",
    icon: "pi pi-user",
    command: () => router.push("/profile"),
    class: route.path === "/profile" ? "bg-tennis-dark text-tennis-dark" : "",
  },
];
</script>

<style scoped lang="postcss">
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
