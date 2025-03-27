<template>
  <div class="notifications-container p-6 max-w-3xl mx-auto">
    <Card>
      <template #title>
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold">Notificações</h2>
          <div class="flex items-center space-x-2">
            <Badge
              :value="unreadCount"
              severity="danger"
              v-if="unreadCount > 0"
            ></Badge>
            <Button
              v-if="notifications.length > 0"
              icon="pi pi-check"
              severity="secondary"
              text
              rounded
              @click="markAllAsRead"
              tooltip="Marcar todas como lidas"
            />
            <Button
              v-if="notifications.length > 0"
              icon="pi pi-trash"
              severity="danger"
              text
              rounded
              @click="confirmClearAllNotifications"
              tooltip="Limpar todas as notificações"
            />
          </div>
        </div>
      </template>
      <template #content>
        <div
          v-if="notifications.length === 0"
          class="text-center text-gray-500 p-4"
        >
          <i class="pi pi-inbox text-4xl mb-4 block"></i>
          <p>Você não tem notificações</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="flex items-center p-4 border-b last:border-b-0 hover:bg-gray-50 transition-colors"
            :class="{
              'bg-blue-50': !notification.read,
              'text-gray-600': notification.read,
            }"
          >
            <div class="flex-grow">
              <div class="flex items-center mb-1">
                <span class="font-semibold mr-2">
                  {{ notification.title }}
                </span>
                <span
                  v-if="!notification.read"
                  class="text-xs bg-blue-500 text-white px-2 py-1 rounded-full"
                >
                  Novo
                </span>
              </div>
              <p class="text-sm">{{ notification.message }}</p>
              <small class="text-gray-500">
                {{ formatDate(notification.createdAt) }}
              </small>
            </div>
            <div class="flex items-center space-x-2">
              <Button
                v-if="!notification.read"
                icon="pi pi-check"
                severity="secondary"
                text
                rounded
                @click="markNotificationAsRead(notification.id)"
                tooltip="Marcar como lida"
              />
              <Button
                icon="pi pi-trash"
                severity="danger"
                text
                rounded
                @click="confirmDeleteNotification(notification.id)"
                tooltip="Excluir notificação"
              />
            </div>
          </div>
        </div>
      </template>
      <template #footer>
        <div class="flex justify-between items-center">
          <small class="text-gray-500">
            Total de notificações: {{ notifications.length }}
          </small>
          <Paginator
            :rows="10"
            :totalRecords="totalNotifications"
            @page="onPageChange"
          ></Paginator>
        </div>
      </template>
    </Card>

    <ConfirmDialog />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";
import { useAuthStore } from "../../stores/auth.store";

// PrimeVue Components
import Card from "primevue/card";
import Button from "primevue/button";
import Badge from "primevue/badge";
import Paginator from "primevue/paginator";
import ConfirmDialog from "primevue/confirmdialog";

// Services
import NotificationService from "../../services/notification.service";

const toast = useToast();
const confirm = useConfirm();
const authStore = useAuthStore();

// Reactive state
const notifications = ref([]);
const totalNotifications = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// Computed properties
const unreadCount = computed(
  () => notifications.value.filter((n) => !n.read).length
);

// Lifecycle hooks
onMounted(async () => {
  await fetchNotifications();
});

// Methods
const fetchNotifications = async (page = 1) => {
  try {
    const userId = authStore.user.user.id;
    console.log(userId);
    if (!userId) {
      throw new Error("Usuário não autenticado");
    }

    const response = await NotificationService.getAllNotifications(userId, {
      page: page - 1,
      size: pageSize.value,
    });

    notifications.value = response.data.content;
    totalNotifications.value = response.data.totalElements;
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Erro",
      detail: "Não foi possível carregar as notificações",
      life: 3000,
    });
  }
};

const markNotificationAsRead = async (notificationId) => {
  try {
    await NotificationService.markAsRead(notificationId);

    // Update local state
    const notification = notifications.value.find(
      (n) => n.id === notificationId
    );
    if (notification) {
      notification.read = true;
    }

    toast.add({
      severity: "success",
      summary: "Sucesso",
      detail: "Notificação marcada como lida",
      life: 2000,
    });
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Erro",
      detail: "Não foi possível marcar a notificação",
      life: 3000,
    });
  }
};

const markAllAsRead = async () => {
  try {
    const userId = authStore.user?.id;
    await NotificationService.markAllAsRead(userId);

    // Update local state
    notifications.value.forEach((n) => (n.read = true));

    toast.add({
      severity: "success",
      summary: "Sucesso",
      detail: "Todas as notificações marcadas como lidas",
      life: 2000,
    });
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Erro",
      detail: "Não foi possível marcar todas as notificações",
      life: 3000,
    });
  }
};

const confirmDeleteNotification = (notificationId) => {
  confirm.require({
    message: "Tem certeza que deseja excluir esta notificação?",
    header: "Confirmação de Exclusão",
    icon: "pi pi-exclamation-triangle",
    accept: () => deleteNotification(notificationId),
  });
};

const deleteNotification = async (notificationId) => {
  try {
    await NotificationService.deleteNotification(notificationId);

    // Remove notification from local state
    notifications.value = notifications.value.filter(
      (n) => n.id !== notificationId
    );
    totalNotifications.value--;

    toast.add({
      severity: "success",
      summary: "Sucesso",
      detail: "Notificação excluída",
      life: 2000,
    });
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Erro",
      detail: "Não foi possível excluir a notificação",
      life: 3000,
    });
  }
};

const confirmClearAllNotifications = () => {
  confirm.require({
    message: "Tem certeza que deseja limpar todas as notificações?",
    header: "Confirmação de Limpeza",
    icon: "pi pi-exclamation-triangle",
    accept: clearAllNotifications,
  });
};

const clearAllNotifications = async () => {
  try {
    const userId = authStore.user?.id;
    await NotificationService.clearAllNotifications(userId);

    // Clear local state
    notifications.value = [];
    totalNotifications.value = 0;

    toast.add({
      severity: "success",
      summary: "Sucesso",
      detail: "Todas as notificações foram excluídas",
      life: 2000,
    });
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Erro",
      detail: "Não foi possível limpar as notificações",
      life: 3000,
    });
  }
};

const onPageChange = (event) => {
  currentPage.value = event.page + 1;
  fetchNotifications(currentPage.value);
};

const formatDate = (date) => {
  if (!date) return "";
  return new Date(date).toLocaleString("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });
};
</script>
