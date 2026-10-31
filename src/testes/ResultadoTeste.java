package testes;

import dados.GeradorDados.TipoDados;

public class ResultadoTeste {

    public enum ModoExecucao {
        SEQUENCIAL,
        PARALELO
    }

    private final String nomeAlgoritmo;
    private final ModoExecucao modoExecucao;
    private final TipoDados tipoDados;

    private final int tamanhoVetor;
    private final int quantidadeThreads;
    private final int numeroAmostra;

    private final long tempoNanos;
    private final long semente;

    public ResultadoTeste(
            String nomeAlgoritmo,
            ModoExecucao modoExecucao,
            TipoDados tipoDados,
            int tamanhoVetor,
            int quantidadeThreads,
            int numeroAmostra,
            long tempoNanos,
            long semente
    ) {
        if (nomeAlgoritmo == null || nomeAlgoritmo.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do algoritmo é obrigatório.");
        }
        if (modoExecucao == null || tipoDados == null) {
            throw new IllegalArgumentException("O modo de execução e o tipo de dados são obrigatórios.");
        }
        if (tamanhoVetor <= 0 || quantidadeThreads <= 0 || numeroAmostra <= 0) {
            throw new IllegalArgumentException("Os parâmetros numéricos devem ser positivos.");
        }
        if (tempoNanos < 0) {
            throw new IllegalArgumentException("O tempo de execução não pode ser negativo.");
        }

        this.nomeAlgoritmo = nomeAlgoritmo;
        this.modoExecucao = modoExecucao;
        this.tipoDados = tipoDados;

        this.tamanhoVetor = tamanhoVetor;
        this.quantidadeThreads = quantidadeThreads;
        this.numeroAmostra = numeroAmostra;

        this.tempoNanos = tempoNanos;
        this.semente = semente;
    }

    public String getNomeAlgoritmo() {
        return nomeAlgoritmo;
    }

    public ModoExecucao getModoExecucao() {
        return modoExecucao;
    }

    public TipoDados getTipoDados() {
        return tipoDados;
    }

    public int getTamanhoVetor() {
        return tamanhoVetor;
    }

    public int getQuantidadeThreads() {
        return quantidadeThreads;
    }

    public int getNumeroAmostra() {
        return numeroAmostra;
    }

    public long getTempoNanos() {
        return tempoNanos;
    }

    public double getTempoMilissegundos() {
        return tempoNanos / 1_000_000.0;
    }

    public long getSemente() {
        return semente;
    }
}
