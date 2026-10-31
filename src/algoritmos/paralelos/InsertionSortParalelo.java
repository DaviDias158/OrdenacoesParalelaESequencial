package algoritmos.paralelos;

import algoritmos.AlgoritmoOrdenacao;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class InsertionSortParalelo implements AlgoritmoOrdenacao {

    private static final int LIMITE_DESLOCAMENTO_PARALELO = 1024;

    private static final int LIMITE_COPIA_POR_TAREFA = 512;

    private final ForkJoinPool grupoDeThreads;

    public InsertionSortParalelo(int quantidadeThreads) {

        if (quantidadeThreads < 1) {
            throw new IllegalArgumentException(
                    "A quantidade de threads deve ser maior que zero."
            );
        }

        grupoDeThreads = new ForkJoinPool(quantidadeThreads);
    }

    @Override
    public String getNome() {
        return "Insertion Sort Paralelo";
    }

    @Override
    public void ordenar(int[] vetor) {

        if (vetor == null || vetor.length <= 1) {
            return;
        }

        grupoDeThreads.invoke(
                new TarefaOrdenacao(vetor)
        );
    }

    @Override
    public void close() {
        grupoDeThreads.shutdown();
    }

    private static class TarefaOrdenacao extends RecursiveAction {

        private final int[] vetor;

        TarefaOrdenacao(int[] vetor) {
            this.vetor = vetor;
        }

        @Override
        protected void compute() {

            int[] auxiliar = new int[vetor.length];

            for (int i = 1; i < vetor.length; i++) {

                int valorAtual = vetor[i];
                int posicao = i;

                // Procura sequencialmente a posição correta.
                while (
                        posicao > 0 &&
                                vetor[posicao - 1] > valorAtual
                ) {
                    posicao--;
                }

                int quantidadeDeslocamentos = i - posicao;

                if (quantidadeDeslocamentos == 0) {
                    continue;
                }

                if (
                        quantidadeDeslocamentos >=
                                LIMITE_DESLOCAMENTO_PARALELO
                ) {

                    // Copia os elementos para o vetor auxiliar,
                    // distribuindo as cópias entre as threads.
                    new TarefaCopia(
                            vetor,
                            auxiliar,
                            posicao,
                            posicao + 1,
                            quantidadeDeslocamentos
                    ).invoke();

                    // Transfere os elementos para suas novas posições.
                    new TarefaCopia(
                            auxiliar,
                            vetor,
                            posicao + 1,
                            posicao + 1,
                            quantidadeDeslocamentos
                    ).invoke();

                } else {

                    // Para deslocamentos pequenos, utiliza
                    // a operação sequencial tradicional.
                    for (int j = i; j > posicao; j--) {
                        vetor[j] = vetor[j - 1];
                    }
                }

                vetor[posicao] = valorAtual;
            }
        }
    }

    private static class TarefaCopia extends RecursiveAction {

        private final int[] origem;
        private final int[] destino;

        private final int inicioOrigem;
        private final int inicioDestino;

        private final int quantidade;

        TarefaCopia(
                int[] origem,
                int[] destino,
                int inicioOrigem,
                int inicioDestino,
                int quantidade
        ) {

            this.origem = origem;
            this.destino = destino;

            this.inicioOrigem = inicioOrigem;
            this.inicioDestino = inicioDestino;

            this.quantidade = quantidade;
        }

        @Override
        protected void compute() {

            if (quantidade <= LIMITE_COPIA_POR_TAREFA) {

                System.arraycopy(
                        origem,
                        inicioOrigem,
                        destino,
                        inicioDestino,
                        quantidade
                );

                return;
            }

            int metade = quantidade / 2;

            TarefaCopia esquerda = new TarefaCopia(
                    origem,
                    destino,
                    inicioOrigem,
                    inicioDestino,
                    metade
            );

            TarefaCopia direita = new TarefaCopia(
                    origem,
                    destino,
                    inicioOrigem + metade,
                    inicioDestino + metade,
                    quantidade - metade
            );

            invokeAll(esquerda, direita);
        }
    }
}
