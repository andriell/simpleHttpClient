package http.helper;

/**
 * Created by arybalko on 05.09.14.
 */
public class C {
    // "\t"
    public static final byte HT = 9;
    public static final int I_HT = 9;
    // "\n"
    public static final byte LF = 10;
    public static final int I_LF = 10;
    // "\r"
    public static final byte CR = 13;
    public static final int I_CR = 13;
    // " "
    public static final byte SP = 32;
    public static final int I_SP = 32;
    // "
    public static final byte QUOTATION = 34;
    public static final int I_QUOTATION = 34;
    // #
    public static final byte NUMBER_SIGN = 35;
    public static final int I_NUMBER_SIGN = 35;
    // '
    public static final byte APOSTROPHE = 39;
    public static final int I_APOSTROPHE = 39;
    // "+"
    public static final byte PLUS = 43;
    public static final int I_PLUS = 43;
    // "-"
    public static final byte MINUS = 45;
    public static final int I_MINUS = 45;
    // "."
    public static final byte FULL_STOP = 46;
    public static final int I_FULL_STOP = 46;
    // "/"
    public static final byte SOLIDUS = 47;
    public static final int I_SOLIDUS = 47;
    // "0"
    public static final byte ZERO = 48;
    public static final int I_ZERO = 48;
    // ":"
    public static final byte COLON = 58;
    public static final int I_COLON = 58;
    // ";"
    public static final byte SEMICOLON = 59;
    public static final int I_SEMICOLON = 59;
    // "="
    public static final byte EQUALS = 61;
    public static final int I_EQUALS = 61;
    // "?"
    public static final byte QUESTION = 63;
    public static final int I_QUESTION = 63;
    // "\"
    public static final byte REVERSE_SOLIDUS = 92;
    public static final int I_REVERSE_SOLIDUS = 92;

    // "\n"
    public static final byte[] BS_LF = {10};
    // " "
    public static final byte[] BS_SP = {32};
    // "#"
    public static final byte[] BS_NUMBER_SIGN = {35};
    // "/"
    public static final byte[] BS_SOLIDUS = {47};
    // ":"
    public static final byte[] BS_COLON = {58};
    // "="
    public static final byte[] BS_EQUALS = {61};
    // "?"
    public static final byte[] BS_QUESTION = {63};
    // "\r\n"
    public static final byte[] BS_CRLF = {13, 10};
    // ", "
    public static final byte[] BS_COMMA_SP = {44, 32};
    // ": "
    public static final byte[] BS_COLON_SP = {58, 32};
    // "; "
    public static final byte[] BS_SEMICOLON_SP = {59, 32};
    // "://"
    public static final byte[] BS_COLON_SS = {58, 47, 47};

    // "Path"
    public static final byte[] BS_PATH = {80, 97, 116, 104};
    // "gzip"
    public static final byte[] BS_GZIP = {103, 122, 105, 112};
    // "Domain"
    public static final byte[] BS_DOMAIN = {68, 111, 109, 97, 105, 110};
    // "Secure"
    public static final byte[] BS_SECURE = {83, 101, 99, 117, 114, 101};
    // "Max-Age"
    public static final byte[] BS_MAX_AGE = {77, 97, 120, 45, 65, 103, 101};
    // "Expires"
    public static final byte[] BS_EXPIRES = {69, 120, 112, 105, 114, 101, 115};
    // "chunked"
    public static final byte[] BS_CHUNKED = {99, 104, 117, 110, 107, 101, 100};
    // "HTTP/1.0"
    public static final byte[] HTTP1P0 = {72, 84, 84, 80, 47, 49, 46, 48};
    // "HTTP/1.1"
    public static final byte[] HTTP1P1 = {72, 84, 84, 80, 47, 49, 46, 49};
    // "HttpOnly"
    public static final byte[] BS_HTTP_ONLY = {72, 116, 116, 112, 79, 110, 108, 121};
}
