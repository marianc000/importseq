package persistence;

import org.mskcc.cbio.portal.util.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.mskcc.cbio.portal.dao.DaoException;
import org.mskcc.cbio.portal.dao.JdbcUtil;

/**
 * To speed up CGDS data loading, bulk load from files using MySQL "LOAD DATA
 * INFILE" functionality. Intercept each record write in the normal load, buffer
 * it in a temp file, and load the temp file when done. NOT thread-safe.
 *
 * @author arthur goldberg In the future, would be cooler to implement this by
 * overloading the JDBC Connection.prepareStatement and PreparedStatement.setX()
 * calls.
 */
public class MyMySQLbulkLoader {

    private static boolean bulkLoad = false;
    private static boolean relaxedMode = false;

    private static final Map<String, MyMySQLbulkLoader> mySQLbulkLoaders = new LinkedHashMap<String, MyMySQLbulkLoader>();

    /**
     * Get a MySQLbulkLoader
     *
     * @param dbName database name
     * @return
     */
    public static MyMySQLbulkLoader getMySQLbulkLoader(String dbName) {

        MyMySQLbulkLoader mySQLbulkLoader = mySQLbulkLoaders.get(dbName);
        if (mySQLbulkLoader == null) {
            mySQLbulkLoader = new MyMySQLbulkLoader(dbName);
            mySQLbulkLoaders.put(dbName, mySQLbulkLoader);
        }
        return mySQLbulkLoader;
    }

    /**
     * Flushes all pending data from the bulk writer. Temporarily disables
     * referential integrity while it does so, largely because MySQL uses a
     * weird model and expects referential integrity at each record, not just at
     * the end of the transaction.
     *
     * @return the number of rows added
     * @throws DaoException
     */
   
    private String tempFileName = null;
    private File tempFileHandle = null;
    private BufferedWriter tempFileWriter = null;
    private String tableName;
    private final String tempTableSuffix = ".tempTable";
    private int rows;
    // TODO: make configurable
    private static final long numDebuggingRowsToPrint = 0;

    private MyMySQLbulkLoader(String tableName) {
        try {
            openTempFile(tableName);
            this.tableName = tableName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open temp file for table 'tableName'. note that auto_increment fields
     * must be handled specially.
     *
     * @param tableName
     * @throws FileNotFoundException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void openTempFile(String tableName) throws IOException {

        tempFileHandle = File.createTempFile(tableName, tempTableSuffix, FileUtils.getTempDirectory());

        tempFileName = tempFileHandle.getAbsolutePath();

        if (!tempFileHandle.exists()) {
            throw new FileNotFoundException("File does not exist: " + tempFileHandle);
        }
        if (!tempFileHandle.isFile()) {
            throw new IllegalArgumentException("Should not be a directory: " + tempFileHandle);
        }
        if (!tempFileHandle.canWrite()) {
            throw new IllegalArgumentException("File cannot be written: " + tempFileHandle);
        }

        // to improve performance use buffering; FileWriter always assumes default encoding is OK!
        this.tempFileWriter = new BufferedWriter(new FileWriter(tempFileHandle, false));
    }

    /**
     * write a record's fields, in order, to the table's temp file. if no fields
     * are provided, writes no record. fields are TAB separated.
     *
     * @param fieldValues
     */
    public void insertRecord(String... fieldValues) {
        if (fieldValues.length == 0) {
            return;
        }
        try {
            tempFileWriter.write(escapeValue(fieldValues[0]));
            for (int i = 1; i < fieldValues.length; i++) {
                tempFileWriter.write("\t");
                tempFileWriter.write(escapeValue(fieldValues[i]));
            }
            tempFileWriter.write("\n");;

            if (rows++ < numDebuggingRowsToPrint) {
                StringBuffer sb = new StringBuffer(escapeValue(fieldValues[0]));
                for (int i = 1; i < fieldValues.length; i++) {
                    sb.append("\t").append(escapeValue(fieldValues[i]));
                }
                System.err.println("MySQLbulkLoader: Wrote " + sb.toString() + " to '" + tempFileName + "'.");
            }
        } catch (IOException e) {
            System.err.println("Unable to write to temp file.\n");
            e.printStackTrace();
        }
    }

    private String escapeValue(String value) {
        if (value == null) {
            return "\\N";
        }

        return value.replace("\r", "").replaceAll("\n", "\\\\n").replace("\t", "\\t");
    }

    /**
     * load the temp file maintained by the MySQLbulkLoader into the DMBS.
     * truncates the temp file, and leaves it open for more insertRecord()
     * operations. returns number of records inserted.
     *
     * TODO: perhaps instead of having each program that uses a DAO that uses
     * bulk loading call 'completeInsert', get MySQLbulkLoader created by a
     * factory, and have the factory remember to load all the tables from all
     * the temp files before the program exits.
     *
     * @return number of records inserted
     * @throws DaoException
     * @throws IOException
     */
   
    public String getTempFileName() {
        return tempFileName;
    }

    public String getTableName() {
        return tableName;
    }

    public static boolean isBulkLoad() {
        return bulkLoad;
    }

    public static void bulkLoadOn() {
        MyMySQLbulkLoader.bulkLoad = true;
    }

    public static void bulkLoadOff() {
        MyMySQLbulkLoader.bulkLoad = false;
    }

    public static void relaxedModeOn() {
        MyMySQLbulkLoader.relaxedMode = true;
    }

    public static void relaxedModeOff() {
        MyMySQLbulkLoader.relaxedMode = false;
    }

}
