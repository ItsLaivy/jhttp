package codes.laivy.jhttp.tests;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class AuthorizationTests {

    private AuthorizationTests() {
    }

    @Test
    @Order(value = 0)
    void basic() {
    }
    @Test
    @Order(value = 0)
    void bearer() {
    }

}
