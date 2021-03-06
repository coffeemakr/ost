package ch.unstable.ost;

import android.app.Application;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import ch.unstable.ost.error.ErrorReportActivity;

public class OSTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CaocConfig.Builder.create()
                .errorActivity(ErrorReportActivity.class)
                .apply();
    }
}
