package com.glinboy.app.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String EMAIL_USER = "ROLE_EMAIL_USER";

    public static final String SMS_USER = "ROLE_SMS_USER";

    public static final String NOTIFICATION_USER = "ROLE_NOTIFICATION_USER";

    private AuthoritiesConstants() {}
}
