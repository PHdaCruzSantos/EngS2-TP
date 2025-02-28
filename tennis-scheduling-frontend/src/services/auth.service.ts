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
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        mode: "no-cors",
        credentials: "include",
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error("Login failed");
      }

      const data = await response.json();
      if (data.token) {
        localStorage.setItem("user", JSON.stringify(data));
      }
      return data;
    } catch (error) {
      throw error;
    }
  },

  async register(data: RegisterData): Promise<any> {
    try {
      const response = await fetch("http://localhost:8080/api/users/register", {
        // Changed endpoint
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },

        credentials: "include",
        mode: "no-cors", // Changed from no-cors to cors
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Registration failed");
      }

      return await response.json();
    } catch (error) {
      throw error;
    }
  },

  logout(): void {
    localStorage.removeItem("user");
  },

  getCurrentUser(): any {
    const userStr = localStorage.getItem("user");
    if (userStr) return JSON.parse(userStr);
    return null;
  },

  isAuthenticated(): boolean {
    return !!this.getCurrentUser()?.token;
  },
};
