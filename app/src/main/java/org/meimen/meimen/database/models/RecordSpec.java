
package org.meimen.meimen.database.models;

import com.yahoo.squidb.annotations.ColumnSpec;
import com.yahoo.squidb.annotations.TableModelSpec;

@TableModelSpec(className = "Record", tableName = "records")
public class RecordSpec {

    @ColumnSpec(constraints = "NOT NULL")
    String userId;

    @ColumnSpec(defaultValue = "0")
    long timestamp;

    @ColumnSpec(defaultValue = "0")
    int duration;

}
