package codes.laivy.jhttp.protocol.v1_0;

import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.protocol.factory.HttpHeaderFactory;
import codes.laivy.jhttp.protocol.v1_0.HttpVersion1_0;
import org.jetbrains.annotations.NotNull;

public class HttpHeaderFactory1_0 implements HttpHeaderFactory {

    // Object

    private final @NotNull HttpVersion version;

    HttpHeaderFactory1_0(@NotNull HttpVersion1_0 version) {
        this.version = version;
    }

    // Getters

    @Override
    public @NotNull HttpVersion getVersion() {
        return version;
    }

}
