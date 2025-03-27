<template>
  <div class="new-match-view p-4">
    <div class="max-w-3xl mx-auto">
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-tennis-dark mb-2">
          Agendar Partida
        </h1>
        <p class="text-gray-600">
          Preencha os dados abaixo para agendar uma nova partida de tênis
        </p>
      </div>

      <Card class="bg-tennis-light/30">
        <template #content>
          <NewMatch @match-created="onMatchCreated" />
        </template>
      </Card>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import NewMatch from "../../components/matches/NewMatch.vue";

export default defineComponent({
  name: "NewMatchView",
  components: {
    NewMatch,
  },
  setup() {
    const router = useRouter();

    const onMatchCreated = (match) => {
      // Navigate to match details or matches list after creation
      setTimeout(() => {
        router.push({ name: "matches" });
      }, 1500);
    };

    return {
      onMatchCreated,
    };
  },
});
</script>
