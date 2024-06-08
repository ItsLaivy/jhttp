package codes.laivy.jhttp.protocol.v1_1;

import codes.laivy.jhttp.protocol.HttpFactory;
import codes.laivy.jhttp.protocol.HttpVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class HttpVersion1_1 extends HttpVersion {

    private final @NotNull HttpFactory factory = new HttpFactory1_1(this);

    public HttpVersion1_1() {
        super(
                new byte[] {
                        0x68, 0x74, 0x74, 0x70, 0x2F, 0x31, 0x2E, 0x31
                },
                1,
                1
        );
    }

    @Override
    public @NotNull HttpFactory getFactory() {
        return factory;
    }

}
