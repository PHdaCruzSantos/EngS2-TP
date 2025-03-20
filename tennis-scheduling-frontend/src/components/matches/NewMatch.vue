<template>
  <div class="new-match-container">
    <Toast />
    <div class="card p-4 shadow-lg rounded-lg bg-white">
      <h2 class="text-2xl font-bold mb-4 text-tennis-dark">
        Agendar Nova Partida
      </h2>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <!-- Opponent Selection -->
        <div class="form-group">
          <label for="opponent" class="block text-gray-700 mb-2"
            >Adversário</label
          >
          <Dropdown
            id="opponent"
            v-model="match.playerTwoId"
            :options="opponents"
            optionLabel="name"
            optionValue="id"
            placeholder="Selecione seu adversário"
            class="w-full"
            :class="{ 'p-invalid': v$.playerTwoId.$error }"
            filter
            :showClear="true"
          />
          <small v-if="v$.playerTwoId.$error" class="text-red-500">{{
            v$.playerTwoId.$errors[0].$message
          }}</small>
        </div>

        <!-- Court Selection -->
        <div class="form-group">
          <label for="court" class="block text-gray-700 mb-2">Quadra</label>
          <Dropdown
            id="court"
            v-model="match.courtId"
            :options="courts"
            optionLabel="name"
            optionValue="id"
            placeholder="Selecione a quadra"
            class="w-full"
            :class="{ 'p-invalid': v$.courtId.$error }"
            filter
          />
          <small v-if="v$.courtId.$error" class="text-red-500">{{
            v$.courtId.$errors[0].$message
          }}</small>
        </div>

        <!-- Date and Time -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="form-group">
            <label for="date" class="block text-gray-700 mb-2">Data</label>
            <Calendar
              id="date"
              v-model="selectedDate"
              dateFormat="dd/mm/yy"
              :minDate="today"
              placeholder="Selecione a data"
              class="w-full"
              :class="{ 'p-invalid': v$.startTime.$error }"
              showIcon
            />
            <small v-if="v$.startTime.$error" class="text-red-500">{{
              v$.startTime.$errors[0].$message
            }}</small>
          </div>

          <div class="form-group">
            <label for="startTime" class="block text-gray-700 mb-2"
              >Horário</label
            >
            <div class="flex gap-2">
              <Calendar
                id="startTime"
                v-model="selectedStartTime"
                timeOnly
                hourFormat="24"
                placeholder="Hora início"
                class="flex-1"
                :class="{ 'p-invalid': v$.startTime.$error }"
                @change="calculateEndTime"
              />
              <Calendar
                id="endTime"
                v-model="selectedEndTime"
                timeOnly
                hourFormat="24"
                placeholder="Hora fim"
                class="flex-1"
                :class="{ 'p-invalid': v$.endTime.$error }"
              />
            </div>
            <small v-if="v$.endTime.$error" class="text-red-500">{{
              v$.endTime.$errors[0].$message
            }}</small>
          </div>
        </div>

        <!-- Duration Selection -->
        <div class="form-group">
          <label for="duration" class="block text-gray-700 mb-2">Duração</label>
          <div class="flex flex-wrap gap-2">
            <Button
              v-for="duration in durations"
              :key="duration.value"
              type="button"
              :label="duration.label"
              :class="[
                selectedDuration === duration.value
                  ? 'bg-tennis-green text-white'
                  : 'bg-white text-tennis-dark border-1 border-gray-300',
                'flex-1 min-w-min',
              ]"
              @click="setDuration(duration.value)"
            />
          </div>
        </div>

        <!-- Match Type -->
        <div class="form-group">
          <label for="matchType" class="block text-gray-700 mb-2"
            >Tipo de Partida</label
          >
          <div class="flex gap-2">
            <SelectButton
              v-model="match.type"
              :options="matchTypes"
              optionLabel="label"
              optionValue="value"
              class="w-full"
              disabled
            />
          </div>
        </div>

        <!-- Notes -->
        <div class="form-group">
          <label for="notes" class="block text-gray-700 mb-2"
            >Observações</label
          >
          <Textarea
            id="notes"
            v-model="match.notes"
            rows="3"
            autoResize
            placeholder="Adicione informações relevantes sobre a partida..."
            class="w-full"
          />
        </div>

        <!-- Available Times Info -->
        <div v-if="availableTimes.length > 0" class="p-3 bg-gray-50 rounded-lg">
          <h3 class="text-sm font-semibold text-gray-700 mb-2">
            Horários disponíveis na quadra selecionada:
          </h3>
          <div class="flex flex-wrap gap-1">
            <Chip
              v-for="time in availableTimes"
              :key="time.start"
              :label="`${formatTime(time.start)} - ${formatTime(time.end)}`"
              class="bg-tennis-light text-tennis-dark"
            />
          </div>
        </div>

        <!-- Submit Button -->
        <div class="form-group flex justify-end mt-6 gap-2">
          <Button
            type="button"
            label="Cancelar"
            icon="pi pi-times"
            class="p-button-outlined"
            @click="cancelForm"
          />
          <Button
            type="submit"
            label="Agendar Partida"
            icon="pi pi-calendar-plus"
            class="bg-tennis-green hover:bg-tennis-dark text-white px-2 py-1"
            :loading="loading"
          />
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useToast } from "primevue/usetoast";
import { useVuelidate } from "@vuelidate/core";
import { required } from "@vuelidate/validators";
import { useRouter } from "vue-router";
import matchService from "../../services/match.service";
import userService from "../../services/user.service";
import courtService from "../../services/court.service";
import { useAuthStore } from "../../stores/auth.store";

export default {
  name: "NewMatch",
  emits: ["match-created"],

  setup(props, { emit }) {
    const toast = useToast();
    const router = useRouter();
    const loading = ref(false);
    const authStore = useAuthStore();
    const currentUser = authStore.user || authStore.user?.user;

    if (!authStore.isAuthenticated) {
      toast.add({
        severity: "warn",
        summary: "Atenção",
        detail: "Você precisa estar logado para agendar uma partida",
        life: 3000,
      });
      router.push("/login");
      return {};
    }

    // Form data
    const match = reactive({
      playerOneId: currentUser.user.id || "",
      playerTwoId: "",
      courtId: "",
      startTime: null,
      endTime: null,
      status: "SCHEDULED",
      type: "SINGLES",
      notes: "",
    });

    // Date and time handling
    const today = new Date();
    const selectedDate = ref(null);
    const selectedStartTime = ref(null);
    const selectedEndTime = ref(null);
    const selectedDuration = ref(60); // Default 60 minutes
    const availableTimes = ref([]);

    // Durations options
    const durations = [
      { label: "30 min", value: 30 },
      { label: "60 min", value: 60 },
      { label: "90 min", value: 90 },
      { label: "120 min", value: 120 },
    ];

    // Match types
    const matchTypes = [
      { label: "Individual", value: "SINGLES" },
      { label: "Duplas", value: "DOUBLES" },
    ];

    // Data for dropdowns
    const allPlayers = ref([]);
    const opponents = computed(() => {
      return allPlayers.value.filter((player) => player.id !== currentUser?.id);
    });
    const courts = ref([]);

    // Form validation
    const rules = computed(() => {
      return {
        playerOneId: { required },
        playerTwoId: { required },
        courtId: { required },
        startTime: { required },
        endTime: {
          required,
          afterStartTime: (value) => {
            if (!match.startTime || !value) return true;
            return (
              value > match.startTime ||
              "O horário de término deve ser posterior ao início"
            );
          },
        },
      };
    });

    const v$ = useVuelidate(rules, match);

    // Set duration and calculate end time
    const setDuration = (duration) => {
      selectedDuration.value = duration;
      calculateEndTime();
    };

    const calculateEndTime = () => {
      if (selectedStartTime.value) {
        const endTime = new Date(selectedStartTime.value);
        endTime.setMinutes(endTime.getMinutes() + selectedDuration.value);
        selectedEndTime.value = endTime;
      }
    };

    // Format time for display
    const formatTime = (dateTime) => {
      if (!dateTime) return "";
      const date = new Date(dateTime);
      return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      });
    };

    // Watch for changes in date/time selections and update the match object
    watch([selectedDate, selectedStartTime, selectedEndTime], () => {
      updateMatchTimes();
    });

    const updateMatchTimes = () => {
      if (selectedDate.value && selectedStartTime.value) {
        const startDateTime = new Date(selectedDate.value);
        startDateTime.setHours(
          selectedStartTime.value.getHours(),
          selectedStartTime.value.getMinutes(),
          0
        );
        match.startTime = startDateTime;
      }

      if (selectedDate.value && selectedEndTime.value) {
        const endDateTime = new Date(selectedDate.value);
        endDateTime.setHours(
          selectedEndTime.value.getHours(),
          selectedEndTime.value.getMinutes(),
          0
        );
        match.endTime = endDateTime;
      }
    };

    // Check available times when court and date are selected
    watch([() => match.courtId, selectedDate], async () => {
      if (match.courtId && selectedDate.value) {
        await checkAvailableTimes();
      }
    });

    const checkAvailableTimes = async () => {
      try {
        // Este é um exemplo e depende de você implementar este endpoint
        // const response = await courtService.getAvailableTimes(
        //   match.courtId,
        //   selectedDate.value.toISOString().split('T')[0]
        // );
        // availableTimes.value = response.data;

        // Simulação de horários disponíveis
        const openingTime = new Date(selectedDate.value);
        openingTime.setHours(8, 0, 0);

        const closingTime = new Date(selectedDate.value);
        closingTime.setHours(22, 0, 0);

        const timeSlots = [];
        let currentTime = new Date(openingTime);

        while (currentTime < closingTime) {
          const endTime = new Date(currentTime);
          endTime.setMinutes(endTime.getMinutes() + 60);

          if (endTime <= closingTime) {
            timeSlots.push({
              start: new Date(currentTime),
              end: new Date(endTime),
            });
          }

          currentTime.setMinutes(currentTime.getMinutes() + 60);
        }

        availableTimes.value = timeSlots;
      } catch (error) {
        console.error("Error fetching available times:", error);
      }
    };

    // Load data
    onMounted(async () => {
      try {
        // Load players
        const playerResponse = await userService.getAllUsers();
        allPlayers.value = playerResponse.data;

        // Load courts
        const courtResponse = await courtService.getAllCourts();
        courts.value = courtResponse.data;
        console.log("Courts loaded:", courts.value);
      } catch (error) {
        toast.add({
          severity: "error",
          summary: "Erro",
          detail: "Falha ao carregar dados. Por favor, tente novamente.",
          life: 3000,
        });
        console.error("Error loading data:", error);
      }
    });

    // Form submission
    const handleSubmit = async () => {
      updateMatchTimes();
      console.log(match);
      console.log(currentUser.user.id);

      // Validate form
      const isValid = await v$.value.$validate();
      if (!isValid) return;

      loading.value = true;

      try {
        const response = await matchService.createMatch(match);

        toast.add({
          severity: "success",
          summary: "Sucesso",
          detail: "Partida agendada com sucesso!",
          life: 3000,
        });

        emit("match-created", response.data);
        router.push("/matches");
      } catch (error) {
        let errorMessage = "Erro ao agendar a partida. Tente novamente.";

        if (
          error.response &&
          error.response.data &&
          error.response.data.message
        ) {
          errorMessage = error.response.data.message;
        }

        toast.add({
          severity: "error",
          summary: "Erro",
          detail: errorMessage,
          life: 5000,
        });
        console.error("Error scheduling match:", error);
      } finally {
        loading.value = false;
      }
    };

    const cancelForm = () => {
      router.push("/matches");
    };

    return {
      match,
      opponents,
      courts,
      loading,
      v$,
      today,
      selectedDate,
      selectedStartTime,
      selectedEndTime,
      selectedDuration,
      durations,
      matchTypes,
      availableTimes,
      handleSubmit,
      updateMatchTimes,
      setDuration,
      calculateEndTime,
      formatTime,
      cancelForm,
    };
  },
};
</script>

<style scoped>
/* Component-specific styles */
:deep(.p-dropdown.p-invalid) {
  border-color: #ef4444;
}
:deep(.p-calendar.p-invalid) > .p-inputtext {
  border-color: #ef4444;
}

:deep(.p-button) {
  transition: all 0.3s;
}

:deep(.p-selectbutton) {
  width: 100%;
}

:deep(.p-selectbutton .p-button) {
  flex: 1;
}

:deep(.p-selectbutton .p-button.p-highlight) {
  background-color: var(--tennis-green, #4caf50);
  border-color: var(--tennis-green, #4caf50);
}

:deep(.p-chip) {
  background-color: #f0f9ff;
  color: #0284c7;
}
</style>
