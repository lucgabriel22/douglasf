package br.com.unipe;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.*;

public class Grafo {
    private final List<Aresta> arestas;
    private final List<Vertice> vertices;
    private boolean eDirigido;
    private int ordem;
    private int tamanho;
    private final boolean ePonderado;

    public Grafo() {
        this(false, false);
    }

    public Grafo(boolean eDirigido, boolean ePonderado) {
        this.eDirigido = eDirigido;
        this.ePonderado = ePonderado;
        arestas = new ArrayList<>();
        vertices = new ArrayList<>();
    }

    public void adicionaVertices(String... nomes) {
        for (String nome : nomes) {
            vertices.add(new Vertice(nome));
            ordem++;
        }
    }

    public void addAresta(String v1, String v2) {
        arestas.add(criaAresta("", v1, v2, null));
    }

    public void addAresta(String v1, String v2, int peso) {
        arestas.add(criaAresta("", v1, v2, peso));
    }

    public void addAresta(String nome, String v1, String v2) {
        arestas.add(criaAresta(nome, v1, v2, null));
    }

    public void addAresta(String nome, String v1, String v2, int peso) {
        arestas.add(criaAresta(nome, v1, v2, peso));
    }

    private Aresta criaAresta(String nomeAresta, String nomeVertice1, String nomeVertice2, Integer peso) {
        Vertice v1 = encontraVertice(nomeVertice1).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice1 + " não encontrado."));
        Vertice v2 = encontraVertice(nomeVertice2).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + nomeVertice2 + " não encontrado."));
        if (!eDirigido) {
            infereSeGrafoEDirecionado(v1, v2);
        }
        aumentaGrauDosVertices(v1, v2);
        resolveAdjacencias(v1, v2);
        tamanho++;
        return new Aresta(nomeAresta, v1, v2, peso);
    }

    private void resolveAdjacencias(Vertice v1, Vertice v2) {
        v1.adicionaAdjacencia(v2); //v1 envia p v2
        v2.adicionaAdjacente(v1); // v2 recebe de v1
        if (!eDirigido) {
            v1.adicionaAdjacente(v2);
            v2.adicionaAdjacencia(v1);
        }
    }

    private void aumentaGrauDosVertices(Vertice v1, Vertice v2) {
        if (eDirigido) {
            v1.aumentaOutDegree();
            v2.aumentaInDegree();
        } else {
            v1.aumentaGrau();
            v2.aumentaGrau();
        }
    }

    private void infereSeGrafoEDirecionado(Vertice v1, Vertice v2) {
        if (eSelfLoop(v1, v2)) {
            reprocessamentoParaDigrafo();
        } else {
            for (Aresta aresta : arestas) {
                if (eViaMaoDupla(v1, v2, aresta) || eArestaDuplicada(v2, v1, aresta)) {
                    reprocessamentoParaDigrafo();
                    break;
                }
            }
        }
    }

    private static boolean eArestaDuplicada(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v1) && aresta.getVerticeDestino().equals(v2);
    }

    private static boolean eViaMaoDupla(Vertice v1, Vertice v2, Aresta aresta) {
        return aresta.getVerticeOrigem().equals(v2) && aresta.getVerticeDestino().equals(v1);
    }

    private static boolean eSelfLoop(Vertice v1, Vertice v2) {
        return v1.getNome().equals(v2.getNome());
    }

    public Optional<Vertice> encontraVertice(String nome) {
        for (Vertice vertice : vertices) {
            if (vertice.getNome().equalsIgnoreCase(nome)) {
                return Optional.of(vertice);
            }
        }
        return Optional.empty();
    }

    private void reprocessamentoParaDigrafo() {
        eDirigido = true;
        System.out.println("Reprocessamento para digrafo necessário. O grafo agora é direcionado.");
        limpezaGrausEAdjacencias();
        recalculaGrausEAdjacencias();
    }

    private void recalculaGrausEAdjacencias() {
        arestas.forEach(aresta -> {
            Vertice origem = aresta.getVerticeOrigem();
            Vertice destino = aresta.getVerticeDestino();
            aumentaGrauDosVertices(origem, destino);
            resolveAdjacencias(origem, destino);
        });
    }

    private void limpezaGrausEAdjacencias() {
        vertices.forEach(vertice -> {
            vertice.resetaGraus();
            vertice.resetaAdjacenciasEAdjacentes();
        });
    }

    public String exibeGrausDosVertices() {
        StringBuilder graus = new StringBuilder();
        for (Vertice vertice : vertices) {
            graus.append(vertice.exibeGraus());
        }
        return graus.toString();
    }

    public String exibeAdjacencias() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias
                    .append("\n")
                    .append(vertice.getNome())
                    .append(": ")
                    .append(vertice.getAdjacencias());
        }
        return adjacencias.toString();
    }

    public String exibeAdjacentes() {
        StringBuilder adjacencias = new StringBuilder();
        for (Vertice vertice : vertices) {
            adjacencias
                    .append("\n")
                    .append(vertice.getNome())
                    .append(": ")
                    .append(vertice.getAdjacentes());
        }
        return adjacencias.toString();
    }


    public void exibeMatrizAdjacencia() {
        List<Vertice> verticesOrdenados = vertices
                .stream()
                .sorted(Comparator.comparing(Vertice::getNome))
                .toList();

        StringBuilder matriz = new StringBuilder("\nMatriz de Adjacência\n");
        matriz.append("\t");
        verticesOrdenados.forEach(v -> matriz.append(v.getNome()).append("\t"));
        matriz.append("\n");

        for (Vertice vertice : verticesOrdenados) { //read-only
            matriz.append(vertice.getNome()).append("\t");
            List<Vertice> adjacencias = vertice.getAdjacencias();
            for (Vertice outroVertice : verticesOrdenados) {
                matriz.append(adjacencias.contains(outroVertice) ? "1" : "0").append("\t");
            }
            matriz.append("\n");
        }

        System.out.println(matriz);
    }

    public void exibeMatrizIncidencia() {
        List<Vertice> verticesOrdenados = vertices.stream().sorted(Comparator.comparing(Vertice::getNome)).toList();
        StringBuilder matriz = new StringBuilder("\nMatriz de Incidência\n\t");
        arestas.forEach(a -> matriz.append(a.getNome()).append("\t"));
        matriz.append("\n");
        for (Vertice vertice : verticesOrdenados) {
            matriz.append(vertice.getNome()).append("\t");
            for (Aresta aresta : arestas) {
                Vertice origem = aresta.getVerticeOrigem();
                Vertice destino = aresta.getVerticeDestino();
                String valor;
                if (origem.equals(vertice) && destino.equals(vertice)) {
                    valor = " 2";
                } else if (origem.equals(vertice)) {
                    valor = eDirigido ? "-1" : "1";
                } else if (destino.equals(vertice)) {
                    valor = " 1";
                } else { // caso contrário
                    valor = " 0";
                }
                matriz.append(valor).append("\t");
            }
            matriz.append("\n");
        }
        System.out.println(matriz);
    }


    public boolean haCaminhoSimples(String origem, String destino) {
        Vertice verticeOrigem = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        Vertice verticeDestino = encontraVertice(destino).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + destino + " não encontrado."));
        Set<Vertice> vistados = new HashSet<>();

        System.out.println("Nível 1");
        List<Vertice> nivel1 = verticeOrigem.getAdjacencias();
        for (Vertice vertice : nivel1) {
            System.out.printf("Visitando %s\n", vertice.getNome());
            vistados.add(vertice);
            System.out.println("Vistados: " + vistados);
            if (vertice.equals(verticeDestino)) {
                System.out.println("Caminho: " + verticeOrigem + " -> " + vertice);
                return true;
            }
        }

        System.out.println("Nível 2");
        for (Vertice vertice1 : nivel1) {
            List<Vertice> nivel2 = vertice1.getAdjacencias();
            for (Vertice vertice2 : nivel2) { //O(n²)
                System.out.printf("Visitando %s, adjacente de %s\n", vertice2.getNome(), vertice1.getNome());
                if (vistados.contains(vertice2)) {
                    System.out.println("Já visitado, pulando...");
                    continue;
                }
                vistados.add(vertice2);
                System.out.println("Vistados: " + vistados);
                if (vertice2.equals(verticeDestino)) {
                    System.out.println("Caminho: " + verticeOrigem + " -> " + vertice1 + " -> " + vertice2);
                    return true;
                }
            }
        }

        System.out.println("Nível 3");
        for (Vertice vertice1 : nivel1) {
            List<Vertice> nivel2 = vertice1.getAdjacencias();
            for (Vertice vertice2 : nivel2) {
                List<Vertice> nivel3 = vertice2.getAdjacencias();
                for (Vertice vertice3 : nivel3) { //O(n³)
                    System.out.printf("Visitando %s, adjacente de %s\n", vertice3.getNome(), vertice2.getNome());
                    if (vistados.contains(vertice3)) {
                        System.out.println("Já visitado, pulando...");
                        continue;
                    }
                    vistados.add(vertice3);
                    System.out.println("Vistados: " + vistados);
                    if (vertice3.equals(verticeDestino)) {
                        System.out.println(
                                "Caminho: " + verticeOrigem + " -> " + vertice1 + " -> " + vertice2 + " -> " + vertice3);
                        return true;
                    }
                }
            }
        }

        System.out.println("Nível 4");
        for (Vertice vertice1 : nivel1) {
            List<Vertice> nivel2 = vertice1.getAdjacencias();
            for (Vertice vertice2 : nivel2) {
                List<Vertice> nivel3 = vertice2.getAdjacencias();
                for (Vertice vertice3 : nivel3) {
                    List<Vertice> nivel4 = vertice3.getAdjacencias();
                    for (Vertice vertice4 : nivel4) { //O(n⁴)
                        System.out.printf("Visitando %s, adjacente de %s\n", vertice4.getNome(), vertice3.getNome());
                        if (vistados.contains(vertice4)) {
                            System.out.println("Já visitado, pulando...");
                            continue;
                        }
                        vistados.add(vertice4);
                        System.out.println("Vistados: " + vistados);
                        if (vertice4.equals(verticeDestino)) {
                            System.out.println(
                                    "Caminho: " + verticeOrigem + " -> " + vertice1 + " -> " + vertice2 + " -> " + vertice3 + " -> " + vertice4);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public List<String> dfsIterativo(String origem, String destino) {
        Vertice verticeOrigem = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        Vertice verticeDestino = destino == null ? null : encontraVertice(destino).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + destino + " não encontrado."));

        Stack<Vertice> pilha = new Stack<>();
        List<Vertice> visitados = new ArrayList<>();
        StringBuilder percurso = new StringBuilder("Percurso = ");

        visitados.add(verticeOrigem);
        pilha.push(verticeOrigem);

        percurso.append(verticeOrigem.getNome()).append(", ");

        while (!pilha.isEmpty()) {
            Vertice atual = pilha.peek();

            if (atual.equals(verticeDestino)) break;

            List<Vertice> adjacencias =  atual.getAdjacencias();
            List<Vertice> adjacenciasOrdenadas = adjacencias
                    .stream()
                    .sorted(Comparator.comparing(Vertice::getNome))
                    .toList();

            //Pegue a primeira adjacência não visitada
            Optional<Vertice> proximo = adjacenciasOrdenadas.stream()
                    .filter(a -> !visitados.contains(a))
                    .findFirst();

            if (proximo.isPresent()) {
                Vertice adjacencia = proximo.get();
                visitados.add(adjacencia);
                percurso.append(adjacencia.getNome()).append(", ");
                pilha.push(adjacencia);     // avança para o primeiro vizinho não visitado
            } else {
                pilha.pop();                   // vértice esgotado: remove da pilha
            }
        }

        System.out.println(percurso);
        return visitados.stream().map(Vertice::getNome).toList();
    }

    public List<String> dfsRecursivo(String origem, String destino, List<Vertice> visitados) {
        final List<Vertice> visitadosAtual = visitados != null ? visitados : new ArrayList<>();

        Vertice v = encontraVertice(origem).orElseThrow(
                () -> new IllegalArgumentException("Vertice " + origem + " não encontrado."));
        visitadosAtual.add(v);

        if (origem.equals(destino)) {
            return visitadosAtual.stream().map(Vertice::getNome).toList();
        }

        // itera os vizinhos um a um — após backtrack, os já visitados são pulados pelo contains()
        // espelhando o peek() + findFirst() do iterativo
        for (Vertice adj : v.getAdjacencias()) {
            if (visitadosAtual.contains(adj)) continue;

            dfsRecursivo(adj.getNome(), destino, visitadosAtual);

            // se destino foi encontrado em algum ramo, propaga o resultado
            if (destino != null && visitadosAtual.stream().anyMatch(x -> x.getNome().equals(destino))) {
                return visitadosAtual.stream().map(Vertice::getNome).toList();
            }
        }

        // vértice esgotado (sem vizinhos não visitados): retorna o percurso até aqui
        return visitadosAtual.stream().map(Vertice::getNome).toList();
    }


    public int encontraComprimentoCaminho(String... caminho) {
        if (!ePonderado) {
            return caminho.length - 1; //qtd de arestas percorridas
        }
        int comprimento = 0;
        List<Aresta> arestasPercorridas = new ArrayList<>();

        for (int i = 0;
             i < caminho.length - 1;
             i++) {
            int indiceAtual = i;
            Vertice origem = encontraVertice(caminho[indiceAtual])
                    .orElseThrow(
                            () -> new IllegalArgumentException("Vertice " + caminho[indiceAtual] + " não encontrado."));
            Vertice destino = encontraVertice(caminho[indiceAtual + 1])
                    .orElseThrow(
                            () -> new IllegalArgumentException(
                                    "Vertice " + caminho[indiceAtual + 1] + " não encontrado."));
            Optional<Aresta> aresta = arestas.stream()
                    .filter(a -> a.getVerticeOrigem().equals(origem)
                            && a.getVerticeDestino().equals(destino))
                    .findFirst();
            if (aresta.isPresent()) {
                if (arestasPercorridas.contains(aresta.get())) {
                    throw new IllegalArgumentException("Aresta repetida!");
                }
                arestasPercorridas.add(aresta.get());
                comprimento += aresta.get().getPeso();
            }
        }
        return comprimento;
    }

    public void desenhaGrafo() {
        int largura = 800, altura = 800;
        int raioLayout = 300;       // raio do círculo de posicionamento
        int raioVertice = 25;       // tamanho do círculo de cada vértice
        int cx = largura / 2, cy = altura / 2;

        // calcula posição de cada vértice distribuído em círculo
        Map<Vertice, Point> posicoes = new LinkedHashMap<>();
        List<Vertice> lista = new ArrayList<>(vertices);
        for (int i = 0; i < lista.size(); i++) {
            double angulo = 2 * Math.PI * i / lista.size() - Math.PI / 2;
            int x = (int) (cx + raioLayout * Math.cos(angulo));
            int y = (int) (cy + raioLayout * Math.sin(angulo));
            posicoes.put(lista.get(i), new Point(x, y));
        }

        JPanel painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(2));

                // desenha arestas
                for (Aresta aresta : arestas) {
                    Point origem = posicoes.get(aresta.getVerticeOrigem());
                    Point destino = posicoes.get(aresta.getVerticeDestino());
                    if (origem == null || destino == null) continue;

                    boolean selfLoop = aresta.getVerticeOrigem().equals(aresta.getVerticeDestino());
                    g2.setColor(Color.DARK_GRAY);

                    if (selfLoop) {
                        // self-loop: arco acima do vértice
                        g2.drawOval(origem.x - raioVertice, origem.y - raioVertice * 3, raioVertice * 2, raioVertice * 2);
                    } else {
                        // calcula pontos nas bordas dos círculos
                        double dx = destino.x - origem.x, dy = destino.y - origem.y;
                        double dist = Math.sqrt(dx * dx + dy * dy);
                        int ox = (int) (origem.x + raioVertice * dx / dist);
                        int oy = (int) (origem.y + raioVertice * dy / dist);
                        int dx2 = (int) (destino.x - raioVertice * dx / dist);
                        int dy2 = (int) (destino.y - raioVertice * dy / dist);

                        g2.drawLine(ox, oy, dx2, dy2);

                        if (eDirigido) desenhaSeta(g2, ox, oy, dx2, dy2);
                    }

                    // peso da aresta (se ponderado)
                    if (ePonderado && aresta.getPeso() != 0) {
                        int mx = (origem.x + destino.x) / 2;
                        int my = (origem.y + destino.y) / 2;
                        g2.setColor(Color.BLUE);
                        g2.drawString(String.valueOf(aresta.getPeso()), mx, my - 5);
                    }
                }

                // desenha vértices
                for (Map.Entry<Vertice, Point> entry : posicoes.entrySet()) {
                    Point p = entry.getValue();
                    g2.setColor(new Color(100, 180, 255));
                    g2.fillOval(p.x - raioVertice, p.y - raioVertice, raioVertice * 2, raioVertice * 2);
                    g2.setColor(Color.BLACK);
                    g2.drawOval(p.x - raioVertice, p.y - raioVertice, raioVertice * 2, raioVertice * 2);

                    // nome do vértice centralizado
                    FontMetrics fm = g2.getFontMetrics();
                    String nome = entry.getKey().getNome();
                    g2.drawString(nome, p.x - fm.stringWidth(nome) / 2, p.y + fm.getAscent() / 2 - 1);
                }
            }

            private void desenhaSeta(Graphics2D g2, int x1, int y1, int x2, int y2) {
                double angulo = Math.atan2(y2 - y1, x2 - x1);
                int tamanho = 12;
                AffineTransform tx = g2.getTransform();
                g2.translate(x2, y2);
                g2.rotate(angulo);
                g2.fillPolygon(new int[]{0, -tamanho, -tamanho}, new int[]{0, -tamanho / 2, tamanho / 2}, 3);
                g2.setTransform(tx);
            }
        };

        painel.setPreferredSize(new Dimension(largura, altura));
        painel.setBackground(Color.WHITE);

        JFrame frame = new JFrame("Grafo — " + (eDirigido ? "Dirigido" : "Não Dirigido") +
                (ePonderado ? " | Ponderado" : ""));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(painel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public String toString() {
        return """
                direcionado = %s,
                ordem = %d,
                tamanho = %d,
                vertices = %s,
                arestas = %s,
                graus = %s,
                adjacencias = %s,
                adjacentes = %s
                }""".formatted(
                eDirigido ? "sim" : "não",
                ordem,
                tamanho,
                vertices,
                arestas,
                exibeGrausDosVertices(),
                exibeAdjacencias(),
                exibeAdjacentes());
    }
}

