using BusinessObjects.Enums;
using BusinessObjects.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Services.Services.NotificationService
{
    public static class NotificationService
    {
        public static async Task SendAsync(EateryReviewDbContext context, int userId, string message, MessageType messageType)
        {
            if (userId <= 0 || string.IsNullOrWhiteSpace(message))
                return;

            string title = messageType switch
            {
                MessageType.Success => "Thành công",
                MessageType.Failure => "Thất bại",
                MessageType.Info => "Thông báo",
                _ => "Thông báo không xác định"
            };

            var notification = new Notification
            {
                UserId = userId,
                Title = title,
                Message = message,
                IsRead = false,
                Timestamp = DateTime.Now
            };

            context.Notifications.Add(notification);
            await context.SaveChangesAsync();
        }
    }
}
