/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Graphics;
import static main.Main.*;

/**
 *
 * @author Ayoze Gil
 */
public class Bola{
    
    //Atributos
    private int x;
    private int y;
    private int r;

    //Constructores
    public Bola(){
        do{
            this.r = (int)((Math.random()*(MAX_RADIO-MIN_RADIO)+MIN_RADIO));
        }while(r==0);
        this.x = (int)((Math.random()*(ANCHO-2*r))+r);
        this.y = (int)((Math.random()*(ALTO-2*r))+r);      
    }

    public Bola(int x, int y, int r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    //El método pintar mueve las bolas a su posición relativa del primer
    //cuadrante para ser pintadas en cada imagen de cuadrante. Posteriormente
    //dibuja la bola en las coordenadas indicadas.
    public void pintar(Graphics g){
        
        int mitadX = ANCHO/2;
        int mitadY = ALTO/2;
        int x = this.x;
        int y = this.y;

        //Cuadrante 2
        if (g.equals(panel.getgHilo()[1]))
            x = x-mitadX;
        //Cuadrante 3
        if (g.equals(panel.getgHilo()[2]))
            y = y-mitadY;
        //Cuadrante 4
        if (g.equals(panel.getgHilo()[3])){
            x = x-mitadX;
            y = y-mitadY;
        }
        g.fillOval(x-r, y-r, r*2, r*2);
    }
    
    //Método que mueve la bola
    public void mover(int a, int b){       
        this.x = this.x + a;
        this.y = this.y + b;
    }

    //Métodos getters.
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getR() {
        return r;
    }
}
