<template>
  <Layout>
    <div class="space-y-6">
      <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold text-tennis-dark">Minhas Partidas</h1>
        <Button
          icon="pi pi-plus"
          label="Nova Partida"
          class="p-button-primary"
          @click="router.push('/matches/new')"
        />
      </div>
      <UserMatches />
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import Layout from "../../components/shared/Layout.vue";
import UserMatches from "../../components/matches/UserMatches.vue";
import Button from "primevue/button";

const router = useRouter();
</script>
