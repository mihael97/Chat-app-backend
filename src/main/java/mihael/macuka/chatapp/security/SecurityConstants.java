package mihael.macuka.chatapp.security;

import org.apache.commons.lang3.RandomStringUtils;

public class SecurityConstants {
    public final static String AUTHORIZATION_HEADER = "Authorization";
    public final static String AUTHORIZATION_KEY = RandomStringUtils.randomAlphanumeric(64);
}
