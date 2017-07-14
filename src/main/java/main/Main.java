/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author mcaikovs
 */
public class Main {

    public static void main(String... args) throws Exception {
         MyProperties p=new  MyProperties();
         new WatchDir(p).processEvents();
    }
}
