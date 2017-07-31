 

package persistence;
 

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class related to ExtendedMutation.
 */
public class MyExtendedMutationUtil
{
	public static final String NOT_AVAILABLE = "NA";

	public static String getCaseId(String barCode)
	{
		// process bar code
		// an example bar code looks like this:  TCGA-13-1479-01A-01W

		String barCodeParts[] = barCode.split("-");

		String caseId = null;

		try
		{
			caseId = barCodeParts[0] + "-" + barCodeParts[1] + "-" + barCodeParts[2];

			// the following condition was prompted by case ids coming from
			// private cancer studies (like SKCM_BROAD) with case id's of
			// the form MEL-JWCI-WGS-XX or MEL-Ma-Mel-XX or MEL-UKRV-Mel-XX
			if (!barCode.startsWith("TCGA") &&
			    barCodeParts.length == 4)
			{
				caseId += "-" + barCodeParts[3];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			caseId = barCode;
		}

		return caseId;
	}

	/**
	 * Determines the most accurate protein change value for the given mutation.
	 *
	 * If there is an annotator value, returns that value.
	 * If no annotator value, then tries Amino Acid Change value.
	 * If none of the above is valid then returns "MUTATED".
	 *
	 * @param parts     current mutation as split parts of the line
	 * @param record    MAF record for the current line
	 * @return          most accurate protein change
	 */
	public static String getProteinChange(String[] parts, MyMafRecord record)
	{
		// try annotator value first
		//String proteinChange = record.getOncotatorProteinChange();
		String proteinChange = record.getProteinChange();

		// if protein change is not valid, try amino acid change value
		if (!isValidProteinChange(proteinChange))
		{
			proteinChange = record.getAminoAcidChange();
		}

		// if none is valid, then use the string "MUTATED"
		if (!isValidProteinChange(proteinChange))
		{
			proteinChange = "MUTATED";
		}

		// also remove the starting "p." string if any
		proteinChange = normalizeProteinChange(proteinChange);

		return proteinChange;
	}

	/**
	 * Removes the starting "p." (if any) from the given
	 * amino acid change string.
	 *
	 * @param aminoAcidChange   aa change string to be normalized
	 * @return                  normalized aa change string
	 */
	public static String normalizeProteinChange(String aminoAcidChange)
	{
		String pDot = "p.";

		// remove the starting "p." string if any
		if (aminoAcidChange.startsWith(pDot))
		{
			aminoAcidChange = aminoAcidChange.substring(pDot.length());
		}

		return aminoAcidChange;
	}

	public static int getProteinPosStart(String proteinPosition, String proteinChange)
	{
		// parts[0] is the protein start-end positions, parts[1] is the length
		String[] parts = proteinPosition.split("/");

		int position = MyTabDelimitedFileUtil.getPartInt(0, parts[0].split("-"));

		// there is a case where the protein change is "-"
		if (position == MyTabDelimitedFileUtil.NA_INT)
		{
			// try to extract it from protein change value
			Pattern p = Pattern.compile(".*[A-Z]([0-9]+)[^0-9]+");
			Matcher m = p.matcher(proteinChange);

			if (m.find())
			{
				position = Integer.parseInt(m.group(1));
			}
		}

		return position;
	}

	public static int getProteinPosEnd(String proteinPosition, String proteinChange)
	{
		// parts[0] is the protein start-end positions, parts[1] is the length
		String[] parts = proteinPosition.split("/");

		int end = MyTabDelimitedFileUtil.getPartInt(1, parts[0].split("-"));

		// if no end position is provided,
		// then use start position as end position
		if (end == -1)
		{
			end = getProteinPosStart(proteinPosition, proteinChange);
		}

		return end;
	}

	public static boolean isValidProteinChange(String proteinChange)
	{
		boolean invalid = proteinChange == null ||
		                  proteinChange.length() == 0 ||
		                  proteinChange.equalsIgnoreCase("NULL") ||
		                  proteinChange.equalsIgnoreCase(NOT_AVAILABLE);

		return !invalid;
	}

	public static boolean isAcceptableMutation(String mutationType)
	{
		// check for null or NA values
		if (mutationType == null ||
		    mutationType.length() == 0 ||
		    mutationType.equals("NULL") ||
		    mutationType.equals(MyTabDelimitedFileUtil.NA_STRING))
		{
			return false;
		}

		// check for the type
		boolean silent = mutationType.toLowerCase().startsWith("silent");
		boolean loh = mutationType.toLowerCase().startsWith("loh");
		boolean wildtype = mutationType.toLowerCase().startsWith("wildtype");
		boolean utr3 = mutationType.toLowerCase().startsWith("3'utr");
		boolean utr5 = mutationType.toLowerCase().startsWith("5'utr");
		boolean flank5 = mutationType.toLowerCase().startsWith("5'flank");
		boolean igr = mutationType.toLowerCase().startsWith("igr");
		boolean rna = mutationType.equalsIgnoreCase("rna");

		return !(silent || loh || wildtype || utr3 || utr5 || flank5 || igr || rna);
	}

	public static String getMutationType(MyMafRecord record)
	{
		String mutationType = record.getOncotatorVariantClassification();

		if (mutationType == null ||
		    mutationType.length() == 0 ||
		    mutationType.equals("NULL") ||
		    mutationType.equals(MyTabDelimitedFileUtil.NA_STRING))
		{
			mutationType = record.getVariantClassification();
		}

		return mutationType;
	}

	public static int getTumorAltCount(MyMafRecord record) {
		int result = MyTabDelimitedFileUtil.NA_INT ;

		if (record.getTumorAltCount() != MyTabDelimitedFileUtil.NA_INT) {
			result = record.getTumorAltCount();
		} else if(record.getTVarCov() != MyTabDelimitedFileUtil.NA_INT) {
			result = record.getTVarCov();
		} else if((record.getTumorDepth() != MyTabDelimitedFileUtil.NA_INT) &&
		          (record.getTumorVaf() != MyTabDelimitedFileUtil.NA_INT)) {
			result = Math.round(record.getTumorDepth() * record.getTumorVaf());
		}

		return result;
	}

	public static int getTumorRefCount(MyMafRecord record) {
		int result = MyTabDelimitedFileUtil.NA_INT;

		if (record.getTumorRefCount() != MyTabDelimitedFileUtil.NA_INT) {
			result = record.getTumorRefCount();
		} else if((record.getTVarCov() != MyTabDelimitedFileUtil.NA_INT) &&
		          (record.getTTotCov() != MyTabDelimitedFileUtil.NA_INT)) {
			result = record.getTTotCov()-record.getTVarCov();
		} else if((record.getTumorDepth() != MyTabDelimitedFileUtil.NA_INT) &&
		          (record.getTumorVaf() != MyTabDelimitedFileUtil.NA_INT)) {
			result = record.getTumorDepth() - Math.round(record.getTumorDepth() * record.getTumorVaf());
		}

		return result;
	}

	public static int getNormalAltCount(MyMafRecord record) {
		int result = MyTabDelimitedFileUtil.NA_INT ;

		if (record.getNormalAltCount() != MyTabDelimitedFileUtil.NA_INT) {
			result = record.getNormalAltCount();
		} else if(record.getNVarCov() != MyTabDelimitedFileUtil.NA_INT) {
			result = record.getNVarCov();
		} else if((record.getNormalDepth() != MyTabDelimitedFileUtil.NA_INT) &&
		          (record.getNormalVaf() != MyTabDelimitedFileUtil.NA_INT)) {
			result = Math.round(record.getNormalDepth() * record.getNormalVaf());
		}

		return result;
	}

	public static int getNormalRefCount(MyMafRecord record) {
		int result = MyTabDelimitedFileUtil.NA_INT;

		if (record.getNormalRefCount() != MyTabDelimitedFileUtil.NA_INT) {
			result = record.getNormalRefCount();
		} else if((record.getNVarCov() != MyTabDelimitedFileUtil.NA_INT) &&
		          (record.getNTotCov() != MyTabDelimitedFileUtil.NA_INT)) {
			result = record.getNTotCov()-record.getNVarCov();
		} else if((record.getNormalDepth() != MyTabDelimitedFileUtil.NA_INT) &&
		          (record.getNormalVaf() != MyTabDelimitedFileUtil.NA_INT)) {
			result = record.getNormalDepth() - Math.round(record.getNormalDepth() * record.getNormalVaf());
		}

		return result;
	}

	/**
	 * Generates a new ExtendedMutation instance with default values.
	 * The mutation instance returned by this method will not have
	 * any field with a "null" value.
	 *
	 * @return  a mutation instance initialized by default values
	 */
	public static MyExtendedMutation newMutation()
	{
		Integer defaultInt = MyTabDelimitedFileUtil.NA_INT;
		String defaultStr = MyTabDelimitedFileUtil.NA_STRING;
		//Long defaultLong = MyTabDelimitedFileUtil.NA_LONG;
		Long defaultLong = -1L;
		Float defaultFloat = MyTabDelimitedFileUtil.NA_FLOAT;
		MyCanonicalGene defaultGene = new MyCanonicalGene("INVALID");

		MyExtendedMutation mutation = new MyExtendedMutation();

		mutation.setGeneticProfileId(defaultInt);
		mutation.setSampleId(defaultInt);
		mutation.setGene(defaultGene);
		mutation.setSequencingCenter(defaultStr);
		mutation.setSequencer(defaultStr);
		mutation.setProteinChange(defaultStr);
		mutation.setMutationType(defaultStr);
		mutation.setChr(defaultStr);
		mutation.setStartPosition(defaultLong);
		mutation.setEndPosition(defaultLong);
		mutation.setValidationStatus(defaultStr);
		mutation.setMutationStatus(defaultStr);
		mutation.setFunctionalImpactScore(defaultStr);
		mutation.setFisValue(defaultFloat);
		mutation.setLinkXVar(defaultStr);
		mutation.setLinkPdb(defaultStr);
		mutation.setLinkMsa(defaultStr);
		mutation.setNcbiBuild(defaultStr);
		mutation.setStrand(defaultStr);
		mutation.setVariantType(defaultStr);
		mutation.setAllele(defaultStr, defaultStr, defaultStr);
		mutation.setDbSnpRs(defaultStr);
		mutation.setDbSnpValStatus(defaultStr);
		mutation.setMatchedNormSampleBarcode(defaultStr);
		mutation.setMatchNormSeqAllele1(defaultStr);
		mutation.setMatchNormSeqAllele2(defaultStr);
		mutation.setTumorValidationAllele1(defaultStr);
		mutation.setTumorValidationAllele2(defaultStr);
		mutation.setMatchNormValidationAllele1(defaultStr);
		mutation.setMatchNormValidationAllele2(defaultStr);
		mutation.setVerificationStatus(defaultStr);
		mutation.setSequencingPhase(defaultStr);
		mutation.setSequenceSource(defaultStr);
		mutation.setValidationMethod(defaultStr);
		mutation.setScore(defaultStr);
		mutation.setBamFile(defaultStr);
		mutation.setTumorAltCount(defaultInt);
		mutation.setTumorRefCount(defaultInt);
		mutation.setNormalAltCount(defaultInt);
		mutation.setNormalRefCount(defaultInt);
		mutation.setOncotatorDbSnpRs(defaultStr);
		mutation.setOncotatorCodonChange(defaultStr);
		mutation.setOncotatorRefseqMrnaId(defaultStr);
		mutation.setOncotatorUniprotName(defaultStr);
		mutation.setOncotatorUniprotAccession(defaultStr);
		mutation.setOncotatorProteinPosStart(defaultInt);
		mutation.setOncotatorProteinPosEnd(defaultInt);
		mutation.setCanonicalTranscript(true);

		return mutation;
	}
}
