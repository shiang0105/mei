
package org.meimen.meimen.database;

import org.meimen.meimen.database.models.Record;

import android.content.Context;

import com.yahoo.squidb.data.SquidDatabase;
import com.yahoo.squidb.data.adapter.SQLiteDatabaseWrapper;
import com.yahoo.squidb.sql.Index;
import com.yahoo.squidb.sql.Table;

public class ExerciseRecordDatabase extends SquidDatabase {

    private static final String DATABASE_NAME = "ExerciseDatabase";

    // version starts from 0
    private static final int VERSION = 1;

    public ExerciseRecordDatabase(Context context) {
        super(context);
    }

    @Override
    public String getName() {
        return DATABASE_NAME;
    }

    @Override
    protected int getVersion() {
        return VERSION;
    }

    @Override
    protected Table[] getTables() {
        return new Table[] {
                Record.TABLE
        };
    }

    @Override
    protected Index[] getIndexes() {
        return new Index[] {
                Record.TABLE.index("record_idx", Record.USER_ID, Record.TIMESTAMP)
        };
    }

    @Override
    protected boolean onUpgrade(SQLiteDatabaseWrapper db, int oldVersion, int newVersion) {
        // Example DB migration if the tags table and tasks.priority columns were added in version 2
        switch (oldVersion) {
            case 1:
                // DON'T ADD BREAK
            case 2:
        }
        return false;
    }
}
