 

package persistence;

import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Encapsulates Patient Data.
 *
 * @author Benjamin Gross
 */
public class MyPatient {

    private int internalId;
    private String stableId;
    private CancerStudy cancerStudy;

	private Map<String, ClinicalData> clinicalDataMap;
    private static final Logger logger = Logger.getLogger(MyPatient.class);

    public MyPatient(CancerStudy cancerStudy, String stableId)
    {
        this(cancerStudy, stableId, -1, new HashMap<String, ClinicalData>());
    }

    public MyPatient(CancerStudy cancerStudy, String stableId, int internalId)
    {
        this(cancerStudy, stableId, internalId, new HashMap<String, ClinicalData>());
    }

    public MyPatient(CancerStudy cancerStudy, String stableId, int internalId, Map<String, ClinicalData> clinicalDataMap)
    {
        this.cancerStudy = cancerStudy;
        this.stableId = stableId;
        this.internalId = internalId;
		this.clinicalDataMap = clinicalDataMap;
    }

    @Override
    public String toString()
    {
        return stableId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyPatient)) {
            return false;
        }
        
        MyPatient anotherPatient = (MyPatient)obj;
        if (this.internalId != anotherPatient.getInternalId()) {
            return false;
        }
        
        if (this.cancerStudy.getInternalId() != anotherPatient.getCancerStudy().getInternalId()) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.stableId != null ? this.stableId.hashCode() : 0);
        hash = 41 * hash + this.cancerStudy.getInternalId();
        return hash;
    }

    public CancerStudy getCancerStudy()
    {
        return cancerStudy;
    }

    public int getInternalId()
    {
        return internalId;
    }

    public String getStableId()
    {
        return stableId;
    }

    public Double getOverallSurvivalMonths()
    { 
		return getDoubleValue(ClinicalAttribute.OS_MONTHS);
	}
    public String getOverallSurvivalStatus()
    {
		return getStringValue(ClinicalAttribute.OS_STATUS);
	}
    public Double getDiseaseFreeSurvivalMonths()
    {
		return getDoubleValue(ClinicalAttribute.DFS_MONTHS);
	}
    public String getDiseaseFreeSurvivalStatus()
    {
		return getStringValue(ClinicalAttribute.DFS_STATUS);
	}
    public Double getAgeAtDiagnosis()
    {
		return getDoubleValue(ClinicalAttribute.AGE_AT_DIAGNOSIS);
	}

	private Double getDoubleValue(String attribute)
    {
		ClinicalData data = clinicalDataMap.get(attribute);
        if (data == null || data.getAttrVal().length() == 0 ||
                data.getAttrVal().equals(ClinicalAttribute.NA) ||
                data.getAttrVal().equals(ClinicalAttribute.MISSING)) {
            return null;
        }
        try {
            return Double.valueOf(data.getAttrVal());
        } catch (NumberFormatException e) {
            logger.warn("Can't handle clinical attribute of patient: " + stableId);
            return null;
        }
	}

	private String getStringValue(String attribute)
    {
		ClinicalData data = clinicalDataMap.get(attribute);
		return (data == null || data.getAttrVal().length() == 0 ||
				data.getAttrVal().equals(ClinicalAttribute.NA) ||
				data.getAttrVal().equals(ClinicalAttribute.MISSING)) ? null : data.getAttrVal();
	}
}
