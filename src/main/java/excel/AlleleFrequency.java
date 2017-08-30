/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package excel;

/**
 *
 * @author mcaikovs
 */
public class AlleleFrequency {

    float refCount, altCount;

    public String getRefCount() {
        return String.valueOf(getRefCountAsInt());
    }

    public String getAltCount() {
        return String.valueOf(getAltCountAsInt());
    }

    public int getRefCountAsInt() {
        return (int) refCount;
    }

    public int getAltCountAsInt() {
        return (int) altCount;
    }
    float frequencyPercent;

    public float getFrequencyPercent() {
        return frequencyPercent;
    }

    public float getCalculatedFrequencyPercent() {
        return ((float) Math.round(altCount * 100 / (altCount + refCount) * 10)) / 10;
    }

    public AlleleFrequency(String frequencyPercentStr, String coverageStr) {
        frequencyPercent = Float.valueOf(frequencyPercentStr.replace("%", ""));
        float coverage = Float.valueOf(coverageStr.replace("X", ""));
        float altCountFloat = frequencyPercent * coverage / 100;
        this.altCount = Math.round(altCountFloat);
        this.refCount = coverage - this.altCount;
      }

    @Override
    public String toString() {
        return "refCount=" + getRefCount() + "; altCount=" + getAltCount() + "; frequencyPercent()=" + getFrequencyPercent() + "; calculatedFrequencyPercent()=" + getCalculatedFrequencyPercent();
    }

}
