package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "send")
public class Send {
    @Id
    @Column(name = "send_id", length = 36, unique = true, nullable = false)
    private String sendId;

    @ManyToOne
    @JoinColumn(name = "sender_phone", nullable = false)
    private Client sender;

    @ManyToOne
    @JoinColumn(name = "receiver_phone", nullable = false)
    private Client receiver;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 100)
    private String reason;

    @ManyToOne
    @JoinColumn(nullable = false)
    private MonetaryUnit currency;

    // --- Constructors ---

    public Send() { this.sendId = UUID.randomUUID().toString(); }

    public Send(Client sender, Client receiver, Integer amount, LocalDateTime date, String reason, MonetaryUnit currency) {
        this.sendId = UUID.randomUUID().toString();
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.date = date;
        this.reason = reason;
        this.currency = currency;
    }

    // --- Getters and Setters ---

    public String getSendId() { return sendId; }

    public void setSendId(String sendId) { this.sendId = sendId; }

    public Client getSender() { return sender; }

    public void setSender(Client sender) { this.sender = sender; }

    public Client getReceiver() { return receiver; }

    public void setReceiver(Client receiver) { this.receiver = receiver; }

    public Integer getAmount() { return amount; }

    public void setAmount(Integer amount) { this.amount = amount; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public MonetaryUnit getCurrency() { return currency; }

    public void setCurrency(MonetaryUnit currency) { this.currency = currency; }
}
