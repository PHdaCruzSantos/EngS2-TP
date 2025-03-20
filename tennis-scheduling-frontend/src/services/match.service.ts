import api from "../config/api.config";

interface MatchData {
  playerOneId: string;
  playerTwoId: string;
  courtId: string;
  startTime: Date;
  endTime: Date;
  status?: string;
}
class MatchService {
  async createMatch(matchData: MatchData): Promise<any> {
    const formattedMatch = {
      ...matchData,
      date: matchData.startTime.toISOString().split("T")[0],
      time: matchData.startTime.toTimeString().split(" ")[0],
      players: [{ id: matchData.playerOneId }, { id: matchData.playerTwoId }],
    };

    return api.post("api/matches", formattedMatch);
  }

  async getAllMatches() {
    return api.get("api/matches");
  }

  async getMatchById(id: string) {
    return api.get(`api/matches/${id}`);
  }

  async getMatchesByPlayer(playerId: string) {
    return api.get(`api/matches/player/${playerId}`);
  }

  async getMatchesByDateRange(start: Date, end: Date) {
    return api.get(`api/matches/date-range?start=${start}&end=${end}`);
  }

  async updateMatchStatus(id: string, status: string) {
    return api.put(`api/matches/${id}/status`, status);
  }

  async deleteMatch(id: string) {
    return api.delete(`api/matches/${id}`);
  }
}

export default new MatchService();
