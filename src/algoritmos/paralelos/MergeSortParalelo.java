package algoritmos.paralelos;

import algoritmos.AlgoritmoOrdenacao;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class MergeSortParalelo implements AlgoritmoOrdenacao {

    private static final int LIMITE_SEQUENCIAL = 2048;

    private final ForkJoinPool grupoDeThreads;

    public MergeSortParalelo(int quantidadeThreads) {

        if (quantidadeThreads < 1) {
            throw new IllegalArgumentException(
                    "A quantidade de threads deve ser maior que zero."
            );
        }

        grupoDeThreads = new ForkJoinPool(quantidadeThreads);
    }

    @Override
    public String getNome() {
        return "Merge Sort Paralelo";
    }

    @Override
    public void ordenar(int[] vetor) {

        if (vetor == null || vetor.length <= 1) {
            return;
        }

        int[] auxiliar = new int[vetor.length];

        grupoDeThreads.invoke(
                new TarefaMergeSort(
                        vetor,
                        auxiliar,
                        0,
                        vetor.length - 1
                )
        );
    }

    @Override
    public void close() {
        grupoDeThreads.shutdown();
    }

    private static class TarefaMergeSort extends RecursiveAction {

        private final int[] vetor;
        private final int[] auxiliar;

        private final int inicio;
        private final int fim;

        TarefaMergeSort(
                int[] vetor,
                int[] auxiliar,
                int inicio,
                int fim
        ) {

            this.vetor = vetor;
            this.auxiliar = auxiliar;

            this.inicio = inicio;
            this.fim = fim;
        }

        @Override
        protected void compute() {

            if (inicio >= fim) {
                return;
            }

            if (fim - inicio + 1 <= LIMITE_SEQUENCIAL) {

                ordenarSequencial(
                        vetor,
                        auxiliar,
                        inicio,
                        fim
                );

                return;
            }

            int meio = inicio + (fim - inicio) / 2;

            TarefaMergeSort esquerda =
                    new TarefaMergeSort(
                            vetor,
                            auxiliar,
                            inicio,
                            meio
                    );

            TarefaMergeSort direita =
                    new TarefaMergeSort(
                            vetor,
                            auxiliar,
                            meio + 1,
                            fim
                    );

            // Executa as duas metades em paralelo.
            invokeAll(esquerda, direita);

            // Intercala os resultados depois que ambas terminam.
            intercalar(
                    vetor,
                    auxiliar,
                    inicio,
                    meio,
                    fim
            );
        }
    }

    private static void ordenarSequencial(
            int[] vetor,
            int[] auxiliar,
            int inicio,
            int fim
    ) {

        if (inicio >= fim) {
            return;
        }

        int meio = inicio + (fim - inicio) / 2;

        ordenarSequencial(
                vetor,
                auxiliar,
                inicio,
                meio
        );

        ordenarSequencial(
                vetor,
                auxiliar,
                meio + 1,
                fim
        );

        intercalar(
                vetor,
                auxiliar,
                inicio,
                meio,
                fim
        );
    }

    private static void intercalar(
            int[] vetor,
            int[] auxiliar,
            int inicio,
            int meio,
            int fim
    ) {

        System.arraycopy(
                vetor,
                inicio,
                auxiliar,
                inicio,
                fim - inicio + 1
        );

        int esquerda = inicio;
        int direita = meio + 1;
        int posicao = inicio;

        while (esquerda <= meio && direita <= fim) {

            if (auxiliar[esquerda] <= auxiliar[direita]) {

                vetor[posicao++] = auxiliar[esquerda++];

            } else {

                vetor[posicao++] = auxiliar[direita++];
            }
        }

        while (esquerda <= meio) {

            vetor[posicao++] = auxiliar[esquerda++];
        }

        while (direita <= fim) {

            vetor[posicao++] = auxiliar[direita++];
        }
    }
}
