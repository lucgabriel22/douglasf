package br.com.unipe;

import java.util.List;
import java.util.Map;

public class TesteLinkedInAnalyzer {
    public static void main(String[] args) {
        Grafo rede = new Grafo(false, true); // não-direcionado e ponderado

        rede.adicionaVertices("Mariana", "Pedro", "Rafael", "Camila", "Diego", "Beatriz",
                "Felipe", "Renata", "Thiago", "Patricia");

        rede.addAresta("Mariana", "Pedro", 1);
        rede.addAresta("Mariana", "Rafael", 2);
        rede.addAresta("Mariana", "Camila", 8);
        rede.addAresta("Pedro", "Diego", 1);
        rede.addAresta("Rafael", "Diego", 1);
        rede.addAresta("Camila", "Beatriz", 5);
        rede.addAresta("Diego", "Beatriz", 1);
        rede.addAresta("Felipe", "Renata", 1);
        rede.addAresta("Thiago", "Patricia", 1);

        LinkedInAnalyzer analisador = new LinkedInAnalyzer(rede);

        System.out.println("Sugestões de vínculo para Rafael:");
        for (Map.Entry<String, Integer> sugestao : analisador.recomendarContatos("Rafael")) {
            System.out.println("  " + sugestao.getKey() + " (" + sugestao.getValue() + " em comum)");
        }

        int saltos = analisador.distanciaEmSaltos("Mariana", "Beatriz");
        System.out.println("Grau de separação Mariana -> Beatriz: " + saltos);

        Grafo.ResultadoCaminho resultado = analisador.melhorRotaAfinidade("Mariana", "Beatriz");
        System.out.println("Rota de maior afinidade Mariana -> Beatriz: " + resultado.caminho()
                + " | custo: " + resultado.custo());

        List<List<String>> subredes = analisador.encontrarSubredes();
        System.out.println("Grupos isolados: " + subredes);
    }
}