package algoritmos.paralelos;

import algoritmos.AlgoritmoOrdenacao;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class QuickSortParalelo implements AlgoritmoOrdenacao {

    private static final int LIMITE_SEQUENCIAL = 2048;

    private final ForkJoinPool grupoDeThreads;

    public QuickSortParalelo(int quantidadeThreads) {

        if (quantidadeThreads < 1) {
            throw new IllegalArgumentException(
                    "A quantidade de threads deve ser maior que zero."
            );
        }

        grupoDeThreads = new ForkJoinPool(quantidadeThreads);
    }

    @Override
    public String getNome() {
        return "Quick Sort Paralelo";
    }

    @Override
    public void ordenar(int[] vetor) {

        if (vetor == null || vetor.length <= 1) {
            return;
        }

        grupoDeThreads.invoke(
                new TarefaQuickSort(vetor, 0, vetor.length - 1)
        );
    }

    @Override
    public void close() {
        grupoDeThreads.shutdown();
    }

    private static class TarefaQuickSort extends RecursiveAction {

        private final int[] vetor;
        private final int inicio;
        private final int fim;

        TarefaQuickSort(int[] vetor, int inicio, int fim) {

            this.vetor = vetor;
            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        protected void compute() {

            if (inicio >= fim) {
                return;
            }

            // Partições pequenas são processadas sequencialmente.
            if (fim - inicio + 1 <= LIMITE_SEQUENCIAL) {

                ordenarSequencial(vetor, inicio, fim);
                return;
            }

            int indice = particionar(vetor, inicio, fim);

            TarefaQuickSort esquerda =
                    new TarefaQuickSort(vetor, inicio, indice);

            TarefaQuickSort direita =
                    new TarefaQuickSort(vetor, indice + 1, fim);

            invokeAll(esquerda, direita);
        }
    }

    private static int particionar(int[] vetor, int inicio, int fim) {

        int meio = inicio + (fim - inicio) / 2;

        int pivo = medianaDeTres(
                vetor[inicio],
                vetor[meio],
                vetor[fim]
        );

        int esquerda = inicio - 1;
        int direita = fim + 1;

        while (true) {

            do {
                esquerda++;
            } while (vetor[esquerda] < pivo);

            do {
                direita--;
            } while (vetor[direita] > pivo);

            if (esquerda >= direita) {
                return direita;
            }

            int temporario = vetor[esquerda];

            vetor[esquerda] = vetor[direita];
            vetor[direita] = temporario;
        }
    }

    private static int medianaDeTres(int a, int b, int c) {

        if (a < b) {

            if (b < c) {
                return b;
            }

            return (a < c) ? c : a;
        }

        if (a < c) {
            return a;
        }

        return (b < c) ? c : b;
    }

    private static void ordenarSequencial(
            int[] vetor,
            int inicio,
            int fim
    ) {

        while (inicio < fim) {

            int indice = particionar(vetor, inicio, fim);

            if (indice - inicio < fim - (indice + 1)) {

                ordenarSequencial(vetor, inicio, indice);
                inicio = indice + 1;

            } else {

                ordenarSequencial(vetor, indice + 1, fim);
                fim = indice;
            }
        }
    }
}
