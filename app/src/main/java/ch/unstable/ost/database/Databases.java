package ch.unstable.ost.database;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.google.common.base.Preconditions;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.unstable.ost.api.offline.StationsDatabase;

public class Databases {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
           database.execSQL("CREATE TABLE IF NOT EXISTS `favorite_connections` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `connection` TEXT NOT NULL, `creation_date` INTEGER NOT NULL)");
           database.execSQL("CREATE  INDEX `index_favorite_connections_creation_date` ON `favorite_connections` (`creation_date`)");
        }
    };
    private static final boolean FORCE_OVERRIDE = true;
    private static StationsDatabase stationsDatabase;
    private static CacheDatabase cacheDatabase;

    public static synchronized StationsDatabase getStationsDatabase(Context context) {
        Preconditions.checkNotNull(context, "context is null");
        if (stationsDatabase == null) {
            String databaseName = "stations.db";
            copyDBFromAssets(context, databaseName);
            stationsDatabase = Room.databaseBuilder(context.getApplicationContext(), StationsDatabase.class, databaseName).build();
        }
        return stationsDatabase;
    }

    public static synchronized CacheDatabase getCacheDatabase(Context context) {
        Preconditions.checkNotNull(context, "context is null");
        if (cacheDatabase == null) {
            String databaseName = "cache.db";
            cacheDatabase = Room.databaseBuilder(context.getApplicationContext(), CacheDatabase.class, databaseName)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return cacheDatabase;
    }

    public static void copyDBFromAssets(Context context, String database) {
        try {
            File databaseFile = context.getApplicationContext().getDatabasePath(database);
            File parentDir = databaseFile.getParentFile();
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new IOException("Couldn't create directory: " + parentDir);
                }
            }
            if (FORCE_OVERRIDE || !databaseFile.exists()) {
                InputStream inputStream = null;
                OutputStream outputStream = null;
                try {
                    inputStream = context.getAssets().open(database);
                    outputStream = new FileOutputStream(databaseFile);
                    IOUtils.copy(inputStream, outputStream);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
