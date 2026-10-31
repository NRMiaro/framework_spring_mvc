package framework.core;

public class AuthManager {

    private static final String AUTH_USER = "framework.auth.user";

    public static void login(Session session, Object user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        session.addAttribute(AUTH_USER, user);
    }

    public static void logout(Session session) {
        session.removeAttribute(AUTH_USER);
    }

    public static boolean isAuthenticated(Session session) {
        return session.hasAttribute(AUTH_USER);
    }

    public static Object getUser(Session session) {
        return session.getAttribute(AUTH_USER);
    }
}