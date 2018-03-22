/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Machine.Mpu;

/**
 *
 * @author Yves Bodê
 */
public class Main {
    public static void main(String[] args) {
        Mpu machine = new Mpu(0x0000);
        MainWindow mainWindow = new MainWindow(machine);
        
        try {
            machine.setMemoryFromFile("MemoryFile64k");
        } catch (Exception e) {
        }
        
        try {
            mainWindow.display();
        } catch (Exception e) {
            throw e;
        }
        
    }
    
}
