namespace BusinessObjects.ModerationModels.User
{
    public class UserModerationDto
    {
        public int UserId { get; set; }
        public string DisplayName { get; set; }
        public string Username { get; set; }
        public string UserEmail { get; set; }
        public int UserStatus { get; set; }
        public string ModeratorNote { get; set; }
    }
}