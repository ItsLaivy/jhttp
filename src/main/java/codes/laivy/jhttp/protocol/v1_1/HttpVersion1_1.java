package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.protocol.HttpFactory;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class HttpVersion1_1 extends HttpVersion {

    private final @NotNull HttpFactory factory = new HttpFactory1_1(this);

    public HttpVersion1_1() {
        super(1, 1);
    }

    @Override
    public @NotNull HttpFactory getFactory() {
        return factory;
    }

}
