// Pet Health Chatbot Widget
class PetHealthChatbot {
    constructor() {
        this.isOpen = false;
        this.currentQuestion = 0;
        this.responses = {};
        this.questions = [
            {
                id: 'greeting',
                text: "Hello! I'm your pet health assistant. I can help you with basic health checks and medication questions. Would you like to start a quick health assessment?",
                type: 'choice',
                options: ['Yes, let\'s start', 'No, thanks']
            },
            {
                id: 'fever',
                text: "Is your pet experiencing a fever? (Check by feeling their ears, nose, or using a pet thermometer)",
                type: 'choice',
                options: ['Yes', 'No', 'Not sure']
            },
            {
                id: 'medication',
                text: "Has your pet been taking any tablets or medication recently?",
                type: 'choice',
                options: ['Yes', 'No', 'I don\'t know']
            },
            {
                id: 'lethargy',
                text: "Is your pet showing signs of lethargy or unusual behavior? (Less active, sleeping more, not eating normally)",
                type: 'choice',
                options: ['Yes', 'No', 'Maybe']
            },
            {
                id: 'appetite',
                text: "How is your pet's appetite?",
                type: 'choice',
                options: ['Normal', 'Eating less', 'Not eating at all', 'Eating more than usual']
            },
            {
                id: 'vomiting',
                text: "Has your pet been vomiting or having diarrhea?",
                type: 'choice',
                options: ['Yes', 'No', 'Occasionally']
            },
            {
                id: 'breathing',
                text: "Is your pet breathing normally? (No coughing, wheezing, or rapid breathing)",
                type: 'choice',
                options: ['Yes', 'No', 'Not sure']
            }
        ];
        
        this.init();
    }

    init() {
        this.createChatbotHTML();
        this.attachEventListeners();
        this.loadChatbot();
    }

    createChatbotHTML() {
        const chatbotHTML = `
            <div id="chatbot-widget" class="chatbot-container">
                <!-- Chatbot Toggle Button -->
                <div id="chatbot-toggle" class="chatbot-toggle">
                    <span style="font-size: 18px;">💬</span>
                    <span class="chatbot-badge">Pet Health</span>
                </div>

                <!-- Chatbot Window -->
                <div id="chatbot-window" class="chatbot-window">
                    <div class="chatbot-header">
                                            <div class="chatbot-title">
                        <span style="font-size: 18px; margin-right: 8px;">🐾</span>
                        <span>Pet Health Assistant</span>
                    </div>
                        <button id="chatbot-close" class="chatbot-close">
                            <span style="font-size: 16px;">✕</span>
                        </button>
                    </div>
                    
                    <div id="chatbot-messages" class="chatbot-messages">
                        <!-- Messages will be added here -->
                    </div>
                    
                    <div id="chatbot-input" class="chatbot-input">
                        <!-- Input options will be added here -->
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', chatbotHTML);
    }

    attachEventListeners() {
        // Toggle chatbot
        document.getElementById('chatbot-toggle').addEventListener('click', () => {
            this.toggleChatbot();
        });

        // Close chatbot
        document.getElementById('chatbot-close').addEventListener('click', () => {
            this.closeChatbot();
        });

        // Close on escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isOpen) {
                this.closeChatbot();
            }
        });
    }

    loadChatbot() {
        // Add CSS if not already loaded
        if (!document.getElementById('chatbot-styles')) {
            this.addChatbotStyles();
        }
    }

    toggleChatbot() {
        if (this.isOpen) {
            this.closeChatbot();
        } else {
            this.openChatbot();
        }
    }

    openChatbot() {
        this.isOpen = true;
        document.getElementById('chatbot-widget').classList.add('open');
        document.getElementById('chatbot-toggle').style.display = 'none';
        
        // Start the conversation
        this.startConversation();
    }

    closeChatbot() {
        this.isOpen = false;
        document.getElementById('chatbot-widget').classList.remove('open');
        document.getElementById('chatbot-toggle').style.display = 'flex';
        
        // Reset conversation
        this.resetConversation();
    }

    startConversation() {
        this.currentQuestion = 0;
        this.responses = {};
        this.showQuestion(this.questions[0]);
    }

    resetConversation() {
        this.currentQuestion = 0;
        this.responses = {};
        document.getElementById('chatbot-messages').innerHTML = '';
        document.getElementById('chatbot-input').innerHTML = '';
    }

    showQuestion(question) {
        const messagesContainer = document.getElementById('chatbot-messages');
        const inputContainer = document.getElementById('chatbot-input');

        // Add bot message
        const botMessage = document.createElement('div');
        botMessage.className = 'chatbot-message bot-message';
        botMessage.innerHTML = `
            <div class="message-content">
                <div class="bot-avatar">🐾</div>
                <div class="message-text">${question.text}</div>
            </div>
        `;
        messagesContainer.appendChild(botMessage);

        // Add user options
        inputContainer.innerHTML = '';
        question.options.forEach(option => {
            const button = document.createElement('button');
            button.className = 'chatbot-option';
            button.textContent = option;
            button.addEventListener('click', () => {
                this.handleResponse(question.id, option);
            });
            inputContainer.appendChild(button);
        });

        // Scroll to bottom
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    handleResponse(questionId, response) {
        // Store response
        this.responses[questionId] = response;

        // Add user message
        const messagesContainer = document.getElementById('chatbot-messages');
        const userMessage = document.createElement('div');
        userMessage.className = 'chatbot-message user-message';
        userMessage.innerHTML = `
            <div class="message-content">
                <div class="message-text">${response}</div>
                <div class="user-avatar">👤</div>
            </div>
        `;
        messagesContainer.appendChild(userMessage);

        // Clear input
        document.getElementById('chatbot-input').innerHTML = '';

        // Process response and show next question or conclusion
        this.processResponse(questionId, response);
    }

    processResponse(questionId, response) {
        const messagesContainer = document.getElementById('chatbot-messages');
        const inputContainer = document.getElementById('chatbot-input');

        // Add typing indicator
        const typingIndicator = document.createElement('div');
        typingIndicator.className = 'chatbot-message bot-message typing';
        typingIndicator.innerHTML = `
            <div class="message-content">
                <div class="bot-avatar">🐾</div>
                <div class="typing-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        `;
        messagesContainer.appendChild(typingIndicator);

        // Simulate typing delay
        setTimeout(() => {
            messagesContainer.removeChild(typingIndicator);

            if (questionId === 'greeting') {
                if (response === 'Yes, let\'s start') {
                    this.currentQuestion = 1;
                    this.showQuestion(this.questions[1]);
                } else {
                    this.showGoodbye();
                }
            } else if (this.currentQuestion < this.questions.length - 1) {
                this.currentQuestion++;
                this.showQuestion(this.questions[this.currentQuestion]);
            } else {
                this.showConclusion();
            }
        }, 1000);
    }

    showConclusion() {
        const messagesContainer = document.getElementById('chatbot-messages');
        const inputContainer = document.getElementById('chatbot-input');

        // Analyze responses
        const analysis = this.analyzeResponses();
        
        const conclusionMessage = document.createElement('div');
        conclusionMessage.className = 'chatbot-message bot-message';
        conclusionMessage.innerHTML = `
            <div class="message-content">
                <div class="bot-avatar">🐾</div>
                <div class="message-text">
                    <strong>Health Assessment Summary:</strong><br><br>
                    ${analysis.message}<br><br>
                    <strong>Recommendation:</strong> ${analysis.recommendation}
                </div>
            </div>
        `;
        messagesContainer.appendChild(conclusionMessage);

        // Add restart option
        inputContainer.innerHTML = `
            <button class="chatbot-option primary" onclick="window.petHealthChatbot.restartConversation()">
                🔄 Start New Assessment
            </button>
            <button class="chatbot-option" onclick="window.petHealthChatbot.closeChatbot()">
                ✕ Close
            </button>
        `;

        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    showGoodbye() {
        const messagesContainer = document.getElementById('chatbot-messages');
        const inputContainer = document.getElementById('chatbot-input');

        const goodbyeMessage = document.createElement('div');
        goodbyeMessage.className = 'chatbot-message bot-message';
        goodbyeMessage.innerHTML = `
            <div class="message-content">
                <div class="bot-avatar">🐾</div>
                <div class="message-text">
                    No problem! I'm here whenever you need help with your pet's health. 
                    Feel free to chat with me anytime. Take care! 🐾
                </div>
            </div>
        `;
        messagesContainer.appendChild(goodbyeMessage);

        inputContainer.innerHTML = `
            <button class="chatbot-option primary" onclick="window.petHealthChatbot.closeChatbot()">
                ✕ Close
            </button>
        `;

        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    restartConversation() {
        document.getElementById('chatbot-messages').innerHTML = '';
        document.getElementById('chatbot-input').innerHTML = '';
        this.startConversation();
    }

    analyzeResponses() {
        const responses = this.responses;
        let concernLevel = 0;
        let symptoms = [];

        // Analyze fever
        if (responses.fever === 'Yes') {
            concernLevel += 2;
            symptoms.push('fever');
        }

        // Analyze medication
        if (responses.medication === 'Yes') {
            concernLevel += 1;
            symptoms.push('on medication');
        }

        // Analyze lethargy
        if (responses.lethargy === 'Yes') {
            concernLevel += 2;
            symptoms.push('lethargy');
        }

        // Analyze appetite
        if (responses.appetite === 'Eating less' || responses.appetite === 'Not eating at all') {
            concernLevel += 2;
            symptoms.push('poor appetite');
        }

        // Analyze vomiting/diarrhea
        if (responses.vomiting === 'Yes') {
            concernLevel += 3;
            symptoms.push('vomiting/diarrhea');
        }

        // Analyze breathing
        if (responses.breathing === 'No') {
            concernLevel += 3;
            symptoms.push('breathing issues');
        }

        let message, recommendation;

        if (concernLevel >= 5) {
            message = "I notice several concerning symptoms that may require immediate attention.";
            recommendation = "Please contact your veterinarian as soon as possible. These symptoms could indicate a serious health issue.";
        } else if (concernLevel >= 3) {
            message = "I see some symptoms that may need attention.";
            recommendation = "Consider scheduling an appointment with your veterinarian within the next 24-48 hours to be safe.";
        } else if (concernLevel >= 1) {
            message = "Your pet shows some mild symptoms that should be monitored.";
            recommendation = "Keep a close eye on your pet and contact your veterinarian if symptoms worsen or persist.";
        } else {
            message = "Based on your responses, your pet appears to be in good health!";
            recommendation = "Continue with regular check-ups and maintain your pet's current care routine.";
        }

        return { message, recommendation };
    }

    addChatbotStyles() {
        const styles = `
            <style id="chatbot-styles">
                .chatbot-container {
                    position: fixed;
                    bottom: 20px;
                    right: 20px;
                    z-index: 1000;
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                }

                .chatbot-toggle {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    padding: 12px 16px;
                    border-radius: 50px;
                    cursor: pointer;
                    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
                    transition: all 0.3s ease;
                    font-weight: 600;
                }

                .chatbot-toggle:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 25px rgba(102, 126, 234, 0.4);
                }

                .chatbot-toggle i {
                    font-size: 20px;
                }

                .chatbot-badge {
                    font-size: 14px;
                }

                .chatbot-window {
                    position: absolute;
                    bottom: 70px;
                    right: 0;
                    width: 350px;
                    height: 500px;
                    background: white;
                    border-radius: 20px;
                    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
                    display: none;
                    flex-direction: column;
                    overflow: hidden;
                    border: 1px solid #e1e5e9;
                }

                .chatbot-container.open .chatbot-window {
                    display: flex;
                }

                .chatbot-header {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: white;
                    padding: 16px 20px;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }

                .chatbot-title {
                    display: flex;
                    align-items: center;
                    gap: 10px;
                    font-weight: 600;
                    font-size: 16px;
                }

                .chatbot-title i {
                    font-size: 18px;
                }

                .chatbot-close {
                    background: none;
                    border: none;
                    color: white;
                    cursor: pointer;
                    font-size: 18px;
                    padding: 5px;
                    border-radius: 50%;
                    transition: background 0.3s ease;
                }

                .chatbot-close:hover {
                    background: rgba(255, 255, 255, 0.2);
                }

                .chatbot-messages {
                    flex: 1;
                    padding: 20px;
                    overflow-y: auto;
                    display: flex;
                    flex-direction: column;
                    gap: 15px;
                }

                .chatbot-message {
                    max-width: 80%;
                }

                .bot-message {
                    align-self: flex-start;
                }

                .user-message {
                    align-self: flex-end;
                }

                .message-content {
                    display: flex;
                    align-items: flex-end;
                    gap: 8px;
                }

                .bot-message .message-content {
                    flex-direction: row;
                }

                .user-message .message-content {
                    flex-direction: row-reverse;
                }

                .message-text {
                    background: #f8f9fa;
                    padding: 12px 16px;
                    border-radius: 18px;
                    font-size: 14px;
                    line-height: 1.4;
                    color: #333;
                }

                .bot-message .message-text {
                    background: #e3f2fd;
                    border-bottom-left-radius: 4px;
                }

                .user-message .message-text {
                    background: #667eea;
                    color: white;
                    border-bottom-right-radius: 4px;
                }

                .bot-avatar, .user-avatar {
                    width: 32px;
                    height: 32px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 16px;
                    background: #f8f9fa;
                    border: 2px solid #e1e5e9;
                }

                .bot-avatar {
                    background: #e3f2fd;
                    border-color: #667eea;
                }

                .user-avatar {
                    background: #f3e5f5;
                    border-color: #764ba2;
                }

                .typing-dots {
                    display: flex;
                    gap: 4px;
                    padding: 12px 16px;
                }

                .typing-dots span {
                    width: 8px;
                    height: 8px;
                    border-radius: 50%;
                    background: #667eea;
                    animation: typing 1.4s infinite ease-in-out;
                }

                .typing-dots span:nth-child(1) { animation-delay: -0.32s; }
                .typing-dots span:nth-child(2) { animation-delay: -0.16s; }

                @keyframes typing {
                    0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
                    40% { transform: scale(1); opacity: 1; }
                }

                .chatbot-input {
                    padding: 20px;
                    border-top: 1px solid #e1e5e9;
                    display: flex;
                    flex-direction: column;
                    gap: 8px;
                }

                .chatbot-option {
                    background: #f8f9fa;
                    border: 1px solid #e1e5e9;
                    padding: 10px 16px;
                    border-radius: 12px;
                    cursor: pointer;
                    font-size: 14px;
                    transition: all 0.3s ease;
                    text-align: left;
                    color: #333;
                }

                .chatbot-option:hover {
                    background: #667eea;
                    color: white;
                    border-color: #667eea;
                }

                .chatbot-option.primary {
                    background: #667eea;
                    color: white;
                    border-color: #667eea;
                }

                .chatbot-option.primary:hover {
                    background: #5a6fd8;
                }

                /* Responsive design */
                @media (max-width: 480px) {
                    .chatbot-window {
                        width: calc(100vw - 40px);
                        right: -10px;
                    }
                    
                    .chatbot-toggle {
                        padding: 10px 14px;
                    }
                    
                    .chatbot-badge {
                        display: none;
                    }
                }
            </style>
        `;
        
        document.head.insertAdjacentHTML('beforeend', styles);
    }
}

// Initialize chatbot when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.petHealthChatbot = new PetHealthChatbot();
});
