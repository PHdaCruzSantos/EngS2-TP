import api from "../config/api.config";

interface UserData {
  id: string;
  name: string;
  email: string;
  password?: string;
}

class UserService {
  async getAllUsers() {
    return api.get("api/users", {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`, // Envia o token de autenticação
      },
    });
  }

  async getUserById(id: string) {
    return api.get(`api/users/${id}`);
  }

  async updateUser(id: string, userData: Partial<UserData>) {
    return api.put(`api/users/${id}`, userData);
  }

  async deleteUser(id: string) {
    return api.delete(`api/users/${id}`);
  }

  // Métodos para gerenciar o usuário localmente
  getCurrentUser() {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  }

  isLoggedIn() {
    return !!this.getCurrentUser();
  }

  logout() {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
  }
}

export default new UserService();
