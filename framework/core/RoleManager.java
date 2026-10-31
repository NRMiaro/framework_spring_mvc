package framework.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RoleManager {

    private static final String USER_ROLES = "framework.auth.roles";

    public static void setRoles(Session session, String... roles) {
        session.addAttribute(USER_ROLES, new HashSet<>(Arrays.asList(roles)));
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getRoles(Session session) {
        Set<String> roles = (Set<String>) session.getAttribute(USER_ROLES);

        return roles != null ? roles : new HashSet<>();
    }

    public static boolean hasRole(Session session, String role) {
        return getRoles(session).contains(role);
    }

    public static boolean hasAnyRole(Session session, String... requiredRoles) {
        Set<String> userRoles = getRoles(session);

        for (String role : requiredRoles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }

        return false;
    }

    public static void removeRoles(Session session) {
        session.removeAttribute(USER_ROLES);
    }
}