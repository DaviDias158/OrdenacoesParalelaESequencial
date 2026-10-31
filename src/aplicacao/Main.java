package aplicacao;

import testes.ConfiguracaoTeste;
import testes.ExecutadorTestes;
import testes.ResultadoTeste;

import dados.GeradorDados.TipoDados;

import exportacao.ExportadorCSV;
import visualizacao.GraficoResultados;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        int processadoresDisponiveis =
                Runtime.getRuntime().availableProcessors();

        System.out.println("   PROJETO DE ORDENACAO PARALELA");

        System.out.println(
                "Processadores disponíveis para a JVM: "
                        + processadoresDisponiveis
        );

        int[] quantidadesThreads =
                determinarQuantidadesThreads(processadoresDisponiveis);

        ConfiguracaoTeste configuracao = new ConfiguracaoTeste(

                new int[]{
                        250,
                        500,
                        1000
                },

                new int[]{
                        10000,
                        50000,
                        100000
                },

                new int[]{
                        10000,
                        50000,
                        100000
                },

                new int[]{
                        1000,
                        2000,
                        4000
                },

                TipoDados.values(),

                quantidadesThreads,

                5,

                1,

                20261030L
        );

        System.out.println("\nConfiguração dos experimentos:");

        System.out.println("Bubble Sort: 250, 500, 1000");
        System.out.println("Quick Sort: 10000, 50000, 100000");
        System.out.println("Merge Sort: 10000, 50000, 100000");
        System.out.println("Insertion Sort: 1000, 2000, 4000");

        System.out.println(
                "Quantidade de amostras: "
                        + configuracao.getQuantidadeAmostras()
        );

        System.out.println(
                "Aquecimentos: "
                        + configuracao.getQuantidadeAquecimento()
        );

        System.out.print("Threads utilizadas: ");

        for (int thread : quantidadesThreads) {
            System.out.print(thread + " ");
        }

        System.out.println("\n");

        try {

            ExecutadorTestes executador =
                    new ExecutadorTestes(configuracao);

            List<ResultadoTeste> resultados =
                    executador.executar();

            Path caminhoCSV = Paths.get(
                    "resultados",
                    "resultados_testes.csv"
            );

            ExportadorCSV.exportar(
                    resultados,
                    caminhoCSV.toString()
            );

            System.out.println("\nEXPERIMENTOS FINALIZADOS");

            System.out.println(
                    "Total de execuções registradas: "
                            + resultados.size()
            );

            System.out.println(
                    "Arquivo: " + caminhoCSV.toAbsolutePath()
            );

            // Abre a interface gráfica após a exportação.
            SwingUtilities.invokeLater(() -> {

                try {

                    GraficoResultados janela =
                            new GraficoResultados(caminhoCSV);

                    janela.setVisible(true);

                } catch (IOException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Não foi possível abrir os gráficos:\n"
                                    + erro.getMessage(),
                            "Erro de visualização",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

        } catch (IOException erro) {

            System.err.println(
                    "\nErro ao exportar os resultados: "
                            + erro.getMessage()
            );

            erro.printStackTrace();
        }
    }

    private static int[] determinarQuantidadesThreads(
            int processadoresDisponiveis
    ) {

        if (processadoresDisponiveis >= 8) {
            return new int[]{1, 2, 4, 8};
        }

        if (processadoresDisponiveis >= 4) {
            return new int[]{1, 2, 4};
        }

        if (processadoresDisponiveis >= 2) {
            return new int[]{1, 2};
        }

        return new int[]{2};
    }
}
