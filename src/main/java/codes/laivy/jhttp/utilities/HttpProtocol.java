package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;

public enum HttpProtocol {

    HTTP("http://", 80, false),
    HTTPS("https://", 443, true),
    ;

    private final @NotNull String name;
    private final int port;

    private final boolean secure;

    HttpProtocol(@NotNull String name, int port, boolean secure) {
        this.name = name;
        this.port = port;
        this.secure = secure;
    }

    public @NotNull String getName() {
        return name;
    }
    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return secure;
    }

}
