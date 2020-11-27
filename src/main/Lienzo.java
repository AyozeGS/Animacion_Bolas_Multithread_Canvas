/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.Main.*;

/**
 *
 * @author Ayoze Gil
 */
public class Lienzo extends Canvas implements Runnable{
    
    private final Bola[] bolas;
    private final int[] movX;
    private final int[] movY;
    
    private Image dibujoAux = createImage(ANCHO,ALTO);
    private final Image[] dibujoHilo = new Image[4];
    private Graphics gAux;
    private final Graphics[] gHilo = new Graphics[4];
    private Semaphore s = new Semaphore(0);

    //Constructor
    Lienzo(){
        bolas = new Bola[NUM_BOLAS];
        movX = new int[NUM_BOLAS];
        movY = new int[NUM_BOLAS];

        //Se generan X bolas comprobando en cada iteración no sobreponer
        //la nueva bola con las anteriores y regenerándola si se da ese caso.
        boolean colision = false;
        for (int i=0;i<NUM_BOLAS;i++){
            do{
                bolas[i] = new Bola(); 
                colision = false;
                for (int j=0; j<i; j++){
                    int diffX = bolas[i].getX() - bolas[j].getX();
                    int diffY = bolas[i].getY() - bolas[j].getY();
                    int sumR = bolas[i].getR() + bolas[j].getR();
                    if (sumR*sumR > (diffX*diffX + diffY*diffY)){
                        colision = true;
                    }
                } 
            }while (colision);
            do{
                movX[i] = (int) (Math.random()*2*MAX_V-MAX_V);
                movY[i] = (int) (Math.random()*2*MAX_V-MAX_V);     
            }while (movX[i] == 0 && movY[i] == 0);
        }
    }

    public Bola[] getBolas() {
        return bolas;
    }

    public int[] getMovX() {
        return movX;
    }

    public int[] getMovY() {
        return movY;
    }

    public Graphics[] getgHilo() {
        return gHilo;
    }

    public Semaphore getS() {
        return s;
    }

    //Método paint
    @Override
    public void paint(Graphics g){
            g.drawImage(dibujoAux, 0, 0, this);
    }

    //El método update de la clase Canvas se sobrescribe para que cree 4 hilos 
    //que se repartan los cálculos de las bolas y su representación gráfica
    @Override
    public void update(Graphics g){
        Hilo h1 = new Hilo("H1");
        Hilo h2 = new Hilo("H2");
        Hilo h3 = new Hilo("H3");
        Hilo h4 = new Hilo("H4");

        //Creamos un imagen para dibujar en ella antes de pintar el panel y 4
        //subimágenes para dibujar cada cuadrante de forma independiente. Para
        // todos ellas guardamos su atributo Graphics en sendas variables.
        if (gAux == null){
            dibujoAux = createImage(ANCHO,ALTO);
            gAux = this.dibujoAux.getGraphics();
        }
        for (int i = 0; i< gHilo.length; i++){
            if (gHilo[i] == null){
                dibujoHilo[i] = createImage(ANCHO/2,ALTO/2);
                gHilo[i] = dibujoHilo[i].getGraphics();
            }
        }
        //Cada hilo almacena los índices de las bolas asociadas a su cuadrante
        for (int i = 0; i < bolas.length; i++){
            if (bolas[i].getX() <= ANCHO/2){
                if (bolas[i].getY() <= ALTO/2)
                    h1.getIndices().add(i); //Cuadrante 1
                else
                    h3.getIndices().add(i); //Cuadrante 3 
            }else{
                if (bolas[i].getY() <= ALTO/2)
                    h2.getIndices().add(i); //Cuadrante 2
                else
                    h4.getIndices().add(i); //Cuadrante 4 
            }
            //Adicionalmente cada hilo almacena los índices de las bolas
            //de los otros cuadrantes cuya área invade el suyo.
            if (bolas[i].getX() - bolas[i].getR() <= ANCHO/2 
                    && bolas[i].getY() - bolas[i].getR() <= ALTO/2)
                h1.getIndicesAux().add(i); //Cuadrante 1
            if ((bolas[i].getX() + bolas[i].getR() > ANCHO/2 
                    && bolas[i].getY() - bolas[i].getR() <= ALTO/2))
                h2.getIndicesAux().add(i); //Cuadrante 2
            if ((bolas[i].getX() - bolas[i].getR() <= ANCHO/2 
                    && bolas[i].getY() + bolas[i].getR() > ALTO/2))
                h3.getIndicesAux().add(i); //Cuadrante 3
            if ((bolas[i].getX() + bolas[i].getR() > ANCHO/2 
                    && bolas[i].getY() + bolas[i].getR() > ALTO/2))
                h4.getIndicesAux().add(i); //Cuadrante 4
        }
        //Arrancamos los hilos
        h1.start();
        h2.start();
        h3.start();
        h4.start();
        //El semáforo no permite continuar hasta que los 4 hilos terminen
        //de pintar sus respectivas imágenes.
        try {
            this.getS().acquire(4);
        } catch (InterruptedException ex) {
            Logger.getLogger(Lienzo.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Unimos las 4 imágenes en una sólo que usamos como Buffer y añadimos
        //las líneas que separan los cuadrantes visualmente.
        gAux.drawImage(dibujoHilo[0], 0, 0, this);
        gAux.drawImage(dibujoHilo[1], ANCHO/2, 0, this);
        gAux.drawImage(dibujoHilo[2], 0, ALTO/2, this);
        gAux.drawImage(dibujoHilo[3], ANCHO/2, ALTO/2, this);
        gAux.drawLine(0, ALTO/2, ANCHO, ALTO/2);
        gAux.drawLine(ANCHO/2, 0, ANCHO/2, ALTO);
        //Llamamos al método pintar para que pinte el panel.
        paint(g);
    }
    
    //Se Sobreescribe el método run de la interfaz Runnable para que llame
    //continuamente al método repaint() y realice pausas de los milisegundos 
    //indicados por parámetro.
    @Override
    public void run() {
        do{
            try {
                Thread.sleep(20);
                //el método repaint de la clase Canvas llama al método update.
                repaint();
            } catch (InterruptedException ex) {
                Logger.getLogger(Lienzo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(true);
    }
}
