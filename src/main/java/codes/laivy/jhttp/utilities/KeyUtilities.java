package codes.laivy.jhttp.utilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class KeyUtilities {
    private KeyUtilities() {
        throw new UnsupportedOperationException("this class cannot be instantiated");
    }

    public static @NotNull Map<String, String> read(@NotNull String string, @Nullable Character delimiter, char split) {
        @NotNull Pattern pattern = Pattern.compile("\\s*" + split + "\\s*(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        @NotNull String[] parts = string.split(pattern.pattern());
        @NotNull Map<String, String> map = new LinkedHashMap<>();

        for (@NotNull String part : parts) {
            if (delimiter != null) {
                @NotNull String[] option = part.split("\\s*" + delimiter + "\\s*");
                @NotNull String key = option[0];

                if (option.length > 1) {
                    @NotNull String value = option[1].startsWith("\"") && option[1].endsWith("\"") ? option[1].substring(1, option[1].length() - 1) : option[1];

                    if (!value.isEmpty()) {
                        map.put(key, value);
                    }
                } else {
                    map.put(key, "");
                }
            } else {
                map.put(part, "");
            }
        }

        return map;
    }

}
