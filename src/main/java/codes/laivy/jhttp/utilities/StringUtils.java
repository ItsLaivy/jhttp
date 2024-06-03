package codes.laivy.jhttp.utilities;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

    public static byte[][] explode(byte[] bytes, int blockSize) {
        int numBlocks = (int) Math.ceil((double) bytes.length / blockSize);
        return IntStream.range(0, numBlocks).mapToObj(i -> Arrays.copyOfRange(bytes, i * blockSize, Math.min((i + 1) * blockSize, bytes.length))).toArray(byte[][]::new);
    }

}
