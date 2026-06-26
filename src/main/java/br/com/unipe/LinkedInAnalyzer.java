package br.com.unipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cérebro das análises sobre a rede social de conexões profissionais.
 * Usa a instância de Grafo já existente (Vertice/Aresta) para resolver
 * as missões do projeto.
 */
public class LinkedInAnalyzer {

    private final Grafo rede;

    public LinkedInAnalyzer(Grafo rede) {
        this.rede = rede;
    }

    /**
     * Missão 2 — Sugestão de vínculos (amigos de 2º grau).
     * Retorna pares (nome, qtd. de amigos em comum), ordenados decrescente.
     */
    public List<Map.Entry<String, Integer>> recomendarContatos(String nomePessoa) {
        Vertice pessoa = encontraVerticeOuFalha(nomePessoa);
        List<Vertice> primeiroGrau = pessoa.getAdjacencias();

        Map<String, Integer> ocorrencias = new HashMap<>();
        for (Vertice contato : primeiroGrau) {
            for (Vertice candidato : contato.getAdjacencias()) {
                if (candidato.equals(pessoa) || primeiroGrau.contains(candidato)) {
                    continue;
                }
                ocorrencias.merge(candidato.getNome(), 1, Integer::sum);
            }
        }

        return ocorrencias.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());
    }

    /**
     * Missão 3 — Grau de separação (BFS, ignora pesos, conta só "saltos").
     * Retorna -1 se não houver caminho entre origem e destino.
     */
    public int distanciaEmSaltos(String nomeOrigem, String nomeDestino) {
        if (nomeOrigem.equalsIgnoreCase(nomeDestino)) {
            return 0;
        }

        Vertice origem = encontraVerticeOuFalha(nomeOrigem);
        Vertice destino = encontraVerticeOuFalha(nomeDestino);

        Map<Vertice, Integer> distancias = new HashMap<>();
        distancias.put(origem, 0);
        Queue<Vertice> filaBusca = new LinkedList<>();
        filaBusca.add(origem);

        while (!filaBusca.isEmpty()) {
            Vertice atual = filaBusca.poll();
            for (Vertice vizinho : atual.getAdjacencias()) {
                if (vizinho.equals(destino)) {
                    return distancias.get(atual) + 1;
                }
                if (!distancias.containsKey(vizinho)) {
                    distancias.put(vizinho, distancias.get(atual) + 1);
                    filaBusca.add(vizinho);
                }
            }
        }

        return -1;
    }

    /**
     * Missão 4 — Rota e custo de maior afinidade (Dijkstra, implementado em Grafo).
     */
    public Grafo.ResultadoCaminho melhorRotaAfinidade(String nomeOrigem, String nomeDestino) {
        return rede.dijkstra(nomeOrigem, nomeDestino);
    }

    /**
     * Missão 5 — Mapear grupos isolados (componentes conexos via BFS).
     */
    public List<List<String>> encontrarSubredes() {
        List<List<String>> subredes = new ArrayList<>();
        Set<Vertice> jaVisitados = new HashSet<>();

        for (Vertice vertice : rede.getVertices()) {
            if (jaVisitados.contains(vertice)) {
                continue;
            }

            List<String> subredeAtual = new ArrayList<>();
            Queue<Vertice> filaBusca = new LinkedList<>();
            filaBusca.add(vertice);
            jaVisitados.add(vertice);

            while (!filaBusca.isEmpty()) {
                Vertice atual = filaBusca.poll();
                subredeAtual.add(atual.getNome());
                for (Vertice vizinho : atual.getAdjacencias()) {
                    if (jaVisitados.add(vizinho)) {
                        filaBusca.add(vizinho);
                    }
                }
            }

            subredes.add(subredeAtual);
        }

        return subredes;
    }

    private Vertice encontraVerticeOuFalha(String nome) {
        return rede.encontraVertice(nome).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nome + " não encontrado."));
    }
}