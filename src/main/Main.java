/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.JFrame;

/**
 *
 * @author Ayoze Gil
 */
public class Main {

    static final int NUM_BOLAS = 200;
    static int ANCHO = 600;
    static int ALTO = 600;
    static final int MIN_RADIO = 5;
    static final int MAX_RADIO = 10;
    static final int MAX_V = 4;
    static Lienzo panel;
    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        //Se crea una instancia JFrame que contenga un objeto Canvas en el
        //que se dibujará nuestra animación.
        JFrame ventanaPrincipal;
        ventanaPrincipal=new JFrame();
        ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaPrincipal.setBounds(10,10,ANCHO+6,ALTO+29);
        ventanaPrincipal.setResizable(false);
        ventanaPrincipal.setVisible(true);
        panel = new Lienzo();
        ventanaPrincipal.add(panel);
        panel.run();
    }
}
