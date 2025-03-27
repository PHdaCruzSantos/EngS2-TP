<template>
  <div class="scheduled-matches-calendar">
    <Card>
      <template #title>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">Minhas Partidas Agendadas</h2>
          <div class="flex gap-2">
            <Button
              icon="pi pi-calendar-plus"
              class="p-button-outlined p-button-primary"
              label="Nova Partida"
              @click="router.push('/matches/new')"
            />
            <Button
              icon="pi pi-refresh"
              class="p-button-text p-button-secondary"
              @click="fetchScheduledMatches"
              :loading="loading"
            />
          </div>
        </div>
      </template>
      <template #content>
        <div v-if="loading" class="flex justify-content-center">
          <ProgressSpinner />
        </div>
        <Calendar
          v-else
          :events="matchEvents"
          :inline="true"
          selectionMode="multiple"
          :showWeek="true"
          class="w-full"
        >
          <template #date="slotProps">
            <div
              class="custom-calendar-day"
              :class="{
                'has-matches': hasMatchesOnDate(slotProps.date),
                'current-month': slotProps.view === 'date',
              }"
            >
              {{ slotProps.date.day }}
              <div
                v-if="hasMatchesOnDate(slotProps.date)"
                class="match-indicators"
              >
                <Badge
                  v-for="match in getMatchesOnDate(slotProps.date)"
                  :key="match.id"
                  :severity="getMatchBadgeSeverity(match)"
                  size="small"
                  class="mr-1"
                />
              </div>
            </div>
          </template>
        </Calendar>

        <div v-if="selectedDateMatches.length" class="mt-4">
          <h3 class="text-lg font-semibold mb-3">
            Partidas em {{ formatSelectedDate }}
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
            <Card
              v-for="match in selectedDateMatches"
              :key="match.id"
              class="match-card"
            >
              <template #content>
                <div class="flex justify-between items-center">
                  <div class="flex items-center">
                    <Avatar
                      :label="getPlayerInitials(getOpponentId(match))"
                      class="mr-3"
                      size="large"
                    />
                    <div>
                      <div class="font-bold">
                        {{ getOpponentName(match) }}
                      </div>
                      <small class="text-gray-500">
                        {{ formatMatchTime(match) }}
                      </small>
                    </div>
                  </div>
                  <div>
                    <Tag
                      :severity="getMatchTagSeverity(match)"
                      :value="formatMatchStatus(match.status)"
                      rounded
                    />
                  </div>
                </div>
                <div class="mt-3 flex justify-between items-center">
                  <div class="flex items-center">
                    <i class="pi pi-map-marker mr-2 text-green-600"></i>
                    <span>{{ match.courtId }}</span>
                  </div>
                  <Button
                    icon="pi pi-eye"
                    class="p-button-rounded p-button-info p-button-text"
                    @click="showMatchDetails(match)"
                  />
                </div>
              </template>
            </Card>
          </div>
        </div>
      </template>
    </Card>

    <Dialog
      v-model:visible="matchDetailsVisible"
      :header="'Detalhes da Partida'"
      :modal="true"
      :style="{ width: '500px' }"
    >
      <div v-if="selectedMatch" class="match-details">
        <!-- Add match details content here -->
      </div>
    </Dialog>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/auth.store";
import { storeToRefs } from "pinia";

import Card from "primevue/card";
import Calendar from "primevue/calendar";
import Button from "primevue/button";
import Dialog from "primevue/dialog";
import Avatar from "primevue/avatar";
import Tag from "primevue/tag";
import Badge from "primevue/badge";
import ProgressSpinner from "primevue/progressspinner";

import MatchService from "../../services/match.service";

export default {
  name: "MatchsShedule",

  components: {
    Card,
    Calendar,
    Button,
    Dialog,
    Avatar,
    Tag,
    Badge,
    ProgressSpinner,
  },

  data() {
    return {
      scheduledMatches: [],
      loading: true,
      selectedDate: null,
      selectedMatch: null,
      matchDetailsVisible: false,
      router: useRouter(),
    };
  },

  computed: {
    matchEvents() {
      return this.scheduledMatches.map((match) => ({
        date: new Date(match.startTime),
        match: match,
      }));
    },

    selectedDateMatches() {
      if (!this.selectedDate) return [];
      return this.scheduledMatches.filter((match) =>
        this.isSameDay(new Date(match.startTime), this.selectedDate)
      );
    },

    formatSelectedDate() {
      return this.selectedDate
        ? this.selectedDate.toLocaleDateString("pt-BR", {
            day: "2-digit",
            month: "long",
            year: "numeric",
          })
        : "";
    },
  },

  mounted() {
    this.fetchScheduledMatches();
  },

  methods: {
    async fetchScheduledMatches() {
      try {
        this.loading = true;
        const store = useAuthStore();
        const userId = store.user.user?.id;

        if (!userId) {
          throw new Error("Usuário não autenticado");
        }

        const response = await MatchService.getMatchesByPlayer(userId);
        this.scheduledMatches = response.data
          .filter((match) => new Date(match.startTime) >= new Date())
          .sort((a, b) => new Date(a.startTime) - new Date(b.startTime));
      } catch (error) {
        console.error("Erro ao buscar partidas agendadas:", error);
      } finally {
        this.loading = false;
      }
    },

    hasMatchesOnDate(date) {
      return this.scheduledMatches.some((match) =>
        this.isSameDay(new Date(match.startTime), date.date)
      );
    },

    getMatchesOnDate(date) {
      return this.scheduledMatches.filter((match) =>
        this.isSameDay(new Date(match.startTime), date.date)
      );
    },

    isSameDay(date1, date2) {
      return (
        date1.getFullYear() === date2.getFullYear() &&
        date1.getMonth() === date2.getMonth() &&
        date1.getDate() === date2.getDate()
      );
    },

    createNewMatch() {
      const router = useRouter();
      router.push("/matches/new");
    },

    getOpponentId(match) {
      const store = useAuthStore();
      return match.playerOneId === store.user?.id
        ? match.playerTwoId
        : match.playerOneId;
    },

    getOpponentName(match) {
      const opponentId = this.getOpponentId(match);
      return this.getUserName(opponentId);
    },

    getUserName(userId) {
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

    formatMatchTime(match) {
      return new Date(match.startTime).toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });
    },

    formatMatchStatus(status) {
      const statusMap = {
        SCHEDULED: "Agendado",
        CONFIRMED: "Confirmado",
        PENDING: "Pendente",
      };
      return statusMap[status] || status;
    },

    getMatchTagSeverity(match) {
      const severityMap = {
        SCHEDULED: "info",
        CONFIRMED: "success",
        PENDING: "warning",
      };
      return severityMap[match.status] || "info";
    },

    getMatchBadgeSeverity(match) {
      const severityMap = {
        SCHEDULED: "secondary",
        CONFIRMED: "success",
        PENDING: "warning",
      };
      return severityMap[match.status] || "secondary";
    },

    showMatchDetails(match) {
      this.selectedMatch = match;
      this.matchDetailsVisible = true;
    },
  },
};
</script>

<style scoped>
.scheduled-matches-calendar {
  max-width: 900px;
  margin: 0 auto;
}

.custom-calendar-day {
  position: relative;
  height: 100%;
}

.custom-calendar-day.has-matches {
  font-weight: bold;
  color: var(--primary-color);
}

.match-indicators {
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
}

.match-card {
  cursor: pointer;
  transition: box-shadow 0.3s ease;
}

.match-card:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
</style>
