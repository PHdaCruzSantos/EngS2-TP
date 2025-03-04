import api from "../config/api.config"; // Importa a instância do axios configurada

export interface LoginResponse {
  token: string;
  user: any;
}

export interface RegisterData {
  name: string;
  email: string;
  password: string;
}

export const authService = {
  async login(email: string, password: string): Promise<LoginResponse> {
    try {
      const response = await api.post("/api/auth/login", { email, password }); // Certifique-se de que a URL está correta
      if (response.data.token) {
        localStorage.setItem("user", JSON.stringify(response.data));
      }
      return response.data;
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : "Unknown error occurred";
      throw new Error("Login failed: " + errorMessage);
    }
  },
  async register(data: RegisterData): Promise<any> {
    try {
      const response = await api.post("/api/users/register", data); // Já está correto
      return response.data;
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : "Unknown error occurred";
      throw new Error("Registration failed: " + errorMessage);
    }
  },

  logout(): void {
    localStorage.removeItem("user"); // Remove o usuário do localStorage
  },

  getCurrentUser(): any {
    const userStr = localStorage.getItem("user");
    if (userStr) return JSON.parse(userStr);
    return null;
  },

  isAuthenticated(): boolean {
    return !!this.getCurrentUser()?.token; // Verifica se o usuário está autenticado
  },
};
