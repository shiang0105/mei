
package org.meimen.meimen.database;

import java.util.List;

import org.meimen.meimen.database.models.Record;

import android.util.LongSparseArray;
import android.util.SparseArray;

abstract public class AbsDatabaseSource {
    abstract public List<Record> getExerciseRecords(String userId);

    /**
     * @param userId : user id
     * @param timeStamp : time stamp in millisecond
     * @param duration : exercise duration
     * @return
     */
    abstract public boolean insertExerciseRecords(String userId, long timeStamp, int duration);

    public LongSparseArray<Integer> getDurations(String userId) {
        return null;
    }

    public List<Record> getAllExerciseRecords(String userId) {
        return null;
    }

    public void removeExerciseRecords(String userId){

    }
}
