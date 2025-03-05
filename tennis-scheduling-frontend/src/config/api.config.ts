import axios from "axios";

const api = axios.create({
  baseURL: "/api", // Usa o proxy configurado no Vite
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // Permite o envio de cookies (se necessário)
});

// Intercepta as requisições para adicionar o token de autenticação
api.interceptors.request.use((config) => {
  const user = localStorage.getItem("user");
  if (user) {
    const { token } = JSON.parse(user);
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// Intercepta as respostas para tratar erros globais
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expirado ou inválido
      localStorage.removeItem("user"); // Remove o usuário do localStorage
      window.location.href = "/login"; // Redireciona para a página de login
    }
    return Promise.reject(error);
  }
);

export default api;
