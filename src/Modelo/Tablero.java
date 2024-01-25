/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.Serializable;

/**
 *
 * @author Pablo Alcudia
 */
public class Tablero implements Serializable{

    // Matriz de 3x4
    private String[][] tablero = {
        {"1000€", "", "", ""},
        {"", "Entradas Final Champions", "", "Viaje"},
        {"", "", "Play 5", ""}
    };

    public synchronized String[][] getTablero() {
        return tablero;
    }

    public void setTablero(String[][] tablero) {
        this.tablero = tablero;
    }

    public synchronized String obtenerPremio(int fila, int columna) throws InterruptedException {
        String premio;
        // Verificar que las coordenadas estén dentro del rango del tablero
        if (fila >= 0 && fila < getTablero().length && columna >= 0 && columna < getTablero()[fila].length) {
            premio = getTablero()[fila][columna];
        } else {
            premio = ""; // Fuera de las dimensiones del tablero
        }

        if (!premio.isEmpty()) {
            getTablero()[fila][columna] = "";
            return premio;
        } else {
            return premio;
        }
    }

}
