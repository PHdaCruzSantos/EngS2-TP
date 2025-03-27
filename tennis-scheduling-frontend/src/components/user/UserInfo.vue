<template>
  <div class="user-profile-edit p-6 max-w-3xl mx-auto">
    <Card>
      <template #title>
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold">Perfil do Usuário</h2>
          <Button
            icon="pi pi-pencil"
            severity="secondary"
            text
            @click="toggleEditMode"
            class="p-button-outlined w-28"
          >
            {{ isEditMode ? "Cancelar" : "Editar" }}
          </Button>
        </div>
      </template>
      <template #content>
        <form @submit.prevent="updateProfile" class="space-y-4">
          <!-- Profile Picture Section -->
          <div class="flex flex-col items-center mb-4">
            <div class="relative">
              <Avatar
                :image="
                  profileImagePreview || user.avatarUrl || '/default-avatar.png'
                "
                size="xlarge"
                shape="circle"
                class="mb-3 shadow-lg"
              />

              <FileUpload
                v-if="isEditMode"
                mode="basic"
                name="avatar"
                accept="image/*"
                @select="onFileSelect"
                chooseLabel="Alterar Foto"
                class="absolute bottom-0 right-0"
              />
            </div>
          </div>

          <!-- User Information Display and Edit -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex flex-col">
              <label class="text-gray-600 mb-1">Nome Completo</label>
              <div v-if="!isEditMode" class="font-semibold text-gray-800">
                {{ user.name || "Não informado" }}
              </div>
              <InputText
                v-else
                v-model="formData.name"
                :class="{ 'p-invalid': v$.name.$error }"
                @blur="v$.name.$touch()"
              />
              <small v-if="isEditMode && v$.name.$error" class="p-error">
                {{ v$.name.$errors[0].$message }}
              </small>
            </div>

            <div class="flex flex-col">
              <label class="text-gray-600 mb-1">E-mail</label>
              <div v-if="!isEditMode" class="font-semibold text-gray-800">
                {{ user.email || "Não informado" }}
              </div>
              <InputText
                v-else
                v-model="formData.email"
                :class="{ 'p-invalid': v$.email.$error }"
                @blur="v$.email.$touch()"
              />
              <small v-if="isEditMode && v$.email.$error" class="p-error">
                {{ v$.email.$errors[0].$message }}
              </small>
            </div>
          </div>

          <!-- Additional Information Section -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
            <div class="flex flex-col">
              <label class="text-gray-600 mb-1">Telefone</label>
              <div v-if="!isEditMode" class="font-semibold text-gray-800">
                {{ formatPhone(user.phone) || "Não informado" }}
              </div>
              <InputMask
                v-else
                v-model="formData.phone"
                mask="(99) 99999-9999"
                placeholder="(00) 00000-0000"
              />
            </div>

            <div class="flex flex-col">
              <label class="text-gray-600 mb-1">Data de Nascimento</label>
              <div v-if="!isEditMode" class="font-semibold text-gray-800">
                {{ formatDate(user.birthdate) || "Não informado" }}
              </div>
              <Calendar
                v-else
                v-model="formData.birthdate"
                dateFormat="dd/mm/yy"
                showIcon
              />
            </div>
          </div>

          <!-- Change Password Section (Only in Edit Mode) -->
          <div v-if="isEditMode" class="mt-4">
            <Divider align="center">
              <span class="text-gray-500">Alterar Senha</span>
            </Divider>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="flex flex-col">
                <label for="currentPassword" class="mb-2">Senha Atual</label>
                <Password
                  id="currentPassword"
                  v-model="formData.currentPassword"
                  :feedback="false"
                  toggleMask
                />
              </div>
              <div class="flex flex-col">
                <label for="newPassword" class="mb-2">Nova Senha</label>
                <Password
                  id="newPassword"
                  v-model="formData.newPassword"
                  :rules="passwordRules"
                  :weakLabel="'Fraca'"
                  :mediumLabel="'Média'"
                  :strongLabel="'Forte'"
                  toggleMask
                />
              </div>
            </div>
          </div>

          <!-- Submit Button (Only in Edit Mode) -->
          <div v-if="isEditMode" class="flex justify-end mt-6">
            <Button
              label="Salvar Alterações"
              type="submit"
              :loading="isSubmitting"
              icon="pi pi-save"
            />
          </div>
        </form>
      </template>
    </Card>

    <ConfirmDialog />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useVuelidate } from "@vuelidate/core";
import { required, email, minLength } from "@vuelidate/validators";
import { useAuthStore } from "../../stores/auth.store";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";

// Import PrimeVue components
import Avatar from "primevue/avatar";
import FileUpload from "primevue/fileupload";
import InputText from "primevue/inputtext";
import Password from "primevue/password";
import Divider from "primevue/divider";
import InputMask from "primevue/inputmask";
import Calendar from "primevue/calendar";
import Button from "primevue/button";
import Card from "primevue/card";
import ConfirmDialog from "primevue/confirmdialog";

// Confirm service setup
const confirm = useConfirm();
const toast = useToast();
const authStore = useAuthStore();

const user = computed(() => authStore.user || {});
const isSubmitting = ref(false);
const isEditMode = ref(false);
const profileImagePreview = ref(null);
const profileImageFile = ref(null);

const formData = ref({
  name: "",
  email: "",
  currentPassword: "",
  newPassword: "",
  phone: "",
  birthdate: null,
});

const rules = {
  name: { required },
  email: { required, email },
  currentPassword: {},
  newPassword: {
    minLength: minLength(6),
  },
  phone: {},
  birthdate: {},
};

const v$ = useVuelidate(rules, formData);

onMounted(() => {
  initializeFormData();
});

const initializeFormData = () => {
  formData.value = {
    name: user.value?.name || "",
    email: user.value?.email || "",
    currentPassword: "",
    newPassword: "",
    phone: user.value?.phone || "",
    birthdate: user.value?.birthdate ? new Date(user.value.birthdate) : null,
  };
};

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value;
  if (!isEditMode.value) {
    // Reset form data when cancelling edit
    initializeFormData();
    profileImagePreview.value = null;
    profileImageFile.value = null;
  }
};

const formatPhone = (phone) => {
  if (!phone) return "";
  return phone.replace(/(\d{2})(\d{5})(\d{4})/, "($1) $2-$3");
};

const formatDate = (date) => {
  if (!date) return "";
  const parsedDate = new Date(date);
  return parsedDate.toLocaleDateString("pt-BR");
};

const onFileSelect = (event) => {
  const file = event.files[0];
  profileImageFile.value = file;

  const reader = new FileReader();
  reader.onload = (e) => {
    profileImagePreview.value = e.target.result;
  };
  reader.readAsDataURL(file);
};

const updateProfile = async () => {
  const isFormValid = await v$.value.$validate();

  if (!isFormValid) {
    toast.add({
      severity: "error",
      summary: "Erro",
      detail: "Por favor, verifique os campos do formulário",
      life: 3000,
    });
    return;
  }

  confirm.require({
    message: "Tem certeza que deseja atualizar seu perfil?",
    header: "Confirmação",
    icon: "pi pi-exclamation-triangle",
    accept: async () => {
      try {
        isSubmitting.value = true;

        const updateData = {
          name: formData.value.name,
          email: formData.value.email,
          phone: formData.value.phone,
          birthdate: formData.value.birthdate,
        };

        if (formData.value.currentPassword && formData.value.newPassword) {
          updateData.currentPassword = formData.value.currentPassword;
          updateData.newPassword = formData.value.newPassword;
        }

        if (profileImageFile.value) {
          const imageFormData = new FormData();
          imageFormData.append("avatar", profileImageFile.value);
          // Implement file upload logic here
        }

        await authStore.updateUser(user.value.id, updateData);

        toast.add({
          severity: "success",
          summary: "Sucesso",
          detail: "Perfil atualizado com sucesso",
          life: 3000,
        });

        // Exit edit mode after successful update
        isEditMode.value = false;
      } catch (error) {
        toast.add({
          severity: "error",
          summary: "Erro",
          detail: error.message || "Não foi possível atualizar o perfil",
          life: 3000,
        });
      } finally {
        isSubmitting.value = false;
      }
    },
    reject: () => {
      // Optional: Handle rejection if needed
    },
  });
};
</script>

<style scoped>
:deep(.p-fileupload-basic) {
  display: none;
}
</style>
