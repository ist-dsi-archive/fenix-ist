package pt.ist.fenix.util;

import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JsonUtils {

    public static <T extends JsonElement> Collector<T, JsonArray, JsonArray> toJsonArray() {
        return Collector.of(JsonArray::new, (array, element) -> array.add(element), (one, other) -> {
            one.addAll(other);
            return one;
        }, Characteristics.IDENTITY_FINISH);
    }

}
