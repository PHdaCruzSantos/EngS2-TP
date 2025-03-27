import api from "../config/api.config";

interface Notification {
  id: string;
  title: string;
  message: string;
  read: boolean;
  createdAt: Date;
}

class NotificationService {
  async getAllNotifications(
    userId: string,
    params?: { page?: number; size?: number }
  ) {
    return api.get(`notifications/all/${userId}`, { params });
  }

  async getUnreadNotifications(userId: string) {
    return api.get(`api/notifications/unread/${userId}`);
  }

  async markAsRead(notificationId: string) {
    return api.put(`api/notifications/${notificationId}/read`);
  }

  async markAllAsRead(userId: string) {
    return api.put(`api/notifications/read-all/${userId}`);
  }

  async deleteNotification(notificationId: string) {
    return api.delete(`api/notifications/${notificationId}`);
  }

  async clearAllNotifications(userId: string) {
    return api.delete(`api/notifications/all/${userId}`);
  }
}

export default new NotificationService();
