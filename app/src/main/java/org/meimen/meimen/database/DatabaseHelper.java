
package org.meimen.meimen.database;

import android.content.Context;

public class DatabaseHelper {

    private AbsDatabaseSource mDataSource;

    private static DatabaseHelper sInstance;

    public static DatabaseHelper getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (DatabaseHelper.class) {
                if (sInstance == null) {
                    sInstance = new DatabaseHelper();
                    sInstance.mDataSource = new SquidbDatabaseSource(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public AbsDatabaseSource getDataSource() {
        return mDataSource;
    }
}
