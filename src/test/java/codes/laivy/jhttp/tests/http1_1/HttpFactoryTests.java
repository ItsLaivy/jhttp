package codes.laivy.jhttp.tests.http1_1;

import codes.laivy.jhttp.protocol.HttpVersion;
import codes.laivy.jhttp.request.HttpRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class HttpFactoryTests {

    private static final @NotNull String[] VALIDS = new String[] {
            "GET /test HTTP/1.1\r\n\r\n"
    };

    private HttpFactoryTests() {
    }

    @Test
    @Order(value = 0)
    void validate() {
        for (@NotNull String valid : VALIDS) {
            @NotNull HttpRequest request = HttpVersion.HTTP1_1().getFactory().getRequest().parse();
        }
    }

}
