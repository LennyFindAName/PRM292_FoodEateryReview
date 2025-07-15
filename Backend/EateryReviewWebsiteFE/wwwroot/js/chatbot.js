let isChatLoaded = false;

// Hiển thị hoặc ẩn khung chat
function toggleChat() {
    const chat = document.getElementById("chat-container");
    const isHidden = (chat.style.display === "none" || chat.style.display === "");

    chat.style.display = isHidden ? "flex" : "none";

    // Khi mở popup và chưa từng load thì load lịch sử
    if (isHidden && !isChatLoaded) {
        loadChatHistory();
        isChatLoaded = true;
    }
}

// Gửi tin nhắn
async function sendMessage() {
    const token = localStorage.getItem('accessToken');
    const messageInput = document.getElementById("message");
    const message = messageInput.value.trim();
    messageInput.value = "";

    if (!message) {
        alert("Vui lòng nhập tin nhắn.");
        return;
    }

    renderMessage("Bạn", message, "chat-user"); // Hiển thị trước

    try {
        const response = await fetch("http://localhost:5029/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ message })
        });

        const result = await response.json();
        const botMessage = formatMessage(result.response);
        renderMessage("Bot", botMessage, "chat-bot");

    } catch (error) {
        console.error("Lỗi khi gửi tin nhắn:", error);
        renderMessage("Bot", "Xin lỗi, có lỗi xảy ra khi gửi tin nhắn.", "chat-bot");
    }
}

// Hiển thị một tin nhắn
function renderMessage(sender, text, className) {
    const chatBox = document.getElementById("chat-box");
    const bubble = document.createElement("div");
    bubble.className = `chat-bubble ${className}`;
    bubble.innerHTML = `<strong>${sender}:</strong> <br>${text}`;
    chatBox.appendChild(bubble);
    chatBox.scrollTop = chatBox.scrollHeight;
}

// Format markdown hoặc xuống dòng
function formatMessage(message) {
    return message
        .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
        .replace(/\n{2,}/g, "<br><br>")
        .replace(/\n/g, "<br>");
}

// Enter để gửi
function handleKey(event) {
    if (event.key === "Enter") {
        event.preventDefault();
        sendMessage();
    }
}

// Load lại lịch sử chat từ API khi mở popup
async function loadChatHistory() {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    try {
        const response = await fetch("http://localhost:5029/api/chat/history", {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (response.ok) {
            const history = await response.json();

            // Sắp xếp theo thời gian tăng dần (cũ → mới)
            history.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

            // Hiển thị đúng thứ tự user → bot
            history.forEach(msg => {
                const userMsg = formatMessage(msg.message);
                const botReply = formatMessage(msg.response);

                renderMessage("Bạn", userMsg, "chat-user");
                renderMessage("Bot", botReply, "chat-bot");
            });
        }
    } catch (error) {
        console.error("Lỗi tải lịch sử chat:", error);
    }
}
