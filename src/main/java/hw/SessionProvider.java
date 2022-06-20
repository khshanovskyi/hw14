package hw;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionProvider {
    private static final String SESSION_NAME = "_sessionId";
    private static ConcurrentHashMap<String, Session> sessionIdSession = new ConcurrentHashMap<>();
    private static LocalDateTime lastCleanTime = LocalDateTime.now();

    private SessionProvider() {
    }

    public static Session getOrCreateSession(Cookie[] cookies, String value, HttpServletResponse response) {
        return getOrCreateSession(cookies, value, 0, response);
    }

    public static Session getOrCreateSession(Cookie[] cookies, String value, long sessionDuration, HttpServletResponse response) {
        cleanUp();

        Cookie cookie = extractCookie(cookies);
        Session session = getSession(cookie);

        if ((Objects.isNull(cookie) || Objects.isNull(session)) && Objects.nonNull(value)) {
            return createSession(value, sessionDuration, response);
        } else if (isActive(session)) {
            return updateSession(session, value);
        } else {
            cleanCookie(cookie);
            return null;
        }
    }

    private static Session getSession(Cookie cookie) {
        return Objects.isNull(cookie) ?
                null :
                sessionIdSession.get(cookie.getValue());
    }

    private static Session createSession(String value, long sessionDuration, HttpServletResponse response) {
        UUID uuid = UUID.randomUUID();
        Session session = createSessionObject(value, sessionDuration);

        sessionIdSession.put(uuid.toString(), session);
        Cookie resultCookie = new Cookie(SESSION_NAME, uuid.toString());
        resultCookie.setHttpOnly(false);
        response.addCookie(resultCookie);

        return session;
    }

    private static Session createSessionObject(String value, long sessionDuration) {
        return sessionDuration <= 60 ? new Session(value) : new Session(sessionDuration, value);
    }

    private static Cookie extractCookie(Cookie[] cookies) {
        return Objects.isNull(cookies) ?
                null :
                Arrays.stream(cookies)
                        .filter(Objects::nonNull)
                        .filter(cookie -> cookie.getName().equals(SESSION_NAME))
                        .findAny()
                        .orElse(null);
    }

    private static void cleanUp() {
        if (Duration.between(lastCleanTime, LocalDateTime.now()).getSeconds() > 30) {
            for (String sessionId : sessionIdSession.keySet()) {
                if (!isActive(sessionIdSession.get(sessionId))) {
                    sessionIdSession.remove(sessionId);
                }
            }
            lastCleanTime = LocalDateTime.now();
        }
    }

    private static void cleanCookie(Cookie cookie) {
        if (Objects.nonNull(cookie)) {
            cookie.setMaxAge(0);
        }
    }

    private static boolean isActive(Session session) {
        return session != null ?
                Duration.between(session.getLastUpdate(), LocalDateTime.now()).getSeconds() < session.getDuration() :
                Boolean.FALSE;
    }

    private static Session updateSession(Session session, String value) {
        if (Objects.nonNull(value)) {
            session.setValue(value);
        }
        session.setLastUpdate(LocalDateTime.now());

        return session;
    }
}
