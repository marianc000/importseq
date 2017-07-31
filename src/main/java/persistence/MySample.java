 
package persistence;

import java.util.EnumSet;
import java.util.Set;
 

import java.util.regex.*;

/**
 * Encapsulates Sample Data.
 *
 * @author Benjamin Gross
 */
public class MySample {

    public static enum Type
    {
        PRIMARY_SOLID_TUMOR("Primary Solid Tumor"),
        RECURRENT_SOLID_TUMOR("Recurrent Solid Tumor"),
        PRIMARY_BLOOD_TUMOR("Primary Blood Tumor"),
        RECURRENT_BLOOD_TUMOR("Recurrent Blood Tumor"),
        METASTATIC("Metastatic"),
        BLOOD_DERIVED_NORMAL("Blood Derived Normal"),
        SOLID_TISSUES_NORMAL("Solid Tissues Normal");

        private String propertyName;
        
        Type(String propertyName) { this.propertyName = propertyName; }
        public String toString() { return propertyName; }
        public boolean isNormal() { return this==BLOOD_DERIVED_NORMAL || this==SOLID_TISSUES_NORMAL; }
        public static Set<Type> normalTypes() { return EnumSet.of(BLOOD_DERIVED_NORMAL, SOLID_TISSUES_NORMAL); }

        static public boolean has(String value)
        {
            if (value == null) return false;
            try { 
                return valueOf(value.toUpperCase()) != null; 
            }
            catch (IllegalArgumentException x) { 
                return false;
            }
        }
    }

    private int internalId;
    private String stableId;
    private Type sampleType;
    private int internalPatientId;
    private String cancerTypeId;

    public MySample(String stableId, int internalPatientId, String cancerTypeId, String sampleType)
    {
        this(stableId, internalPatientId, cancerTypeId);
        this.sampleType = getType(stableId, sampleType);
    }
    
    public MySample(int internalId, String stableId, int internalPatientId, String cancerTypeId)
    {
        this(stableId, internalPatientId, cancerTypeId);
        this.internalId = internalId;
    }

    public MySample(String stableId, int internalPatientId, String cancerTypeId)
    {
        this.stableId = stableId;
        this.sampleType = getType(stableId, null);
        this.internalPatientId = internalPatientId;
		this.cancerTypeId = cancerTypeId;
    }

    private Type getType(String stableId, String sampleType)
    {
        Matcher tcgaSampleBarcodeMatcher = MyStableIdUtil.TCGA_SAMPLE_TYPE_BARCODE_REGEX.matcher(stableId);
        if (tcgaSampleBarcodeMatcher.find()) {
            return MyStableIdUtil.getTypeByTCGACode(tcgaSampleBarcodeMatcher.group(1));
        }
        else if (sampleType != null && Type.has(sampleType)) {
            return Type.valueOf(sampleType.toUpperCase());
        }
        else {
            return Type.PRIMARY_SOLID_TUMOR;
        }
    }

    public int getInternalId()
    {
        return internalId;
    }

    public String getStableId()
    {
        return stableId;
    }
   
    public Type getType()
    {
        return sampleType;
    }

    public int getInternalPatientId()
    {
        return internalPatientId;
    }

    public String getCancerTypeId()
    {
        return cancerTypeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MySample)) {
            return false;
        }
        
        MySample anotherSample = (MySample)obj;
        return (this.internalId == anotherSample.getInternalId());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.stableId != null ? this.stableId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Sample{" + "internalId=" + internalId + ", stableId=" + stableId + ", sampleType=" + sampleType + ", internalPatientId=" + internalPatientId + ", cancerTypeId=" + cancerTypeId + '}';
    }
    
}
