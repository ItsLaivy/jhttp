package codes.laivy.jhttp.tests.content;

import codes.laivy.jhttp.content.CacheControl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

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
            Assertions.assertTrue(CacheControl.isCacheControl(valid));
            Assertions.assertEquals(CacheControl.parse(VALIDS[0]), CacheControl.parse(CacheControl.parse(VALIDS[0]).toString()));
        }
    }
    @Test
    @Order(value = 1)
    void assertions() throws ParseException {
        @NotNull CacheControl control = CacheControl.parse("max-age=60,s-maxage=60,no-cache,must-revalidate,proxy-revalidate,no-store,private,public,must-understand,no-transform,immutable,stale-while-revalidate=60,stale-if-error=60,max-stale=60,min-fresh=60,no-transform,only-if-cached");

        Assertions.assertEquals(control.get(CacheControl.Key.MAX_AGE).orElseThrow(IllegalStateException::new), 60L);
        Assertions.assertEquals(control.get(CacheControl.Key.S_MAXAGE).orElseThrow(IllegalStateException::new), 60L);
        Assertions.assertNull(control.get(CacheControl.Key.NO_CACHE).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.MUST_REVALIDATE).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.PROXY_REVALIDATE).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.NO_STORE).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.PRIVATE).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.PUBLIC).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.MUST_UNDERSTAND).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.NO_TRANSFORM).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.IMMUTABLE).orElseThrow(IllegalStateException::new));
        Assertions.assertNull(control.get(CacheControl.Key.STALE_WHILE_REVALIDATE).orElseThrow(IllegalStateException::new));
        Assertions.assertEquals(control.get(CacheControl.Key.STALE_IF_ERROR).orElseThrow(IllegalStateException::new), 60L);
        Assertions.assertEquals(control.get(CacheControl.Key.MAX_STALE).orElseThrow(IllegalStateException::new), 60L);
        Assertions.assertEquals(control.get(CacheControl.Key.MIN_FRESH).orElseThrow(IllegalStateException::new), 60L);
        Assertions.assertNull(control.get(CacheControl.Key.ONLY_IF_CACHED).orElseThrow(IllegalStateException::new));
    }

}
