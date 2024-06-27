package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpHeaderFactory;
import org.jetbrains.annotations.NotNull;

public class HttpHeaderFactory1_1 implements HttpHeaderFactory {

    // Object

    private final @NotNull HttpVersion version;

    HttpHeaderFactory1_1(@NotNull HttpVersion1_1 version) {
        this.version = version;
    }

    // Getters

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

}
