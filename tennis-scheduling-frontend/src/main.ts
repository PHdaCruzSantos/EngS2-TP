import { createApp } from "vue";
import { createPinia } from "pinia";
import PrimeVue from "primevue/config";
import App from "./App.vue";
import router from "./router";

import Menubar from "primevue/menubar";
import Menu from "primevue/menu";
import Avatar from "primevue/avatar";
import Badge from "primevue/badge";
import Button from "primevue/button";
import Sidebar from "primevue/sidebar";
import PanelMenu from "primevue/panelmenu";
import Dialog from "primevue/dialog";
import Dropdown from "primevue/dropdown";
import Calendar from "primevue/calendar";
import Card from "primevue/card";
import Toast from "primevue/toast";
import ToastService from "primevue/toastservice";
import SelectButton from "primevue/selectbutton";
import TabPanel from "primevue/tabpanel";
import TabView from "primevue/tabview";
import ProgressSpinner from "primevue/progressspinner";
import ConfirmDialog from "primevue/confirmdialog";
import ConfirmationService from "primevue/confirmationservice";

import "./assets/styles/main.css";
import "primevue/resources/themes/lara-light-green/theme.css";
import "primeicons/primeicons.css";

const app = createApp(App);

app.use(PrimeVue, { ripple: true });

// Register PrimeVue components
app.component("Menubar", Menubar);
app.component("Menu", Menu);
app.component("Avatar", Avatar);
app.component("Badge", Badge);
app.component("Button", Button);
app.component("Sidebar", Sidebar);
app.component("PanelMenu", PanelMenu);
app.component("Dialog", Dialog);
app.component("Dropdown", Dropdown);
app.component("Calendar", Calendar);
app.component("Card", Card);
app.component("Toast", Toast);
app.component("SelectButton", SelectButton);
app.component("TabPanel", TabPanel);
app.component("TabView", TabView);
app.component("ProgressSpinner", ProgressSpinner);
app.component("ConfirmDialog", ConfirmDialog);
app.use(ConfirmationService);

app.use(createPinia());
app.use(router);
app.use(ToastService);

app.mount("#app");
