package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.CacheControl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public final class CacheControlTests {

    private CacheControlTests() {
    }

    private static final @NotNull String[] VALIDS = new String[] {
            "max-age=60,public,private",
            "max-age=60,s-maxage=60,no-cache,must-revalidate,proxy-revalidate,no-store,private,public,must-understand,no-transform,immutable,stale-while-revalidate=60,stale-if-error=60,no-cache,max-stale=60,min-fresh=60,no-transform,only-if-cached", // Check all keys
            "max-age   =   60   ,   public  ,   private"
    };

    @Test
    @Order(value = 0)
    void validate() throws ParseException {
        for (@NotNull String valid : VALIDS) {
            Assertions.assertTrue(CacheControl.validate(valid), "cannot verify '" + valid + "' as a valid alternative service");
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull CacheControl control = CacheControl.parse("max-age=60,s-maxage=61,no-cache,must-revalidate,proxy-revalidate,no-store,private,public,must-understand,no-transform,immutable,stale-while-revalidate=65,stale-if-error=62,max-stale=63,min-fresh=64,no-transform,only-if-cached");

        Assertions.assertEquals(60L, control.get(CacheControl.Key.MAX_AGE).orElseThrow(IllegalStateException::new));
        Assertions.assertEquals(61L, control.get(CacheControl.Key.S_MAXAGE).orElseThrow(IllegalStateException::new));
        Assertions.assertEquals(62L, control.get(CacheControl.Key.STALE_IF_ERROR).orElseThrow(IllegalStateException::new));
        Assertions.assertEquals(63L, control.get(CacheControl.Key.MAX_STALE).orElseThrow(IllegalStateException::new));
        Assertions.assertEquals(64L, control.get(CacheControl.Key.MIN_FRESH).orElseThrow(IllegalStateException::new));
        Assertions.assertEquals(65L, control.get(CacheControl.Key.STALE_WHILE_REVALIDATE).orElseThrow(IllegalStateException::new));

        Assertions.assertTrue(control.has(CacheControl.Key.NO_CACHE));
        Assertions.assertTrue(control.has(CacheControl.Key.MUST_REVALIDATE));
        Assertions.assertTrue(control.has(CacheControl.Key.PROXY_REVALIDATE));
        Assertions.assertTrue(control.has(CacheControl.Key.NO_STORE));
        Assertions.assertTrue(control.has(CacheControl.Key.PRIVATE));
        Assertions.assertTrue(control.has(CacheControl.Key.PUBLIC));
        Assertions.assertTrue(control.has(CacheControl.Key.MUST_UNDERSTAND));
        Assertions.assertTrue(control.has(CacheControl.Key.NO_TRANSFORM));
        Assertions.assertTrue(control.has(CacheControl.Key.IMMUTABLE));
        Assertions.assertTrue(control.has(CacheControl.Key.ONLY_IF_CACHED));
    }
    @Test
    @Order(value = 2)
    void serialization() throws ParseException, UnknownHostException, URISyntaxException {
        @NotNull CacheControl reference = CacheControl.parse("max-age=60,s-maxage=61,no-cache,must-revalidate,proxy-revalidate,no-store,private,public,must-understand,no-transform,immutable,stale-while-revalidate=65,stale-if-error=62,max-stale=63,min-fresh=64,no-transform,only-if-cached");
        @NotNull CacheControl clone = CacheControl.parse(reference.toString());

        Assertions.assertEquals(reference, clone);
    }

}
