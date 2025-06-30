package com.example.bookingvehiclebackend.demo;

public class chatbotServiceDemo {

    public String getReply(String message) {
        message = message.trim().toLowerCase();

        switch (message) {
            case "xin chào":
            case "chào":
                return "Chào bạn! Tôi có thể giúp gì cho bạn?";
            case "bạn tên là gì":
                return "Tôi là Chatbot demo do bạn tạo ra!";
            case "bạn có thể làm gì":
                return "Tôi có thể trả lời những câu hỏi đơn giản.";
            default:
                return "Xin lỗi, tôi chưa hiểu câu hỏi của bạn.";
        }
    }
}