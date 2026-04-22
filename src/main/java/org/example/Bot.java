package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(Bot.class);

    private final String     botUsername;
    private final BotService service;

    public Bot(String token, String username,
               OrderService orderService, StatsService statsService) {
        super(token);
        this.botUsername = username;
        this.service     = new BotService(orderService, statsService);
        this.service.setPhotoSender(this::safeSendPhoto);
        log.info("🤖 Bot: @{}", username);
    }

    @Override public String getBotUsername() { return botUsername; }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message m  = update.getMessage();
            User    u  = m.getFrom();
            long   cid = m.getChatId();
            String txt = m.getText();
            String name = s(u.getFirstName(), "друг");
            String uname= s(u.getUserName(), "");
            log.info("📨 chat={} user='{}' text='{}'", cid, name, txt);
            service.onText(cid, name, uname, txt, this::safeSend);
        }
        else if (update.hasCallbackQuery()) {
            CallbackQuery cb = update.getCallbackQuery();
            User u  = cb.getFrom();
            long cid = cb.getMessage().getChatId();
            String data  = cb.getData();
            String name  = s(u.getFirstName(), "друг");
            String uname = s(u.getUserName(), "");
            log.info("🖱 chat={} user='{}' data='{}'", cid, name, data);
            try { execute(new AnswerCallbackQuery(cb.getId())); } catch (Exception ignored) {}
            service.onCallback(cid, name, uname, data, this::safeSend);
        }
    }

    private void safeSend(SendMessage m) {
        try { execute(m); } catch (TelegramApiException e) {
            log.error("❌ send chat={}: {}", m.getChatId(), e.getMessage()); }
    }

    private void safeSendPhoto(SendPhoto p) {
        try { execute(p); } catch (TelegramApiException e) {
            log.error("❌ photo chat={}: {}", p.getChatId(), e.getMessage()); }
    }

    private String s(String v, String def) { return v != null && !v.isBlank() ? v : def; }
}
