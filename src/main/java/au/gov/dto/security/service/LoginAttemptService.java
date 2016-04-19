package au.gov.dto.security.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {

    @Value("${access.control.max-login-attempt:5}")
    private int maxLoginAttempt;

    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService() {
        super();
        attemptsCache = CacheBuilder.newBuilder()
                                    .expireAfterWrite(1, TimeUnit.DAYS)
                                    .build(new CacheLoader<String, Integer>() {

                                        @Override
                                        public Integer load(String key) {

                                            return 0;
                                        }
                                    });
    }

    public void unlock(String key) {

        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {

        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {

        try {
            return attemptsCache.get(key) >= maxLoginAttempt;
        } catch (ExecutionException e) {
            return false;
        }
    }
}
