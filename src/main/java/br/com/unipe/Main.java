package br.com.unipe;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Grafo grafo = new Grafo(true, true);

        grafo.adicionaVertices("0", "1", "2", "3", "4", "5", "6", "7");

        grafo.addAresta("5", "1", 2);
        grafo.addAresta("1", "3", 3);
        grafo.addAresta("3", "6", 4);
        grafo.addAresta("6", "4", 5);
        grafo.addAresta("4", "5", 2);
        grafo.addAresta("5", "4", 1);
        grafo.addAresta("4", "7", 2);
        grafo.addAresta("7", "3", 3);
        grafo.addAresta("2", "7", 2);
        grafo.addAresta("6", "2", 1);
        grafo.addAresta("0", "2", 4);
        grafo.addAresta("0", "4", 3);
        grafo.addAresta("6", "0", 2);
        grafo.addAresta("5", "7", 1);
        grafo.addAresta("7", "5", 2);

        System.out.println(grafo);
        grafo.exibeMatrizAdjacencia();
        grafo.exibeMatrizIncidencia();

        //System.out.println("\nHa caminho simples 5-> 0? " + grafo.haCaminhoSimples("5", "0"));
        //System.out.println("\nComprimento do caminho 5->0: " + grafo.encontraComprimentoCaminho("5", "1", "3", "6","0"));
        System.out.println(grafo.dfsIterativo("5", null));
        System.out.println(grafo.dfsRecursivo("5", null, null));

        grafo.desenhaGrafo();
    }
}