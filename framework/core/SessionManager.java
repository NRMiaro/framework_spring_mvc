package framework.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionManager {

    private static final String SESSION_KEY = "session";

    public static Session getSession(HttpServletRequest request) {

        HttpSession httpSession = request.getSession();

        Session session = (Session) httpSession.getAttribute(SESSION_KEY);

        if (session == null) {
            session = new Session();
            httpSession.setAttribute(SESSION_KEY, session);
        }

        return session;
    }
}