package org.apache.cassandra.db;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import static org.apache.cassandra.db.TableTest.assertColumns;
import org.apache.cassandra.CleanupHelper;
import static org.apache.cassandra.Util.column;

public class MultitableTest extends CleanupHelper
{
    @Test
    public void testSameCFs() throws IOException, ExecutionException, InterruptedException
    {
        Table table1 = Table.open("Table1");
        Table table2 = Table.open("Table2");

        RowMutation rm;
        ColumnFamily cf;

        rm = new RowMutation("Table1", "keymulti");
        cf = ColumnFamily.create("Table1", "Standard1");
        cf.addColumn(column("col1", "val1", 1L));
        rm.add(cf);
        rm.apply();

        rm = new RowMutation("Table2", "keymulti");
        cf = ColumnFamily.create("Table2", "Standard1");
        cf.addColumn(column("col2", "val2", 1L));
        rm.add(cf);
        rm.apply();

        table1.getColumnFamilyStore("Standard1").forceBlockingFlush();
        table2.getColumnFamilyStore("Standard1").forceBlockingFlush();

        assertColumns(table1.get("keymulti", "Standard1"), "col1");
        assertColumns(table2.get("keymulti", "Standard1"), "col2");
    }
}
