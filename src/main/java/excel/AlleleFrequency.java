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

    int refCount;
    int altCount;

    public int getRefCount() {
        return refCount;
    }

    public int getAltCount() {
        return altCount;
    }

    public AlleleFrequency(String frequencyPercentStr, String coverageStr) {
        System.out.println(frequencyPercentStr + "; " + coverageStr);
        float frequencyPercent = Float.valueOf(frequencyPercentStr.replace("%", ""));
        float coverage = Float.valueOf(coverageStr.replace("X", ""));
        this.altCount = Math.round(coverage * frequencyPercent / 100);
        this.refCount = (int) coverage - this.altCount;
    }

    @Override
    public String toString() {
        return "refCount=" + refCount + "; altCount=" + altCount;
    }

}
