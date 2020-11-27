/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import static main.Main.*;

/**
 *
 * @author Ayoze Gil
 */
public class Hilo extends Thread{
    //Atributos
    private final ArrayList<Integer> indices;
    private final ArrayList<Integer> indicesAux;
    private final int var_hilo;
    
    public Hilo(String nombre){
        super(nombre);
        indices = new ArrayList<>();
        indicesAux = new ArrayList<>();
        var_hilo = Integer.valueOf(nombre.substring(1));  
    }

    //devuelve el array con los indices de las bolas que moverá.
    public ArrayList<Integer> getIndices() {
        return indices;
    }
    //devuelve los indices de bolas con una sección ocupando su cuadrente.
    public ArrayList<Integer> getIndicesAux() {
        return indicesAux;
    }

    @Override 
    public void run(){
        //Variables locales
        boolean colision;
        int movXaux;
        int movYaux;
        Bola[] bolas = panel.getBolas();
        int[] movX = panel.getMovX();
        int[] movY = panel.getMovY();
        Bola[] b = panel.getBolas();
        Graphics[] g = panel.getgHilo();

        for (int i : indices){
            colision = false;
            //COMPRUEBO COLISIONES 
            //Se comprueba que en su próximo ciclo la bolas vaya a vaya a
            //superponerse con otra. En ese caso invierte sus vectores de 
            //movimientos y se mueve la bola que se está comprobando.
            //La otra bola no detectará la colisión al cambiar los vectores
            //pero se moverá con el nuevo movimiento grabado.
            for (int j=i+1; j<NUM_BOLAS; j++){
                int diffX = bolas[i].getX()+movX[i] - bolas[j].getX()-movX[j];
                int diffY = bolas[i].getY()+movY[i] - bolas[j].getY()-movY[j];
                int sumR = bolas[i].getR() + bolas[j].getR();
                if (sumR*sumR > diffX*diffX + diffY*diffY){
                    colision = true;
                    movXaux = movX[i]; 
                    movYaux = movY[i];
                    movX[i] = movX[j];
                    movY[i] = movY[j];
                    movX[j] = movXaux;
                    movY[j] = movYaux;
                    bolas[i].mover(movX[i],movY[i]);
                }
            }
            //AJUSTE Y COMPRUEBACION DE BORDES
            //Si la bola ha colisionado cerca de un borde y ha salido del área 
            //de dibujo del panel se ajusta para simular un rebote.
            if (colision){
                if (bolas[i].getX() + bolas[i].getR() > ANCHO)
                     bolas[i].mover(ANCHO - (bolas[i].getX() + bolas[i].getR()),0);   
                if (bolas[i].getX() - bolas[i].getR() < 0)
                     bolas[i].mover(-bolas[i].getX() + bolas[i].getR(),0); 
                if (bolas[i].getY() + bolas[i].getR() > ALTO)
                     bolas[i].mover(0, ALTO - (bolas[i].getY() + bolas[i].getR()));
                if (bolas[i].getY() - bolas[i].getR() < 0)
                    bolas[i].mover(0, -bolas[i].getY() + bolas[i].getR());
            //Si la bola no ha colisionado se procede a moverla normalmente
            //y se comprueban los bordes por si rebotase
            }
            else if (bolas[i].getX() + bolas[i].getR() + movX[i] > ANCHO){
                movX[i] = -movX[i];
                bolas[i].mover(movX[i]+ANCHO - (bolas[i].getX() + bolas[i].getR()),0); 
            }
            else if (bolas[i].getX() - bolas[i].getR() + movX[i] < 0){
                movX[i] = -movX[i];
                bolas[i].mover(movX[i]-bolas[i].getX() + bolas[i].getR(),0); 
            }
            else if (bolas[i].getY() + bolas[i].getR() + movY[i] > ALTO){
                movY[i] = -movY[i];   
                bolas[i].mover(0, movY[i]+ALTO - (bolas[i].getY() + bolas[i].getR()));
            }
            else if (bolas[i].getY() - bolas[i].getR() + movY[i] < 0){
                movY[i] = -movY[i];   
                bolas[i].mover(0, movY[i]-bolas[i].getY() + bolas[i].getR());
            }
            else
                bolas[i].mover(movX[i],movY[i]);
            }
        //Cada hilo crea un rectangulo del color de fondo para limpiar las
        //bolas pintadas en el ciclo anterior.
        g[var_hilo-1].setColor(panel.getBackground());
        g[var_hilo-1].fillRect(0,0,ANCHO/2,ALTO/2);
            
        //En funcion de la variable que indica el número del hilo se
        //define un color diferente para su respectivo cuadrante.
        switch (var_hilo){
            case 1:
                g[0].setColor(Color.red);
                break;
            case 2:
                g[1].setColor(Color.blue);
                break;
            case 3:
                g[2].setColor(Color.green);
                break;
            case 4:
                g[3].setColor(Color.yellow);
                break;
        }
     
        //Se llama al método pintar de cada bola perteneciente a su cuadrante.
        this.indices.forEach((i) -> {
            b[i].pintar(g[var_hilo-1]);
        });
        
        //Se llama al método pintar de cada bola de otro hilo
        //cuya superficie invade el cuadrante de este hilo.
        this.indicesAux.forEach((i) -> {
            b[i].pintar(g[var_hilo-1]);
        });
        
        //Liberamos el semáforo.
        panel.getS().release();
    }
}