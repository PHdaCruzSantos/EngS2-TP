<template>
  <div class="matches-container">
    <Toast />
    <div class="card p-4 shadow-lg rounded-lg bg-white">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold text-tennis-dark">Minhas Partidas</h2>
        <Button
          icon="pi pi-refresh"
          @click="loadMatches"
          class="p-button-outlined p-button-rounded"
          :loading="loading"
        />
      </div>

      <div v-if="loading" class="flex justify-center my-8">
        <ProgressSpinner strokeWidth="4" />
      </div>

      <div v-else-if="matches.length === 0" class="text-center py-8">
        <div class="text-gray-500 mb-4">
          <i class="pi pi-calendar-times text-5xl"></i>
        </div>
        <h3 class="text-xl font-semibold text-gray-700 mb-2">
          Nenhuma partida encontrada
        </h3>
        <p class="text-gray-600 mb-4">Você ainda não tem partidas agendadas.</p>
        <Button
          label="Agendar Partida"
          icon="pi pi-calendar-plus"
          @click="navigateToNewMatch"
          class="bg-tennis-green hover:bg-tennis-dark text-white px-4 py-2"
        />
      </div>

      <div v-else>
        <TabView>
          <TabPanel header="Próximas">
            <div v-if="upcomingMatches.length === 0" class="text-center py-6">
              <p class="text-gray-600">Não há partidas futuras agendadas.</p>
            </div>
            <div v-else class="grid grid-cols-1 gap-4">
              <div
                v-for="match in upcomingMatches"
                :key="match.id"
                class="match-card border rounded-lg p-4 hover:shadow-md transition-shadow border-tennis-dark bg-lime-200"
              >
                <div class="flex justify-between">
                  <div class="match-info">
                    <h3 class="text-lg font-semibold">
                      {{ formatDate(match.date) }} às
                      {{ formatTime(match.time) }}
                    </h3>
                    <p class="text-gray-700">
                      <i class="pi pi-map-marker mr-1"></i> {{ match.location }}
                    </p>
                    <div class="mt-2">
                      <span
                        class="inline-flex items-center gap-1 text-sm font-medium"
                        :class="getStatusColor(match.status)"
                      >
                        <i class="pi" :class="getStatusIcon(match.status)"></i>
                        {{ formatStatus(match.status) }}
                      </span>
                      <span
                        v-if="match.status === 'SCHEDULED'"
                        class="text-xs text-gray-500 ml-2"
                      >
                        ({{ getConfirmationCount(match) }}/{{
                          match.players.length
                        }}
                        confirmações)
                      </span>
                    </div>
                  </div>
                  <div class="match-players">
                    <div class="flex items-center gap-2">
                      <div class="flex flex-col items-center">
                        <Avatar
                          :label="getPlayerInitials(getCurrentUser())"
                          shape="circle"
                          :class="
                            isPlayerConfirmed(match, getCurrentUser().id)
                              ? 'bg-tennis-green text-white'
                              : 'bg-gray-400 text-white'
                          "
                        />
                        <span class="text-xs mt-1">Você</span>
                        <span
                          v-if="isPlayerConfirmed(match, getCurrentUser().id)"
                          class="text-xs text-green-600"
                        >
                          <i class="pi pi-check"></i> Confirmado
                        </span>
                        <span v-else class="text-xs text-yellow-600">
                          <i class="pi pi-clock"></i> Pendente
                        </span>
                      </div>
                      <span class="text-xl px-2 font-bold text-gray-400"
                        >vs</span
                      >
                      <div class="flex flex-col items-center">
                        <Avatar
                          :label="getPlayerInitials(getOpponent(match))"
                          shape="circle"
                          :class="
                            isPlayerConfirmed(match, getOpponent(match).id)
                              ? 'bg-tennis-green text-white'
                              : 'bg-gray-400 text-white'
                          "
                        />
                        <span class="text-xs mt-1">{{
                          getOpponent(match).name || "Adversário"
                        }}</span>
                        <span
                          v-if="isPlayerConfirmed(match, getOpponent(match).id)"
                          class="text-xs text-green-600"
                        >
                          <i class="pi pi-check"></i> Confirmado
                        </span>
                        <span v-else class="text-xs text-yellow-600">
                          <i class="pi pi-clock"></i> Pendente
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="match-actions mt-4 flex justify-end gap-2">
                  <Button
                    v-if="
                      match.status === 'SCHEDULED' &&
                      !isPlayerConfirmed(match, getCurrentUser().id)
                    "
                    icon="pi pi-check"
                    label="Confirmar"
                    class="p-button-success p-button-sm"
                    @click="confirmParticipation(match.id)"
                  />
                  <Button
                    v-if="
                      match.status === 'SCHEDULED' &&
                      isPlayerConfirmed(match, getCurrentUser().id)
                    "
                    icon="pi pi-times"
                    label="Cancelar Confirmação"
                    class="p-button-warning p-button-sm bg-yellow-500 px-1 text-gray-900 hover:bg-yellow-600 hover:text-white"
                    @click="unconfirmParticipation(match.id)"
                  />
                  <Button
                    v-if="['SCHEDULED', 'CONFIRMED'].includes(match.status)"
                    icon="pi pi-times"
                    label="Cancelar Partida"
                    class="p-button-danger p-button-sm bg-red-500 px-1 text-gray-900 hover:bg-red-600 hover:text-white"
                    @click="updateMatchStatus(match.id, 'CANCELLED')"
                  />
                  <Button
                    v-if="match.status === 'CONFIRMED' && isToday(match.date)"
                    icon="pi pi-flag"
                    label="Concluir"
                    class="p-button-info p-button-sm"
                    @click="updateMatchStatus(match.id, 'COMPLETED')"
                  />
                </div>
              </div>
            </div>
          </TabPanel>

          <TabPanel header="Anteriores">
            <div v-if="pastMatches.length === 0" class="text-center py-6">
              <p class="text-gray-600">
                Não há partidas anteriores registradas.
              </p>
            </div>
            <div v-else class="grid grid-cols-1 gap-4">
              <div
                v-for="match in pastMatches"
                :key="match.id"
                class="match-card border rounded-lg p-4 border-tennis-dark bg-lime-200"
              >
                <div class="flex justify-between">
                  <div class="match-info">
                    <h3 class="text-lg font-semibold text-gray-600">
                      {{ formatDate(match.date) }} às
                      {{ formatTime(match.time) }}
                    </h3>
                    <p class="text-gray-500">
                      <i class="pi pi-map-marker mr-1"></i> {{ match.location }}
                    </p>
                    <div class="mt-2">
                      <span
                        class="inline-flex items-center gap-1 text-sm font-medium"
                        :class="getStatusColor(match.status)"
                      >
                        <i class="pi" :class="getStatusIcon(match.status)"></i>
                        {{ formatStatus(match.status) }}
                      </span>
                    </div>
                  </div>
                  <div class="match-players">
                    <div class="flex items-center gap-2">
                      <div class="flex flex-col items-center">
                        <Avatar
                          :label="getPlayerInitials(getCurrentUser())"
                          shape="circle"
                          :class="
                            isPlayerConfirmed(match, getCurrentUser().id)
                              ? 'bg-green-400 text-white'
                              : 'bg-gray-400 text-white'
                          "
                        />
                        <span class="text-xs mt-1 text-gray-600">Você</span>
                      </div>
                      <span class="text-xl font-bold text-gray-400">vs</span>
                      <div class="flex flex-col items-center">
                        <Avatar
                          :label="getPlayerInitials(getOpponent(match))"
                          shape="circle"
                          :class="
                            isPlayerConfirmed(match, getOpponent(match).id)
                              ? 'bg-green-400 text-white'
                              : 'bg-gray-400 text-white'
                          "
                        />
                        <span class="text-xs mt-1 text-gray-600">{{
                          getOpponent(match).name || "Adversário"
                        }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </TabPanel>
        </TabView>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useToast } from "primevue/usetoast";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth.store";
import matchService from "../../services/match.service";

export default {
  name: "UserMatches",

  setup() {
    const toast = useToast();
    const router = useRouter();
    const authStore = useAuthStore();
    const loading = ref(false);
    const matches = ref([]);

    const currentUser = computed(() => {
      return (
        authStore.user.user ||
        (authStore.user?.user ? authStore.user.user : null)
      );
    });

    // Computed properties to filter matches
    const upcomingMatches = computed(() => {
      return matches.value
        .filter((match) => {
          // Check if the match date is today or in the future
          const matchDate = new Date(match.date);
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          return (
            matchDate >= today &&
            ["SCHEDULED", "CONFIRMED"].includes(match.status)
          );
        })
        .sort((a, b) => new Date(a.date) - new Date(b.date));
    });

    const pastMatches = computed(() => {
      return matches.value
        .filter((match) => {
          // Check if the match date is in the past or match is completed/cancelled
          const matchDate = new Date(match.date);
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          return (
            matchDate < today ||
            ["COMPLETED", "CANCELLED"].includes(match.status)
          );
        })
        .sort((a, b) => new Date(b.date) - new Date(a.date)); // Most recent first
    });

    // Load user matches
    const loadMatches = async () => {
      if (!authStore.isAuthenticated) {
        router.push("/login");
        return;
      }

      try {
        loading.value = true;
        const userId = currentUser.value?.id;
        if (!userId) {
          throw new Error("Usuário não identificado");
        }

        const response = await matchService.getMatchesByPlayer(userId);
        matches.value = response.data;
      } catch (error) {
        console.error("Error loading matches:", error);
        toast.add({
          severity: "error",
          summary: "Erro",
          detail: "Falha ao carregar partidas. Por favor, tente novamente.",
          life: 3000,
        });
      } finally {
        loading.value = false;
      }
    };

    // Verifica se um jogador confirmou a partida
    const isPlayerConfirmed = (match, playerId) => {
      return (
        match.confirmedPlayerIds && match.confirmedPlayerIds.includes(playerId)
      );
    };

    // Obter contagem de confirmações
    const getConfirmationCount = (match) => {
      return match.confirmedPlayerIds ? match.confirmedPlayerIds.length : 0;
    };

    // Confirmar participação
    const confirmParticipation = async (matchId) => {
      try {
        const userId = currentUser.value?.id;
        if (!userId) {
          throw new Error("Usuário não identificado");
        }

        await matchService.confirmParticipation(matchId, userId);
        toast.add({
          severity: "success",
          summary: "Sucesso",
          detail: "Você confirmou sua participação na partida",
          life: 3000,
        });
        loadMatches(); // Reload matches after update
      } catch (error) {
        console.error("Error confirming participation:", error);
        toast.add({
          severity: "error",
          summary: "Erro",
          detail: "Falha ao confirmar participação",
          life: 3000,
        });
      }
    };

    // Cancelar confirmação
    const unconfirmParticipation = async (matchId) => {
      try {
        const userId = currentUser.value?.id;
        if (!userId) {
          throw new Error("Usuário não identificado");
        }

        await matchService.unconfirmParticipation(matchId, userId);
        toast.add({
          severity: "success",
          summary: "Sucesso",
          detail: "Você cancelou sua confirmação para esta partida",
          life: 3000,
        });
        loadMatches(); // Reload matches after update
      } catch (error) {
        console.error("Error unconfirming participation:", error);
        toast.add({
          severity: "error",
          summary: "Erro",
          detail: "Falha ao cancelar confirmação",
          life: 3000,
        });
      }
    };

    // Update match status
    const updateMatchStatus = async (matchId, newStatus) => {
      try {
        await matchService.updateMatchStatus(matchId, newStatus);
        toast.add({
          severity: "success",
          summary: "Sucesso",
          detail: `Status da partida atualizado para ${formatStatus(
            newStatus
          )}`,
          life: 3000,
        });
        loadMatches(); // Reload matches after update
      } catch (error) {
        console.error("Error updating match status:", error);
        toast.add({
          severity: "error",
          summary: "Erro",
          detail: "Falha ao atualizar status da partida",
          life: 3000,
        });
      }
    };

    // Helper methods
    const formatDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleDateString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    };

    const formatTime = (timeString) => {
      if (!timeString) return "";
      return timeString.substring(0, 5); // Format HH:mm
    };

    const formatStatus = (status) => {
      if (!status) return "";
      const statusMap = {
        SCHEDULED: "Agendada",
        CONFIRMED: "Confirmada",
        CANCELLED: "Cancelada",
        COMPLETED: "Concluída",
      };
      return statusMap[status] || status;
    };

    const getStatusColor = (status) => {
      if (!status) return "";
      const colorMap = {
        SCHEDULED: "text-yellow-600",
        CONFIRMED: "text-green-600",
        CANCELLED: "text-red-600",
        COMPLETED: "text-blue-600",
      };
      return colorMap[status] || "";
    };

    const getStatusIcon = (status) => {
      if (!status) return "";
      const iconMap = {
        SCHEDULED: "pi-clock",
        CONFIRMED: "pi-check-circle",
        CANCELLED: "pi-times-circle",
        COMPLETED: "pi-flag",
      };
      return iconMap[status] || "";
    };

    const getCurrentUser = () => {
      return currentUser.value || { id: "", name: "Você" };
    };

    const getOpponent = (match) => {
      if (!match.players || match.players.length < 2) {
        return { id: "", name: "Adversário" };
      }
      const userId = currentUser.value?.id;
      return (
        match.players.find((player) => player.id !== userId) || {
          id: "",
          name: "Adversário",
        }
      );
    };

    const getPlayerInitials = (player) => {
      if (!player || !player.name) return "?";
      return player.name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
        .substring(0, 2);
    };

    const isToday = (dateString) => {
      if (!dateString) return false;
      const date = new Date(dateString);
      const today = new Date();
      return (
        date.getDate() === today.getDate() &&
        date.getMonth() === today.getMonth() &&
        date.getFullYear() === today.getFullYear()
      );
    };

    const navigateToNewMatch = () => {
      router.push("/agendamento");
    };

    onMounted(() => {
      loadMatches();
    });

    return {
      loading,
      matches,
      upcomingMatches,
      pastMatches,
      loadMatches,
      isPlayerConfirmed,
      getConfirmationCount,
      confirmParticipation,
      unconfirmParticipation,
      updateMatchStatus,
      formatDate,
      formatTime,
      formatStatus,
      getStatusColor,
      getStatusIcon,
      getCurrentUser,
      getOpponent,
      getPlayerInitials,
      isToday,
      navigateToNewMatch,
    };
  },
};
</script>

<style scoped>
.match-card {
  transition: all 0.2s ease;
}
.match-card:hover {
  transform: translateY(-2px);
}
</style>
