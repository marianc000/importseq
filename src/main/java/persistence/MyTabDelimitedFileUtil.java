package persistence;

/**
 * Generic utility class providing simple utility functions for tab delimited
 * data files.
 *
 * @author Selcuk Onur Sumer
 */
public class MyTabDelimitedFileUtil {

    public final static String NA_STRING = "NA";
    public final static long NA_LONG = Long.MIN_VALUE;
    // TODO use MIN instead of -1, we may have fields with negative values
    public final static int NA_INT = -1;
    public final static float NA_FLOAT = -1;

    /**
     * If field is not found in header or data line, or is empty, it just
     * returns empty field value "NA".
     *
     * @param index: index of the column to parse. Can be set to -1 if the
     * column was not found in header. This method will return "NA" in this
     * case.
     * @param parts: the data line parts, i.e. the line split by separator.
     * @return : the value as is, or "NA" if column was empty, not present in
     * file (indicated by index=-1), or not present in data line (parts
     * parameter above).
     */
    public static String getPartString(int index, String[] parts) {
        try {
            if (parts[index].length() == 0) {
                return NA_STRING;
            } else {
                return parts[index];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return NA_STRING;
        }
    }

    /**
     * Return the trimmed string from the column, or an empty string if -1.
     *
     * Require the column to exist before the end of the data line. This can be
     * used instead of getPartString() if NA may be a meaningful value and the
     * file is expected to have been validated.
     *
     * @param index : index of the column to parse. May be set to -1 if the
     * column was not found in header, to return "".
     * @param parts: the data line parts, i.e. the line split by separator.
     *
     * @return : the value as is, or "" if the index is -1.
     */
    public static String getPartStringAllowEmpty(int index, String[] parts) {
        try {
            if (index < 0) {
                //return empty string:
                return "";
            }
            //else just return as is, trimmed version:
            return parts[index].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            // all lines must have the same number of columns, and the
            // validation script should never allow this to reach the loader
            throw new RuntimeException(
                    "Unexpected error while parsing column nr: " + (index + 1),
                    e);
        }
    }

    public static Long getPartLong(int index, String[] parts) {
        try {
            String part = parts[index];
            return Long.parseLong(part);
        } catch (ArrayIndexOutOfBoundsException e) {
            return NA_LONG;
        } catch (NumberFormatException e) {
            return NA_LONG;
        }
    }

    public static Integer getPartInt(int index, String[] parts) {
        try {
            String part = parts[index];
            return (int) (Float.parseFloat(part));
        } catch (ArrayIndexOutOfBoundsException e) {
            return NA_INT;
        } catch (NumberFormatException e) {
            return NA_INT;
        }
    }

    public static Float getPartPercentage(int index, String[] parts) {
        try {
            float result = NA_FLOAT;
            String part = parts[index];
            if (part.contains("%")) {
                result = Float.parseFloat(part.replace("%", "")) / Float.parseFloat("100");
            } else {
                result = Float.parseFloat(part);
            }
            return result;
        } catch (ArrayIndexOutOfBoundsException e) {
            return NA_FLOAT;
        } catch (NumberFormatException e) {
            return NA_FLOAT;
        }
    }

    public static Float getPartFloat(int index, String[] parts) {
        try {
            String part = parts[index];
            return Float.parseFloat(part);
        } catch (ArrayIndexOutOfBoundsException e) {
            return NA_FLOAT;
        } catch (NumberFormatException e) {
            return NA_FLOAT;
        }
    }

    // returning MIN_VALUE instead of NA_FLOAT
    // use this one if -1 is not a safe "NA" value.
    public static Float getPartFloat2(int index, String[] parts) {
        try {
            String part = parts[index];
            return Float.parseFloat(part);
        } catch (ArrayIndexOutOfBoundsException e) {
            return Float.MIN_VALUE;
        } catch (NumberFormatException e) {
            return Float.MIN_VALUE;
        }
    }

    public static String adjustDataLine(String dataLine,
            int headerCount) {
        String line = dataLine;
        String[] parts = line.split("\t", -1);

        // diff should be zero if (# of headers == # of data cols)
        int diff = headerCount - parts.length;

        // number of header columns are more than number of data columns
        if (diff > 0) {
            // append appropriate number of tabs
            for (int i = 0; i < diff; i++) {
                line += "\t";
            }
        } // number of data columns are more than number of header columns
        else if (diff < 0) {
            line = "";

            // just truncate the data (discard the trailing columns)
            for (int i = 0; i < headerCount; i++) {
                line += parts[i];

                if (i < headerCount - 1) {
                    line += "\t";
                }
            }
        }

        return line;
    }
}
