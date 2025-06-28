package org.example.dto;

import org.example.model.MonetaryUnit;

import java.time.LocalDateTime;


public class SendDTO {
    private String sendId;
    private String senderPhone;
    private String receiverPhone;
    private Integer amount;
    private LocalDateTime date;
    private String reason;
    private MonetaryUnit currency;

    public SendDTO(String sendId, String senderPhone, String receiverPhone, Integer amount, LocalDateTime date, String reason, MonetaryUnit currency) {
        this.sendId = sendId;
        this.senderPhone = senderPhone;
        this.receiverPhone = receiverPhone;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
        this.currency = currency;
    }

    // Getters et setters
    public String getSendId() { return sendId; }
    public String getSenderPhone() { return senderPhone; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }
    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public MonetaryUnit getCurrency() { return currency; }
    public void setCurrency(MonetaryUnit currency) { this.currency = currency; }
}
