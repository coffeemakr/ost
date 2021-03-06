package ch.unstable.ost.api.transport.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import ch.unstable.ost.BuildConfig;
import ch.unstable.ost.api.model.DepartureCheckpoint;
import ch.unstable.ost.api.model.Location;

public enum DepartureCheckpointDeserializer implements JsonDeserializer<DepartureCheckpoint> {
    INSTANCE;
    private static final String TAG = "DepCPDeserializer";
    private static final String FIELD_PLATFORM = "platform";
    private static final String FIELD_DEPARTURE_TIMESTAMP = "departureTimestamp";
    private static final String FIELD_STATION = "station";

    @Nullable
    public static String getPlatform(JsonObject object) {
        if (!object.has(FIELD_PLATFORM)) {
            return null;
        }
        JsonElement platform = object.get(FIELD_PLATFORM);
        if (!platform.isJsonNull()) {
            return platform.getAsString().trim();
        }
        return null;
    }

    @NonNull
    public static Date getDate(JsonObject obj, String fieldName) {
        return new Date(obj.get(fieldName).getAsLong() * 1000);
    }

    @Override
    public DepartureCheckpoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (BuildConfig.DEBUG) Log.d(TAG, "Getting departure from: " + json);
        JsonObject passObj = json.getAsJsonObject();
        Location location = LocationDeserializer.INSTANCE.deserialize(passObj.get(FIELD_STATION), null, context);

        Date departure = getDate(passObj, FIELD_DEPARTURE_TIMESTAMP);
        String platform = getPlatform(passObj);
        if (BuildConfig.DEBUG) Log.v(TAG, "departure platform: " + platform);
        return new DepartureCheckpoint(departure, platform, location);
    }
}
