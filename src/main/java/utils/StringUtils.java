/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author mcaikovs
 */
public class StringUtils {

    public static String truncate(String str, int maxLength) {
 
        if (str.length() > maxLength) {
            return str.substring(0, maxLength);
        }
        return str;
    }
}
