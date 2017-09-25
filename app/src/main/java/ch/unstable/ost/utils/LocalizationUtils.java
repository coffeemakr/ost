package ch.unstable.ost.utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.common.base.Preconditions;

import java.util.Calendar;
import java.util.Date;

import ch.unstable.ost.R;
import ch.unstable.ost.api.model.ConnectionQuery;

import static com.google.common.base.Preconditions.checkNotNull;

public enum LocalizationUtils {
    ;

    @NonNull
    public static String getArrivalOrDepartureText(Context context, ConnectionQuery query) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(query, "query is null");
        return getArrivalOrDepartureText(context, query.getArrivalTime(), query.getDepartureTime());
    }

    public static String getDepartureText(final Context context, Date departureTime) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(departureTime, "departureTime is null");
        return getArrivalOrDepartureText(context, departureTime, R.string.departure_time_same_day, R.string.departure_time_other_day);
    }

    @NonNull
    private static String getArrivalText(Context context, Date arrivalTime) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(arrivalTime, "arrivalTime is null");
        return getArrivalOrDepartureText(context, arrivalTime, R.string.arrival_time_same_day, R.string.arrival_time_other_day);
    }

    @NonNull
    public static String getArrivalOrDepartureText(final Context context, @Nullable Date arrivalTime, @Nullable Date departureTime) {
        //noinspection ResultOfMethodCallIgnored
        checkNotNull(context, "context is null");
        Date time;
        if ((time = departureTime) != null) {
            return getDepartureText(context, time);
        } else if ((time = arrivalTime) != null) {
            return getArrivalText(context, time);
        } else {
            return context.getString(R.string.departure_time_now);
        }
    }

    @NonNull
    private static String getArrivalOrDepartureText(Context context, Date date, @StringRes int sameDayFormat, @StringRes int otherDayFormat) {
        Date today = new Date();
        if (isSameDay(today, date)) {
            return context.getString(sameDayFormat, TimeDateUtils.formatTime(date));
        } else {
            return context.getString(otherDayFormat, TimeDateUtils.formatTime(date), TimeDateUtils.formatDate(context, date));
        }
    }

    private static boolean isSameDay(Date first, Date second) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(first);
        cal2.setTime(second);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
