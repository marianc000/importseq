/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    public static void printCollection(Collection<String> l, String title) {
        System.out.println(">>>" + title);
        printCollection(l);
        System.out.println("<<<" + title);
    }

    public static void printMap(Map map) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        for (Object k : map.keySet()) {
            System.out.println(k + "\t" + map.get(k));
        }
        System.out.println(map.size());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
