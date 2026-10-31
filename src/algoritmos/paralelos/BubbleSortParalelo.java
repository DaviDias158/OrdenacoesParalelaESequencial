package algoritmos.paralelos;

import algoritmos.AlgoritmoOrdenacao;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BubbleSortParalelo implements AlgoritmoOrdenacao {
    private final int quantidadeThreads;
    private final ExecutorService grupoDeThreads;

    public BubbleSortParalelo(int quantidadeThreads) {
        if (quantidadeThreads < 1) {
            throw new IllegalArgumentException(
                    "A quantidade de threads deve ser maior que zero."
            );
        }
        this.quantidadeThreads = quantidadeThreads;

        this.grupoDeThreads =
                Executors.newFixedThreadPool(quantidadeThreads);
    }

    @Override
    public String getNome() {
        return "Bubble Sort Paralelo";
    }

    @Override
    public void ordenar(int[] vetor) {

        if (vetor == null || vetor.length <= 1) {
            return;
        }

        if (quantidadeThreads == 1) {
            ordenarOddEvenSequencial(vetor);
            return;
        }

        CyclicBarrier barreira =
                new CyclicBarrier(quantidadeThreads);

        Future<?>[] tarefas = new Future<?>[quantidadeThreads];

        for (int trabalhador = 0;
             trabalhador < quantidadeThreads;
             trabalhador++) {

            final int identificador = trabalhador;

            tarefas[trabalhador] = grupoDeThreads.submit(() -> {

                for (int fase = 0; fase < vetor.length; fase++) {

                    int primeiroIndice = fase % 2;

                    int quantidadePares =
                            (vetor.length - primeiroIndice) / 2;

                    int inicioPar =
                            quantidadePares * identificador / quantidadeThreads;

                    int fimPar =
                            quantidadePares * (identificador + 1) / quantidadeThreads;

                    for (int par = inicioPar; par < fimPar; par++) {

                        int indice = primeiroIndice + 2 * par;

                        if (vetor[indice] > vetor[indice + 1]) {

                            int temporario = vetor[indice];

                            vetor[indice] = vetor[indice + 1];
                            vetor[indice + 1] = temporario;
                        }
                    }

                    // Todas as threads precisam terminar a fase
                    // antes de começarem a próxima.
                    barreira.await();
                }

                return null;
            });
        }

        try {

            for (Future<?> tarefa : tarefas) {
                tarefa.get();
            }

        } catch (InterruptedException erro) {

            cancelarTarefas(tarefas);

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "A ordenação paralela foi interrompida.",
                    erro
            );

        } catch (ExecutionException erro) {

            cancelarTarefas(tarefas);

            throw new IllegalStateException(
                    "Falha durante a ordenação paralela.",
                    erro.getCause()
            );
        }
    }

    private static void ordenarOddEvenSequencial(int[] vetor) {

        for (int fase = 0; fase < vetor.length; fase++) {

            int primeiroIndice = fase % 2;

            for (
                    int indice = primeiroIndice;
                    indice + 1 < vetor.length;
                    indice += 2
            ) {

                if (vetor[indice] > vetor[indice + 1]) {

                    int temporario = vetor[indice];

                    vetor[indice] = vetor[indice + 1];
                    vetor[indice + 1] = temporario;
                }
            }
        }
    }

    private static void cancelarTarefas(Future<?>[] tarefas) {

        for (Future<?> tarefa : tarefas) {

            if (tarefa != null) {
                tarefa.cancel(true);
            }
        }
    }

    @Override
    public void close() {
        grupoDeThreads.shutdown();
    }
}
