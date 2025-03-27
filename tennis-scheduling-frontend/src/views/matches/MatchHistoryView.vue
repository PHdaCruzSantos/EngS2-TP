<template>
  <div class="new-match-view p-4">
    <div class="max-w-3xl mx-auto">
      <div class="mb-6">
        <!-- Go back btn -->
        <router-link
          to="/matches"
          class="flex items-center text-tennis-dark hover:text-tennis-dark-dark"
        >
          <i class="pi pi-angle-left mr-2"></i>
          <span> Voltar</span>
        </router-link>
        <!-- Title -->
        <h1 class="text-3xl font-bold text-tennis-dark mb-2">
          Historico de partidas
        </h1>
      </div>

      <Card class="bg-tennis-light/30">
        <template #content>
          <MatchHistory />
        </template>
      </Card>
    </div>
  </div>
</template>

<script>
import MatchHistory from "../../components/matches/MatchHistory.vue";

export default {
  name: "MatchHistoryView",
  components: {
    MatchHistory,
  },
  setup() {
    return {};
  },
};
</script>
