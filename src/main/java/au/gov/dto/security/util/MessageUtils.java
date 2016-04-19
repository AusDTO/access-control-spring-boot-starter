package au.gov.dto.security.util;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("securityMessageUtils")
public class MessageUtils {

    private static AtomicReference<MessageUtils> INSTANCE = new AtomicReference<MessageUtils>();

    @Autowired
    private MessageSource messageSource;

    public MessageUtils() {
        INSTANCE.getAndSet(this);
    }

    public static MessageUtils getInstance() {

        return INSTANCE.get();
    }

    public static String getMessage(String key) {

        return getInstance().messageSource.getMessage(key, null, Locale.getDefault());
    }

    public static String getMessage(String key, Object[] args) {

        return getInstance().messageSource.getMessage(key, args, Locale.getDefault());
    }
}
