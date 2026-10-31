package testes;

import algoritmos.AlgoritmoOrdenacao;

import algoritmos.sequenciais.BubbleSort;
import algoritmos.sequenciais.QuickSort;
import algoritmos.sequenciais.MergeSort;
import algoritmos.sequenciais.InsertionSort;

import algoritmos.paralelos.BubbleSortParalelo;
import algoritmos.paralelos.QuickSortParalelo;
import algoritmos.paralelos.MergeSortParalelo;
import algoritmos.paralelos.InsertionSortParalelo;

import dados.GeradorDados;
import dados.GeradorDados.TipoDados;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ExecutadorTestes {

    private final ConfiguracaoTeste configuracao;

    public ExecutadorTestes(ConfiguracaoTeste configuracao) {

        if (configuracao == null) {
            throw new IllegalArgumentException(
                    "A configuração dos testes não pode ser nula."
            );
        }

        this.configuracao = configuracao;
    }

    public List<ResultadoTeste> executar() {

        List<ResultadoTeste> resultados = new ArrayList<>();

        List<EspecificacaoAlgoritmo> algoritmos =
                criarAlgoritmos();

        for (TipoDados tipo : configuracao.getTiposDados()) {

            System.out.println(
                    "\n========================================"
            );

            System.out.println("Tipo de dados: " + tipo);

            for (EspecificacaoAlgoritmo especificacao : algoritmos) {

                int[] tamanhos = configuracao.getTamanhos(
                        especificacao.familia
                );

                for (int tamanho : tamanhos) {

                    System.out.println(
                            "\nTamanho: " + tamanho
                                    + " | Algoritmo: " + especificacao.nome
                                    + " | Threads: "
                                    + especificacao.quantidadeThreads
                    );

                    executarConfiguracao(
                            especificacao,
                            tipo,
                            tamanho,
                            resultados
                    );
                }
            }
        }

        System.out.println(
                "\nTodos os testes foram concluídos."
        );

        return resultados;
    }

    private void executarConfiguracao(
            EspecificacaoAlgoritmo especificacao,
            TipoDados tipo,
            int tamanho,
            List<ResultadoTeste> resultados
    ) {

        try (AlgoritmoOrdenacao algoritmo = especificacao.criar()) {

            System.out.println(
                    "  Executando: " + algoritmo.getNome()
            );

            // Aquecimento da JVM.
            for (int i = 0;
                 i < configuracao.getQuantidadeAquecimento();
                 i++) {

                int numeroAquecimento = -(i + 1);

                long semente = calcularSemente(
                        tipo,
                        tamanho,
                        numeroAquecimento
                );

                int[] original = GeradorDados.gerar(
                        tamanho,
                        tipo,
                        semente
                );

                int[] vetor = original.clone();

                algoritmo.ordenar(vetor);

                verificarOrdenacao(
                        original,
                        vetor,
                        algoritmo.getNome()
                );
            }

            // Execuções oficiais.
            for (int amostra = 1;
                 amostra <= configuracao.getQuantidadeAmostras();
                 amostra++) {

                long semente = calcularSemente(
                        tipo,
                        tamanho,
                        amostra
                );

                int[] original = GeradorDados.gerar(
                        tamanho,
                        tipo,
                        semente
                );

                int[] vetor = original.clone();

                long inicio = System.nanoTime();

                algoritmo.ordenar(vetor);

                long fim = System.nanoTime();

                long tempoExecucao = fim - inicio;

                verificarOrdenacao(
                        original,
                        vetor,
                        algoritmo.getNome()
                );

                ResultadoTeste resultado = new ResultadoTeste(
                        algoritmo.getNome(),
                        especificacao.modoExecucao,
                        tipo,
                        tamanho,
                        especificacao.quantidadeThreads,
                        amostra,
                        tempoExecucao,
                        semente
                );

                resultados.add(resultado);
            }
        }
    }

    private List<EspecificacaoAlgoritmo> criarAlgoritmos() {

        List<EspecificacaoAlgoritmo> algoritmos =
                new ArrayList<>();

        // Algoritmos sequenciais.

        algoritmos.add(new EspecificacaoAlgoritmo(
                ConfiguracaoTeste.FamiliaAlgoritmo.BUBBLE,
                "Bubble Sort",
                ResultadoTeste.ModoExecucao.SEQUENCIAL,
                1,
                BubbleSort::new
        ));

        algoritmos.add(new EspecificacaoAlgoritmo(
                ConfiguracaoTeste.FamiliaAlgoritmo.QUICK,
                "Quick Sort",
                ResultadoTeste.ModoExecucao.SEQUENCIAL,
                1,
                QuickSort::new
        ));

        algoritmos.add(new EspecificacaoAlgoritmo(
                ConfiguracaoTeste.FamiliaAlgoritmo.MERGE,
                "Merge Sort",
                ResultadoTeste.ModoExecucao.SEQUENCIAL,
                1,
                MergeSort::new
        ));

        algoritmos.add(new EspecificacaoAlgoritmo(
                ConfiguracaoTeste.FamiliaAlgoritmo.INSERTION,
                "Insertion Sort",
                ResultadoTeste.ModoExecucao.SEQUENCIAL,
                1,
                InsertionSort::new
        ));

        // Algoritmos paralelos.

        for (int quantidadeThreads :
                configuracao.getQuantidadesThreads()) {

            final int threads = quantidadeThreads;

            algoritmos.add(new EspecificacaoAlgoritmo(
                    ConfiguracaoTeste.FamiliaAlgoritmo.BUBBLE,
                    "Bubble Sort Paralelo",
                    ResultadoTeste.ModoExecucao.PARALELO,
                    threads,
                    () -> new BubbleSortParalelo(threads)
            ));

            algoritmos.add(new EspecificacaoAlgoritmo(
                    ConfiguracaoTeste.FamiliaAlgoritmo.QUICK,
                    "Quick Sort Paralelo",
                    ResultadoTeste.ModoExecucao.PARALELO,
                    threads,
                    () -> new QuickSortParalelo(threads)
            ));

            algoritmos.add(new EspecificacaoAlgoritmo(
                    ConfiguracaoTeste.FamiliaAlgoritmo.MERGE,
                    "Merge Sort Paralelo",
                    ResultadoTeste.ModoExecucao.PARALELO,
                    threads,
                    () -> new MergeSortParalelo(threads)
            ));

            algoritmos.add(new EspecificacaoAlgoritmo(
                    ConfiguracaoTeste.FamiliaAlgoritmo.INSERTION,
                    "Insertion Sort Paralelo",
                    ResultadoTeste.ModoExecucao.PARALELO,
                    threads,
                    () -> new InsertionSortParalelo(threads)
            ));
        }

        return algoritmos;
    }

    private long calcularSemente(
            TipoDados tipo,
            int tamanho,
            int numeroAmostra
    ) {

        long semente = configuracao.getSementeBase();

        semente = 31L * semente + tipo.ordinal();
        semente = 31L * semente + tamanho;
        semente = 31L * semente + numeroAmostra;

        return semente;
    }

    private void verificarOrdenacao(
            int[] original,
            int[] resultado,
            String nomeAlgoritmo
    ) {

        int[] esperado = original.clone();

        Arrays.sort(esperado);

        if (!Arrays.equals(esperado, resultado)) {

            throw new IllegalStateException(
                    "O algoritmo " + nomeAlgoritmo
                            + " produziu uma ordenação incorreta."
            );
        }
    }

    private static class EspecificacaoAlgoritmo {

        private final ConfiguracaoTeste.FamiliaAlgoritmo familia;

        private final String nome;

        private final ResultadoTeste.ModoExecucao modoExecucao;

        private final int quantidadeThreads;

        private final Supplier<AlgoritmoOrdenacao> fornecedor;

        public EspecificacaoAlgoritmo(
                ConfiguracaoTeste.FamiliaAlgoritmo familia,
                String nome,
                ResultadoTeste.ModoExecucao modoExecucao,
                int quantidadeThreads,
                Supplier<AlgoritmoOrdenacao> fornecedor
        ) {

            this.familia = familia;
            this.nome = nome;
            this.modoExecucao = modoExecucao;
            this.quantidadeThreads = quantidadeThreads;
            this.fornecedor = fornecedor;
        }

        public AlgoritmoOrdenacao criar() {
            return fornecedor.get();
        }
    }
}
