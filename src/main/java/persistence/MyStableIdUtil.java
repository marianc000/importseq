 

package persistence;

 
import java.util.regex.*;

public class MyStableIdUtil
{
    public static final String TCGA_BARCODE_PREFIX = "TCGA";

    public static final Pattern TCGA_SAMPLE_BARCODE_REGEX =
        Pattern.compile("^(TCGA-\\w\\w-\\w\\w\\w\\w-\\d\\d).*$");

    public static final Pattern TCGA_SAMPLE_TYPE_BARCODE_REGEX =
        Pattern.compile("^TCGA-\\w\\w-\\w\\w\\w\\w-(\\d\\d).*$");

    public static final Pattern TCGA_PATIENT_BARCODE_FROM_SAMPLE_REGEX =
        Pattern.compile("^(TCGA-\\w\\w-\\w\\w\\w\\w)\\-\\d\\d.*$");

	public static String getPatientId(String barcode)
	{
        return getId(barcode, false);
	}

	public static String getSampleId(String barcode)
	{
        return getId(barcode, true);
	}

    private static String getId(String barcode, boolean forSample)
    {
		// do not process non-TCGA bar codes...
		if (!barcode.startsWith(TCGA_BARCODE_PREFIX)) {
			return barcode;
		}

        String id = null;
		String barcodeParts[] = clean(barcode).split("-");
		try {
            // an example bar code looks like this:  TCGA-13-1479-01A-01W
			id = barcodeParts[0] + "-" + barcodeParts[1] + "-" + barcodeParts[2];
            if (forSample) {
                id += "-" + barcodeParts[3];
                Matcher tcgaSampleBarcodeMatcher = TCGA_SAMPLE_BARCODE_REGEX.matcher(id);
                id = (tcgaSampleBarcodeMatcher.find()) ? tcgaSampleBarcodeMatcher.group(1) : id;
            }
		}
        catch (ArrayIndexOutOfBoundsException e) {
            // many (all?) barcodes in overrides are just patient id's - tack on a primary solid tumor code
            // (we don't have any blood cancer overrides that need a sample code)
			id = barcode + "-01";
		}

		return id;
    }

    private static String clean(String barcode)
    {
        if (barcode.contains("Tumor")) {
            return barcode.replace("Tumor", "01");
        }
        else if (barcode.contains("Normal")) {
            return barcode.replace("Normal", "11");
        }
        else {
            return barcode;
        }
    }

    public static MySample.Type getTypeByTCGACode(String tcgaCode)
    {
        if (tcgaCode.equals("01")) {
            return MySample.Type.PRIMARY_SOLID_TUMOR;
        }
        else if (tcgaCode.equals("02")) {
            return MySample.Type.RECURRENT_SOLID_TUMOR;
        }
        else if (tcgaCode.equals("03")) {
            return MySample.Type.PRIMARY_BLOOD_TUMOR;
        }
        else if (tcgaCode.equals("04")) {
            return MySample.Type.RECURRENT_BLOOD_TUMOR;
        }
        else if (tcgaCode.equals("06")) {
            return MySample.Type.METASTATIC;
        }
        else if (tcgaCode.equals("10")) {
            return MySample.Type.BLOOD_DERIVED_NORMAL;
        }
        else if (tcgaCode.equals("11")) {
            return MySample.Type.SOLID_TISSUES_NORMAL;
        }
        else {
            return MySample.Type.PRIMARY_SOLID_TUMOR;
        }
    }

    public static boolean isNormal(String barcode)
    {
        Matcher tcgaSampleBarcodeMatcher = TCGA_SAMPLE_TYPE_BARCODE_REGEX.matcher(barcode);
        if (tcgaSampleBarcodeMatcher.find()) {
            MySample.Type type = getTypeByTCGACode(tcgaSampleBarcodeMatcher.group(1));
            return (type.equals(MySample.Type.BLOOD_DERIVED_NORMAL) || type.equals(MySample.Type.SOLID_TISSUES_NORMAL));
        }
        return false;
    }
}
