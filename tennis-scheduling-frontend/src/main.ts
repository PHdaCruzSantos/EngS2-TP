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

import "./assets/styles/main.css";
import "primevue/resources/themes/lara-light-green/theme.css";
import "primeicons/primeicons.css";
import ToastService from "primevue/toastservice";

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

app.use(createPinia());
app.use(router);
app.use(ToastService);

app.mount("#app");
