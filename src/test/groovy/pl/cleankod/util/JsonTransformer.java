package pl.cleankod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonTransformer {
    private static final Gson gson = new GsonBuilder().create();

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
