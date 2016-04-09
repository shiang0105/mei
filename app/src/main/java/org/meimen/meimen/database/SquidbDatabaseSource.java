
package org.meimen.meimen.database;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.meimen.meimen.database.models.Record;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;

import com.yahoo.squidb.data.SquidCursor;
import com.yahoo.squidb.sql.Delete;
import com.yahoo.squidb.sql.Function;
import com.yahoo.squidb.sql.Property;
import com.yahoo.squidb.sql.Query;

public class SquidbDatabaseSource extends AbsDatabaseSource {

    public ExerciseRecordDatabase mDatabase;

    public SquidbDatabaseSource(Context context) {
        mDatabase = new ExerciseRecordDatabase(context);
    }

    private static final Query GET_EXERCISE_RECORDS = Query.select(Record.USER_ID, Record.DURATION, Record.TIMESTAMP)
            .from(Record.TABLE);

    public List<Record> getExerciseRecords(String userId) {
        List<Record> records = new ArrayList<>();
        // GET_EXERCISE_RECORDS.where(Record.USER_ID.eq(userId));

        SquidCursor<Record> voters = mDatabase.query(Record.class, GET_EXERCISE_RECORDS.where(Record.USER_ID.eq(userId)));

        try {
            while (voters.moveToNext()) {
                Record person = new Record();
                person.readPropertiesFromCursor(voters);
                records.add(person);
            }
        } finally {
            voters.close();
        }
        return records;
    }

    public LongSparseArray<Integer> getDurations(String userId) {
        LongSparseArray<Integer> durations = new LongSparseArray<>();
        if (!TextUtils.isEmpty(userId)) {
            Property.IntegerProperty durationSum = Property.IntegerProperty.fromFunction(
                    Function.sum(Record.DURATION), "durationSum");

            TimeZone tz = TimeZone.getDefault();
            int timeZoneOffset = tz.getRawOffset();

            Property.IntegerProperty dayTimeStamp = Property.IntegerProperty.fromFunction(
                    Function.divide(Function.add(Record.TIMESTAMP, timeZoneOffset), 86400 * 1000), "dayTimeStamp");

            Query query = Query.select(Record.USER_ID, Record.DURATION, Record.TIMESTAMP, durationSum, dayTimeStamp).groupBy(
                    Record.USER_ID, dayTimeStamp);

            SquidCursor<Record> records = mDatabase.query(Record.class, query);

            try {
                while (records.moveToNext()) {
                    durations.append(records.get(dayTimeStamp).longValue() * 86400l * 1000l - timeZoneOffset,
                            records.get(durationSum).intValue());
                    Record person = new Record();
                    person.readPropertiesFromCursor(records);
                }
            } finally {
                records.close();
            }

        }
        return durations;
    }

    public List<Record> getAllExerciseRecords(String userId) {
        List<Record> records = new ArrayList<>();
        Query query = Query.select(Record.USER_ID, Record.DURATION, Record.TIMESTAMP)
                .from(Record.TABLE);

        SquidCursor<Record> voters = mDatabase.query(Record.class, query);

        try {
            while (voters.moveToNext()) {
                Record person = new Record();
                person.readPropertiesFromCursor(voters);
                records.add(person);
                Log.e("John all records", person.getUserId() + ", " + person.getDuration() + ", " + person.getTimestamp() + "");
            }
        } finally {
            voters.close();
        }
        return records;
    }

    public boolean insertExerciseRecords(String userId, long timeStamp, int duration) {
        boolean insertSuccess = false;
        mDatabase.beginTransaction();
        try {
            Record record = new Record()
                    .setUserId(userId)
                    .setTimestamp(timeStamp)
                    .setDuration(duration);

            // mDatabase.update(Record.USER_ID.eq(userId), record);
            // mDatabase.setTransactionSuccessful();
            // TODO : change to update

            if (mDatabase.persist(record)) {
                mDatabase.setTransactionSuccessful();
                insertSuccess = true;
            }
        } finally {
            mDatabase.endTransaction();
        }

        return insertSuccess;
    }

    public void removeExerciseRecords(String userId) {
        mDatabase.delete(Delete.from(Record.TABLE).where(Record.USER_ID.eq(userId)));
    }
}
