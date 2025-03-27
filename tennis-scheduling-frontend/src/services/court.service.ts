// Mock data para quadras de tênis
const mockCourts = [
  {
    id: "1",
    name: "Quadra Central",
    location: "Complexo Principal",
    surfaceType: "Saibro",
    isIndoor: false,
    status: "ACTIVE",
  },
  {
    id: "2",
    name: "Quadra Coberta",
    location: "Ginásio Indoor",
    surfaceType: "Sintético",
    isIndoor: true,
    status: "ACTIVE",
  },
  {
    id: "3",
    name: "Quadra Grama",
    location: "Área Externa",
    surfaceType: "Grama Natural",
    isIndoor: false,
    status: "ACTIVE",
  },
  {
    id: "4",
    name: "Quadra Rápida",
    location: "Complexo Principal",
    surfaceType: "Hard Court",
    isIndoor: false,
    status: "ACTIVE",
  },
  {
    id: "5",
    name: "Quadra de Treino",
    location: "Área de Treinamento",
    surfaceType: "Saibro",
    isIndoor: false,
    status: "ACTIVE",
  },
];

interface CourtData {
  id: string;
  name: string;
  location: string;
  surfaceType: string;
  isIndoor: boolean;
  status: string;
}

class CourtService {
  async getAllCourts(): Promise<{ data: CourtData[] }> {
    // Simula resposta assíncrona do backend
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ data: mockCourts });
      }, 300);
    });
  }

  async getCourtById(id: string): Promise<{ data: CourtData }> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const court = mockCourts.find((court) => court.id === id);
        if (court) {
          resolve({ data: court });
        } else {
          reject({ message: "Quadra não encontrada" });
        }
      }, 300);
    });
  }

  async getAvailableCourts(
    date: string,
    startTime: string,
    endTime: string
  ): Promise<{ data: CourtData[] }> {
    // Simula verificação de disponibilidade
    // Na prática, retorna todas as quadras, simulando que todas estão disponíveis
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({ data: mockCourts });
      }, 300);
    });
  }
}

export default new CourtService();
