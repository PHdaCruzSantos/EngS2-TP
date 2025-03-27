<template>
  <div class="match-history">
    <Card>
      <template #title>
        <div class="flex justify-between items-center">
          <span>Histórico de Partidas</span>
          <Button
            icon="pi pi-refresh"
            class="p-button-text p-button-secondary"
            @click="fetchMatchHistory"
            :loading="loading"
          />
        </div>
      </template>
      <template #content>
        <div v-if="loading" class="flex justify-content-center">
          <ProgressSpinner />
        </div>
        <div v-else-if="pastMatches.length === 0" class="text-center p-4">
          <i class="pi pi-inbox text-4xl text-gray-400 mb-4 block"></i>
          <p class="text-gray-600">Nenhuma partida anterior encontrada.</p>
        </div>
        <div v-else>
          <DataTable
            :value="pastMatches"
            :rows="5"
            paginator
            :rowsPerPageOptions="[5, 10, 15]"
            responsiveLayout="scroll"
            class="p-datatable-striped"
          >
            <Column header="Data" class="w-2">
              <template #body="{ data }">
                <div class="flex align-items-center">
                  <i class="pi pi-calendar mr-2 text-primary"></i>
                  {{ formatDate(data.startTime) }}
                </div>
              </template>
            </Column>
            <Column header="Oponente" class="w-3">
              <template #body="{ data }">
                <div class="flex align-items-center">
                  <Avatar
                    :label="getPlayerInitials(getOpponentId(data))"
                    class="mr-2"
                    size="large"
                  />
                  <span>{{ getOpponentName(data) }}</span>
                </div>
              </template>
            </Column>
            <Column header="Quadra" class="w-2">
              <template #body="{ data }">
                <div class="flex align-items-center">
                  <i class="pi pi-map-marker mr-2 text-green-600"></i>
                  {{ data.courtId }}
                </div>
              </template>
            </Column>
            <Column header="Resultado" class="w-2">
              <template #body="{ data }">
                <Tag
                  :severity="getResultSeverity(data.status)"
                  :value="formatStatus(data.status)"
                  rounded
                />
              </template>
            </Column>
            <Column header="Ações" class="w-1">
              <template #body="{ data }">
                <Button
                  icon="pi pi-eye"
                  class="p-button-rounded p-button-info p-button-text"
                  @click="showMatchDetails(data)"
                />
              </template>
            </Column>
          </DataTable>
        </div>
      </template>
    </Card>

    <Dialog
      v-model:visible="matchDetailsVisible"
      :header="selectedMatch ? 'Detalhes da Partida' : ''"
      :modal="true"
      :style="{ width: '500px' }"
    >
      <div v-if="selectedMatch" class="match-details">
        <div class="grid">
          <div class="col-6 flex align-items-center">
            <i class="pi pi-calendar mr-2 text-primary"></i>
            <strong>Data:</strong> {{ formatDate(selectedMatch.startTime) }}
          </div>
          <div class="col-6 flex align-items-center">
            <i class="pi pi-clock mr-2 text-orange-600"></i>
            <strong>Horário:</strong> {{ formatTime(selectedMatch.startTime) }}
          </div>

          <div class="col-12 flex align-items-center mt-3">
            <i class="pi pi-map-marker mr-2 text-green-600"></i>
            <strong>Quadra:</strong> {{ selectedMatch.courtId }}
          </div>

          <div class="col-12 mt-3">
            <strong>Jogadores:</strong>
            <div class="flex align-items-center justify-content-center mt-2">
              <div class="flex flex-column align-items-center mr-4">
                <Avatar
                  :label="getPlayerInitials(selectedMatch.playerOneId)"
                  size="xlarge"
                  class="mb-2"
                />
                <span>{{ getUserName(selectedMatch.playerOneId) }}</span>
              </div>
              <div class="text-2xl mx-4">VS</div>
              <div class="flex flex-column align-items-center ml-4">
                <Avatar
                  :label="getPlayerInitials(selectedMatch.playerTwoId)"
                  size="xlarge"
                  class="mb-2"
                />
                <span>{{ getUserName(selectedMatch.playerTwoId) }}</span>
              </div>
            </div>
          </div>

          <div
            class="col-12 mt-3 flex align-items-center justify-content-center"
          >
            <strong>Status:</strong>
            <Tag
              :severity="getResultSeverity(selectedMatch.status)"
              :value="formatStatus(selectedMatch.status)"
              rounded
              class="ml-2"
            />
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script>
import { ref } from "vue";
import { useAuthStore } from "../../stores/auth.store";
import { storeToRefs } from "pinia";
import MatchService from "../../services/match.service";

import Card from "primevue/card";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Button from "primevue/button";
import Dialog from "primevue/dialog";
import Tag from "primevue/tag";
import ProgressSpinner from "primevue/progressspinner";
import Avatar from "primevue/avatar";

export default {
  name: "MatchHistory",

  components: {
    Card,
    DataTable,
    Column,
    Button,
    Dialog,
    Tag,
    ProgressSpinner,
    Avatar,
  },

  data() {
    return {
      pastMatches: [],
      loading: true,
      matchDetailsVisible: false,
      selectedMatch: null,
    };
  },

  setup() {
    const userStore = useAuthStore();
    const { currentUser } = storeToRefs(userStore);
    return { userStore, currentUser };
  },

  mounted() {
    this.fetchMatchHistory();
  },

  methods: {
    async fetchMatchHistory() {
      try {
        this.loading = true;
        const userStore = useAuthStore();
        const userId = userStore.user.user.id;
        console.log("User ID:", userStore.user.user.id);

        if (!userId) {
          throw new Error("Usuário não autenticado");
        }

        const response = await MatchService.getMatchesByPlayer(userId);
        this.pastMatches = response.data
          .filter((match) => new Date(match.endTime) < new Date())
          .sort((a, b) => new Date(b.startTime) - new Date(a.startTime));
      } catch (error) {
        console.error("Erro ao buscar histórico de partidas:", error);
      } finally {
        this.loading = false;
      }
    },

    formatDate(date) {
      return new Date(date).toLocaleDateString("pt-BR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    },

    formatTime(date) {
      return new Date(date).toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });
    },

    getOpponentId(match) {
      return match.playerOneId === this.currentUser?.id
        ? match.playerTwoId
        : match.playerOneId;
    },

    getOpponentName(match) {
      const opponentId = this.getOpponentId(match);
      return this.getUserName(opponentId);
    },

    getUserName(userId) {
      // Implement user name retrieval from user store or service
      return "Jogador";
    },

    getPlayerInitials(userId) {
      const userName = this.getUserName(userId);
      return userName
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
        .slice(0, 2);
    },

    formatStatus(status) {
      const statusMap = {
        COMPLETED: "Concluído",
        CANCELLED: "Cancelado",
        SCHEDULED: "Agendado",
      };
      return statusMap[status] || status;
    },

    getResultSeverity(status) {
      const severityMap = {
        COMPLETED: "success",
        CANCELLED: "warning",
        SCHEDULED: "info",
      };
      return severityMap[status] || "info";
    },

    showMatchDetails(match) {
      this.selectedMatch = match;
      this.matchDetailsVisible = true;
    },
  },
};
</script>

<style scoped>
.match-history {
  max-width: 900px;
  margin: 0 auto;
}
</style>
