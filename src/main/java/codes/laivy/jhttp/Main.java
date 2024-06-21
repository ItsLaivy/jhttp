package codes.laivy.jhttp;

import codes.laivy.jhttp.headers.HeaderKey;
import org.jetbrains.annotations.NotNull;

public final class Main {

    public static final @NotNull String CRLF = "\r\n";

    public static void main(String[] args) throws ClassNotFoundException {
        // Load headers
        Class.forName(HeaderKey.class.getName());
    }

}