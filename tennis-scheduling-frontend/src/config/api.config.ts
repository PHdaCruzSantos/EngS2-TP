import axios from "axios";

const api = axios.create({
  baseURL: "/api", // Usa o proxy configurado no Vite
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Permite o envio de cookies (se necessário)
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401: // Não autorizado
          localStorage.removeItem("user"); // Remove o usuário do localStorage
          localStorage.removeItem("token"); // Remove o usuário do localStorage
          window.location.href = "/login"; // Redireciona para a página de login
          break;
        case 403: // Proibido
          console.error("Acesso negado");
          console.log(localStorage.getItem("user"));
          break;
        case 500:
          console.error("Erro no servidor");
          break;
      }
    } else {
      console.error("Erro de rede ou servidor indisponível");
    }
    return Promise.reject(error);
  }
);

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // Assuming you store the token in localStorage
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
