package com.example.bookingvehiclebackend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtils {
    private static final Logger log = LoggerFactory.getLogger(MessageUtils.class);
    private final MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    public Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
    public String getMessage(String key) {
        try {
            return this.messageSource.getMessage(key, null, this.getLocale());
        } catch (Exception var3) {
            return key;
        }
    }
    public String getMessage(String key, Object... params) {
        try {
            return this.messageSource.getMessage(key, params, this.getLocale());
        } catch (Exception var4) {
            return key;
        }
    }
}
