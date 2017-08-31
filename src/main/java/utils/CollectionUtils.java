/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author mcaikovs
 */
public class CollectionUtils {

    public static void printListAsTabbedRow(List<String> l) {
        for (int c = 0; c < l.size(); c++) {
            System.out.print(l.get(c));
            if (c < l.size() - 1) {
                System.out.print("\t");
            } else {
                System.out.print("\n");
            }
        }
    }

    public static void printCollection(Collection<String> l) {
        for (String s : l) {
            System.out.println(s);
        }
    }
}
