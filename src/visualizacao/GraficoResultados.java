package visualizacao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.geom.AffineTransform;

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class GraficoResultados extends JFrame {
    private final List<Registro> registros;

    private final JComboBox<String> comboAlgoritmo =
            new JComboBox<>();

    private final JComboBox<String> comboTipo =
            new JComboBox<>();

    private final JComboBox<Integer> comboTamanho =
            new JComboBox<>();

    private final JLabel resumo = new JLabel();

    private final PainelGrafico painelGrafico =
            new PainelGrafico();

    private boolean atualizandoFiltros = false;

    public GraficoResultados(Path caminhoArquivo) throws IOException {

        super("Análise de Desempenho - Algoritmos de Ordenação");

        registros = carregarResultados(caminhoArquivo);

        if (registros.isEmpty()) {
            throw new IOException(
                    "O arquivo CSV não possui resultados."
            );
        }

        inicializarInterface();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);
    }

    private void inicializarInterface() {

        setLayout(new BorderLayout(10, 10));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 15));
        painelPrincipal.setBorder(
                new EmptyBorder(15, 20, 15, 20)
        );

        // Título.
        JLabel titulo = new JLabel(
                "ANÁLISE DE DESEMPENHO DOS ALGORITMOS"
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 21));

        // Painel de filtros.
        JPanel painelFiltros = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 15, 10)
        );

        painelFiltros.setBorder(
                BorderFactory.createTitledBorder(
                        "Configurações de visualização"
                )
        );

        preencherAlgoritmos();
        preencherTipos();
        preencherTamanhos();

        painelFiltros.add(criarFiltro(
                "Algoritmo:",
                comboAlgoritmo
        ));

        painelFiltros.add(criarFiltro(
                "Tipo de dados:",
                comboTipo
        ));

        painelFiltros.add(criarFiltro(
                "Tamanho do vetor:",
                comboTamanho
        ));

        comboAlgoritmo.addActionListener(e -> {

            if (!atualizandoFiltros) {

                atualizandoFiltros = true;

                preencherTipos();
                preencherTamanhos();

                atualizandoFiltros = false;

                atualizarGrafico();
            }
        });

        comboTipo.addActionListener(e -> {

            if (!atualizandoFiltros) {

                atualizandoFiltros = true;

                preencherTamanhos();

                atualizandoFiltros = false;

                atualizarGrafico();
            }
        });

        comboTamanho.addActionListener(e -> {

            if (!atualizandoFiltros) {
                atualizarGrafico();
            }
        });

        // Painel com o título e os filtros.
        JPanel painelSuperior = new JPanel(new BorderLayout(5, 10));

        painelSuperior.add(titulo, BorderLayout.NORTH);
        painelSuperior.add(painelFiltros, BorderLayout.CENTER);

        // Painel de resumo estatístico.
        resumo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                "Indicadores de desempenho"
                        ),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        resumo.setFont(new Font("Arial", Font.PLAIN, 13));

        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
        painelPrincipal.add(painelGrafico, BorderLayout.CENTER);
        painelPrincipal.add(resumo, BorderLayout.SOUTH);

        add(painelPrincipal);

        atualizarGrafico();
    }

    private JPanel criarFiltro(
            String titulo,
            JComboBox<?> combo
    ) {

        JPanel painel = new JPanel(new BorderLayout(3, 5));

        JLabel rotulo = new JLabel(titulo);

        rotulo.setFont(
                new Font("Arial", Font.BOLD, 12)
        );

        combo.setPreferredSize(new Dimension(155, 30));

        painel.add(rotulo, BorderLayout.NORTH);
        painel.add(combo, BorderLayout.CENTER);

        return painel;
    }

    private void preencherAlgoritmos() {

        Set<String> algoritmos = new TreeSet<>();

        for (Registro registro : registros) {
            algoritmos.add(registro.getFamilia());
        }

        comboAlgoritmo.setModel(
                new DefaultComboBoxModel<>(
                        algoritmos.toArray(new String[0])
                )
        );
    }

    private void preencherTipos() {

        String algoritmo =
                (String) comboAlgoritmo.getSelectedItem();

        Set<String> tipos = new TreeSet<>();

        for (Registro registro : registros) {

            if (registro.getFamilia().equals(algoritmo)) {
                tipos.add(registro.tipoDados);
            }
        }

        comboTipo.setModel(
                new DefaultComboBoxModel<>(
                        tipos.toArray(new String[0])
                )
        );
    }

    private void preencherTamanhos() {

        String algoritmo =
                (String) comboAlgoritmo.getSelectedItem();

        String tipo =
                (String) comboTipo.getSelectedItem();

        Set<Integer> tamanhos = new TreeSet<>();

        for (Registro registro : registros) {

            if (registro.getFamilia().equals(algoritmo)
                    && registro.tipoDados.equals(tipo)) {

                tamanhos.add(registro.tamanhoVetor);
            }
        }

        comboTamanho.setModel(
                new DefaultComboBoxModel<>(
                        tamanhos.toArray(new Integer[0])
                )
        );
    }

    private void atualizarGrafico() {

        String algoritmo =
                (String) comboAlgoritmo.getSelectedItem();

        String tipo =
                (String) comboTipo.getSelectedItem();

        Integer tamanho =
                (Integer) comboTamanho.getSelectedItem();

        if (algoritmo == null || tipo == null || tamanho == null) {
            return;
        }

        double tempoSequencial = calcularMediana(
                algoritmo,
                tipo,
                tamanho,
                "SEQUENCIAL",
                1
        );

        List<Barra> barras = new ArrayList<>();

        List<Integer> quantidadesThreads = new ArrayList<>();

        barras.add(new Barra(
                "Sequencial",
                tempoSequencial
        ));

        Set<Integer> threads = new TreeSet<>();

        for (Registro registro : registros) {

            if (registro.getFamilia().equals(algoritmo)
                    && registro.tipoDados.equals(tipo)
                    && registro.tamanhoVetor == tamanho
                    && registro.modoExecucao.equals("PARALELO")) {

                threads.add(registro.quantidadeThreads);
            }
        }

        StringBuilder indicadores = new StringBuilder();

        indicadores.append("<html>");

        if (tempoSequencial >= 0) {

            indicadores.append(
                    "<b>Mediana sequencial:</b> "
                            + formatarTempo(tempoSequencial)
                            + "<br><br>"
            );

        } else {

            indicadores.append(
                    "Não foi encontrada uma medição sequencial.<br><br>"
            );
        }

        for (int quantidadeThreads : threads) {

            double tempoParalelo = calcularMediana(
                    algoritmo,
                    tipo,
                    tamanho,
                    "PARALELO",
                    quantidadeThreads
            );

            if (tempoParalelo < 0) {
                continue;
            }

            barras.add(new Barra(
                    quantidadeThreads == 1
                            ? "Paralelo 1 thread"
                            : "Paralelo " + quantidadeThreads + " threads",
                    tempoParalelo
            ));

            quantidadesThreads.add(quantidadeThreads);

            if (tempoSequencial > 0) {

                double speedup = tempoSequencial / tempoParalelo;

                double eficiencia =
                        speedup / quantidadeThreads * 100.0;

                indicadores.append(
                        "<b>" + quantidadeThreads + " threads:</b> "
                                + "Speedup: "
                                + String.format(Locale.US, "%.2fx", speedup)
                                + " | Eficiência: "
                                + String.format(Locale.US, "%.2f%%", eficiencia)
                                + "<br>"
                );
            }
        }

        indicadores.append("</html>");

        resumo.setText(indicadores.toString());

        painelGrafico.setDados(
                barras,
                algoritmo + " - " + tipo
                        + " - " + tamanho + " elementos"
        );

        painelGrafico.repaint();
    }

    private double calcularMediana(
            String algoritmo,
            String tipo,
            int tamanho,
            String modo,
            int threads
    ) {

        List<Long> tempos = new ArrayList<>();

        for (Registro registro : registros) {

            if (registro.getFamilia().equals(algoritmo)
                    && registro.tipoDados.equals(tipo)
                    && registro.tamanhoVetor == tamanho
                    && registro.modoExecucao.equals(modo)
                    && registro.quantidadeThreads == threads) {

                tempos.add(registro.tempoNanos);
            }
        }

        if (tempos.isEmpty()) {
            return -1;
        }

        Collections.sort(tempos);

        int meio = tempos.size() / 2;

        double mediana;

        if (tempos.size() % 2 == 0) {

            mediana = (
                    tempos.get(meio - 1).doubleValue()
                            + tempos.get(meio).doubleValue()
            ) / 2.0;

        } else {

            mediana = tempos.get(meio).doubleValue();
        }

        // Converte nanossegundos para milissegundos.
        return mediana / 1_000_000.0;
    }

    private String formatarTempo(double tempo) {

        return String.format(
                Locale.US,
                "%.4f ms",
                tempo
        );
    }

    private List<Registro> carregarResultados(
            Path caminhoArquivo
    ) throws IOException {

        List<Registro> lista = new ArrayList<>();

        try (BufferedReader leitor = Files.newBufferedReader(
                caminhoArquivo,
                StandardCharsets.UTF_8
        )) {

            String cabecalho = leitor.readLine();

            if (cabecalho == null) {
                throw new IOException("O CSV está vazio.");
            }

            String linha;
            int numeroLinha = 1;

            while ((linha = leitor.readLine()) != null) {

                numeroLinha++;

                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split(";", -1);

                if (campos.length < 9) {

                    throw new IOException(
                            "CSV inválido na linha " + numeroLinha
                    );
                }

                try {

                    Registro registro = new Registro();

                    registro.algoritmo = campos[0];
                    registro.modoExecucao = campos[1];
                    registro.tipoDados = campos[2];

                    registro.tamanhoVetor =
                            Integer.parseInt(campos[3]);

                    registro.quantidadeThreads =
                            Integer.parseInt(campos[4]);

                    registro.numeroAmostra =
                            Integer.parseInt(campos[5]);

                    registro.tempoNanos =
                            Long.parseLong(campos[6]);

                    lista.add(registro);

                } catch (NumberFormatException erro) {

                    throw new IOException(
                            "Valor inválido na linha " + numeroLinha,
                            erro
                    );
                }
            }
        }

        return lista;
    }

    private static class Registro {

        private String algoritmo;
        private String modoExecucao;
        private String tipoDados;

        private int tamanhoVetor;
        private int quantidadeThreads;
        private int numeroAmostra;

        private long tempoNanos;

        public String getFamilia() {

            return algoritmo.replaceFirst(
                    "\\s+Paralelo$",
                    ""
            ).trim();
        }
    }

    private static class Barra {

        private final String nome;
        private final double valor;

        Barra(String nome, double valor) {
            this.nome = nome;
            this.valor = valor;
        }
    }

    private static class PainelGrafico extends JPanel {

        private List<Barra> barras = new ArrayList<>();

        private String titulo = "";

        private final Color[] cores = {
                new Color(52, 120, 190),
                new Color(65, 160, 110),
                new Color(220, 155, 55),
                new Color(160, 100, 190),
                new Color(205, 90, 90)
        };

        PainelGrafico() {

            setBackground(Color.WHITE);

            setBorder(
                    BorderFactory.createTitledBorder(
                            "Comparação dos tempos de execução"
                    )
            );

            setPreferredSize(new Dimension(850, 430));
        }

        void setDados(List<Barra> barras, String titulo) {

            this.barras = new ArrayList<>(barras);
            this.titulo = titulo;
        }

        @Override
        protected void paintComponent(Graphics grafico) {

            super.paintComponent(grafico);

            Graphics2D g = (Graphics2D) grafico.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (barras.isEmpty()) {

                g.drawString(
                        "Nenhum resultado disponível.",
                        30,
                        50
                );

                g.dispose();
                return;
            }

            int margemEsquerda = 85;
            int margemDireita = 35;
            int margemSuperior = 60;
            int margemInferior = 100;

            int largura = getWidth()
                    - margemEsquerda
                    - margemDireita;

            int altura = getHeight()
                    - margemSuperior
                    - margemInferior;

            g.setFont(new Font("Arial", Font.BOLD, 14));

            FontMetrics metricas = g.getFontMetrics();

            g.drawString(
                    titulo,
                    (getWidth() - metricas.stringWidth(titulo)) / 2,
                    30
            );

            double maiorValor = 0;

            for (Barra barra : barras) {
                maiorValor = Math.max(maiorValor, barra.valor);
            }

            if (maiorValor <= 0) {
                maiorValor = 1;
            }

            maiorValor *= 1.20;

            // Linhas horizontais e valores do eixo Y.
            g.setFont(new Font("Arial", Font.PLAIN, 11));

            for (int i = 0; i <= 4; i++) {

                int y = margemSuperior
                        + altura
                        - (altura * i / 4);

                double valor = maiorValor * i / 4.0;

                g.setColor(new Color(220, 220, 220));

                g.drawLine(
                        margemEsquerda,
                        y,
                        margemEsquerda + largura,
                        y
                );

                g.setColor(Color.DARK_GRAY);

                String texto = String.format(
                        Locale.US,
                        "%.2f",
                        valor
                );

                g.drawString(
                        texto,
                        margemEsquerda - 55,
                        y + 4
                );
            }

            // Eixos.
            g.setColor(Color.DARK_GRAY);

            g.drawLine(
                    margemEsquerda,
                    margemSuperior,
                    margemEsquerda,
                    margemSuperior + altura
            );

            g.drawLine(
                    margemEsquerda,
                    margemSuperior + altura,
                    margemEsquerda + largura,
                    margemSuperior + altura
            );

            // Legenda do eixo Y.
            AffineTransform transformacaoOriginal =
                    g.getTransform();

            g.rotate(-Math.PI / 2);

            g.drawString(
                    "Tempo de execução (ms)",
                    -(margemSuperior + altura / 2 + 65),
                    20
            );

            g.setTransform(transformacaoOriginal);

            // Barras.
            int quantidade = barras.size();

            int espacoPorBarra = largura / quantidade;

            int larguraBarra = Math.min(
                    90,
                    Math.max(20, espacoPorBarra / 2)
            );

            for (int i = 0; i < quantidade; i++) {

                Barra barra = barras.get(i);

                int alturaBarra = (int) (
                        barra.valor / maiorValor * altura
                );

                int x = margemEsquerda
                        + i * espacoPorBarra
                        + (espacoPorBarra - larguraBarra) / 2;

                int y = margemSuperior
                        + altura
                        - alturaBarra;

                g.setColor(cores[i % cores.length]);

                g.fillRoundRect(
                        x,
                        y,
                        larguraBarra,
                        alturaBarra,
                        8,
                        8
                );

                g.setColor(Color.DARK_GRAY);

                String valor = String.format(
                        Locale.US,
                        "%.3f",
                        barra.valor
                );

                int larguraTexto =
                        g.getFontMetrics().stringWidth(valor);

                g.drawString(
                        valor,
                        x + (larguraBarra - larguraTexto) / 2,
                        Math.max(margemSuperior - 5, y - 8)
                );

                // Nome da configuração.
                g.setFont(new Font("Arial", Font.PLAIN, 10));

                String[] linhas = barra.nome.split(" ");

                int yTexto = margemSuperior + altura + 22;

                for (String palavra : linhas) {

                    int larguraPalavra =
                            g.getFontMetrics().stringWidth(palavra);

                    g.drawString(
                            palavra,
                            x + (larguraBarra - larguraPalavra) / 2,
                            yTexto
                    );

                    yTexto += 13;
                }
            }

            g.dispose();
        }
    }
}
