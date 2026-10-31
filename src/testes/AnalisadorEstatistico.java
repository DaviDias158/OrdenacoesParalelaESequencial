package testes;

import java.util.Arrays;
import java.util.List;

public class AnalisadorEstatistico {
    public static Estatisticas analisar(List<ResultadoTeste> resultados) {
        if (resultados == null || resultados.isEmpty()) {
            throw new IllegalArgumentException(
                    "A lista de resultados não pode estar vazia."
            );
        }

        long[] tempos = new long[resultados.size()];

        long menorTempo = Long.MAX_VALUE;
        long maiorTempo = Long.MIN_VALUE;
        double soma = 0;

        for (int i = 0; i < resultados.size(); i++) {
            long tempo = resultados.get(i).getTempoNanos();

            tempos[i] = tempo;
            soma += tempo;

            if (tempo < menorTempo) {
                menorTempo = tempo;
            }

            if (tempo > maiorTempo) {
                maiorTempo = tempo;
            }
        }

        double media = soma / tempos.length;

        Arrays.sort(tempos);

        double mediana;

        if (tempos.length % 2 == 0) {
            mediana = (
                    (double) tempos[tempos.length / 2 - 1]
                            + tempos[tempos.length / 2]
            ) / 2.0;

        } else {
            mediana = tempos[tempos.length / 2];
        }

        double somaQuadrados = 0;

        for (long tempo : tempos) {
            double diferenca = tempo - media;
            somaQuadrados += diferenca * diferenca;
        }

        double desvioPadrao = tempos.length > 1
                ? Math.sqrt(somaQuadrados / (tempos.length - 1))
                : 0;

        return new Estatisticas(
                media,
                mediana,
                desvioPadrao,
                menorTempo,
                maiorTempo
        );
    }

    public static double calcularSpeedup(
            List<ResultadoTeste> sequenciais,
            List<ResultadoTeste> paralelos
    ) {
        validarComparacao(sequenciais, paralelos);

        double mediaSequencial = analisar(sequenciais).getMediaNanos();

        double mediaParalela = analisar(paralelos).getMediaNanos();

        if (mediaParalela <= 0) {
            throw new IllegalArgumentException(
                    "Não é possível calcular o speedup com tempo paralelo igual a zero."
            );
        }
        return mediaSequencial / mediaParalela;
    }

    public static double calcularEficiencia(
            double speedup,
            int quantidadeThreads
    ) {
        if (speedup <= 0 || quantidadeThreads <= 0) {
            throw new IllegalArgumentException(
                    "Speedup e quantidade de threads devem ser positivos."
            );
        }
        return speedup / quantidadeThreads;
    }

    public static double calcularEficienciaPercentual(
            double speedup,
            int quantidadeThreads
    ) {
        return calcularEficiencia(speedup, quantidadeThreads) * 100.0;
    }

    private static void validarComparacao(
            List<ResultadoTeste> sequenciais,
            List<ResultadoTeste> paralelos
    ) {
        if (sequenciais == null || sequenciais.isEmpty()
                || paralelos == null || paralelos.isEmpty()) {

            throw new IllegalArgumentException(
                    "As duas listas precisam possuir resultados."
            );
        }
        ResultadoTeste primeiroSequencial = sequenciais.get(0);
        ResultadoTeste primeiroParalelo = paralelos.get(0);

        if (primeiroSequencial.getModoExecucao()
                != ResultadoTeste.ModoExecucao.SEQUENCIAL
                || primeiroParalelo.getModoExecucao()
                != ResultadoTeste.ModoExecucao.PARALELO) {

            throw new IllegalArgumentException(
                    "Compare uma execução sequencial com uma paralela."
            );
        }

        if (!normalizarNomeAlgoritmo(
                primeiroSequencial.getNomeAlgoritmo()
        ).equals(normalizarNomeAlgoritmo(
                primeiroParalelo.getNomeAlgoritmo()
        ))
                || primeiroSequencial.getTipoDados()
                != primeiroParalelo.getTipoDados()
                || primeiroSequencial.getTamanhoVetor()
                != primeiroParalelo.getTamanhoVetor()
                || sequenciais.size() != paralelos.size()) {

            throw new IllegalArgumentException(
                    "Os resultados não possuem condições equivalentes."
            );
        }

        int quantidadeThreads = primeiroParalelo.getQuantidadeThreads();

        for (int i = 0; i < sequenciais.size(); i++) {

            ResultadoTeste serial = sequenciais.get(i);
            ResultadoTeste paralelo = paralelos.get(i);

            if (serial.getModoExecucao()
                    != ResultadoTeste.ModoExecucao.SEQUENCIAL
                    || paralelo.getModoExecucao()
                    != ResultadoTeste.ModoExecucao.PARALELO

                    || !normalizarNomeAlgoritmo(
                    serial.getNomeAlgoritmo()
            ).equals(normalizarNomeAlgoritmo(
                    primeiroSequencial.getNomeAlgoritmo()
            ))

                    || !normalizarNomeAlgoritmo(
                    paralelo.getNomeAlgoritmo()
            ).equals(normalizarNomeAlgoritmo(
                    primeiroParalelo.getNomeAlgoritmo()
            ))

                    || serial.getQuantidadeThreads() != 1
                    || paralelo.getQuantidadeThreads() != quantidadeThreads

                    || serial.getTipoDados() != primeiroSequencial.getTipoDados()
                    || paralelo.getTipoDados() != primeiroParalelo.getTipoDados()

                    || serial.getTamanhoVetor() != primeiroSequencial.getTamanhoVetor()
                    || paralelo.getTamanhoVetor() != primeiroParalelo.getTamanhoVetor()

                    || serial.getNumeroAmostra() != paralelo.getNumeroAmostra()
                    || serial.getSemente() != paralelo.getSemente()) {

                throw new IllegalArgumentException(
                        "As amostras comparadas precisam representar os mesmos dados e condições."
                );
            }
        }
    }

    private static String normalizarNomeAlgoritmo(String nome) {

        return nome.replaceFirst("\\s+Paralelo$", "").trim();
    }

    public static class Estatisticas {
        private final double mediaNanos;
        private final double medianaNanos;
        private final double desvioPadraoNanos;

        private final long menorTempoNanos;
        private final long maiorTempoNanos;

        public Estatisticas(
                double mediaNanos,
                double medianaNanos,
                double desvioPadraoNanos,
                long menorTempoNanos,
                long maiorTempoNanos
        ) {
            this.mediaNanos = mediaNanos;
            this.medianaNanos = medianaNanos;
            this.desvioPadraoNanos = desvioPadraoNanos;

            this.menorTempoNanos = menorTempoNanos;
            this.maiorTempoNanos = maiorTempoNanos;
        }

        public double getMediaNanos() {
            return mediaNanos;
        }

        public double getMedianaNanos() {
            return medianaNanos;
        }

        public double getDesvioPadraoNanos() {
            return desvioPadraoNanos;
        }

        public long getMenorTempoNanos() {
            return menorTempoNanos;
        }

        public long getMaiorTempoNanos() {
            return maiorTempoNanos;
        }

        public double getMediaMilissegundos() {
            return mediaNanos / 1_000_000.0;
        }

        public double getMedianaMilissegundos() {
            return medianaNanos / 1_000_000.0;
        }

        public double getDesvioPadraoMilissegundos() {
            return desvioPadraoNanos / 1_000_000.0;
        }
    }
}
