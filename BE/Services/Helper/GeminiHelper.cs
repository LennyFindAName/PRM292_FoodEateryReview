using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Json;
using System.Text;
using System.Threading.Tasks;

namespace Services.Helper
{
    public class GeminiHelper
    {
        private readonly HttpClient _httpClient;
        private readonly string _apiKey;
        private readonly string _apiBaseUrl;

        public GeminiHelper(IConfiguration configuration, HttpClient httpClient)
        {
            _httpClient = httpClient;
            _apiKey = configuration["Gemini:ApiKey"];
            _apiBaseUrl = configuration["Gemini:ApiUrl"];
        }

        public async Task<string> GetChatResponseAsync(string userMessage, string model = "gemini-2.0-flash")
        {
            try
            {
                var apiUrl = $"{_apiBaseUrl}/{model}:generateContent?key={_apiKey}";
                var prompt = "Bạn là chatbot và tên của bạn là Foodper. Bạn tư vấn về món ăn và đánh giá quán ăn, hãy trả lời bằng tiếng Việt.\nCâu hỏi: " + userMessage;
                var request = new GeminiRequest
                {
                    Contents = new List<Content>
                {
                    new Content
                    {
                        Parts = new List<Part> { new Part { Text = prompt } }
                    }
                }
                };

                var response = await _httpClient.PostAsJsonAsync(apiUrl, request);
                response.EnsureSuccessStatusCode();
                var geminiResponse = await response.Content.ReadFromJsonAsync<GeminiResponse>();
                var text = geminiResponse?.Candidates?.FirstOrDefault()?.Content?.Parts?.FirstOrDefault()?.Text;
                return string.IsNullOrEmpty(text) ? "No response from Gemini." : text;
            }
            catch (HttpRequestException ex)
            {
                return $"Error calling Gemini API: {ex.Message}";
            }
            catch (Exception ex)
            {
                return $"Unexpected error: {ex.Message}";
            }
        }
    }

    public class GeminiRequest
    {
        public List<Content> Contents { get; set; } = new List<Content>();
    }

    public class Content
    {
        public List<Part> Parts { get; set; } = new List<Part>();
    }

    public class Part
    {
        public string Text { get; set; } = string.Empty;
    }

    public class GeminiResponse
    {
        public List<Candidate> Candidates { get; set; } = new List<Candidate>();
    }

    public class Candidate
    {
        public Content Content { get; set; } = new Content();
    }
}
