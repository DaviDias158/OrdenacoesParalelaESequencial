
package testes;

import dados.GeradorDados.TipoDados;

public class ConfiguracaoTeste {

    public enum FamiliaAlgoritmo {
        BUBBLE,
        QUICK,
        MERGE,
        INSERTION
    }

    private final int[] tamanhosBubble;
    private final int[] tamanhosQuick;
    private final int[] tamanhosMerge;
    private final int[] tamanhosInsertion;

    private final TipoDados[] tiposDados;
    private final int[] quantidadesThreads;

    private final int quantidadeAmostras;
    private final int quantidadeAquecimento;

    private final long sementeBase;

    public ConfiguracaoTeste(
            int[] tamanhosBubble,
            int[] tamanhosQuick,
            int[] tamanhosMerge,
            int[] tamanhosInsertion,
            TipoDados[] tiposDados,
            int[] quantidadesThreads,
            int quantidadeAmostras,
            int quantidadeAquecimento,
            long sementeBase
    ) {

        this.tamanhosBubble = validarTamanhos(
                tamanhosBubble, "Bubble Sort"
        );

        this.tamanhosQuick = validarTamanhos(
                tamanhosQuick, "Quick Sort"
        );

        this.tamanhosMerge = validarTamanhos(
                tamanhosMerge, "Merge Sort"
        );

        this.tamanhosInsertion = validarTamanhos(
                tamanhosInsertion, "Insertion Sort"
        );

        if (tiposDados == null || tiposDados.length == 0) {
            throw new IllegalArgumentException(
                    "Informe os tipos de dados."
            );
        }

        for (TipoDados tipo : tiposDados) {

            if (tipo == null) {
                throw new IllegalArgumentException(
                        "Os tipos de dados não podem ser nulos."
                );
            }
        }

        if (quantidadesThreads == null
                || quantidadesThreads.length == 0) {

            throw new IllegalArgumentException(
                    "Informe as quantidades de threads."
            );
        }

        for (int threads : quantidadesThreads) {

            if (threads <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade de threads deve ser positiva."
                );
            }
        }

        if (quantidadeAmostras < 5) {
            throw new IllegalArgumentException(
                    "São necessárias pelo menos 5 amostras."
            );
        }

        if (quantidadeAquecimento < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de aquecimentos não pode ser negativa."
            );
        }

        this.tiposDados = tiposDados.clone();
        this.quantidadesThreads = quantidadesThreads.clone();

        this.quantidadeAmostras = quantidadeAmostras;
        this.quantidadeAquecimento = quantidadeAquecimento;

        this.sementeBase = sementeBase;
    }

    private static int[] validarTamanhos(
            int[] tamanhos,
            String nomeAlgoritmo
    ) {

        if (tamanhos == null || tamanhos.length == 0) {
            throw new IllegalArgumentException(
                    "Informe os tamanhos para " + nomeAlgoritmo + "."
            );
        }

        int[] copia = tamanhos.clone();

        for (int i = 0; i < copia.length; i++) {

            if (copia[i] <= 0) {
                throw new IllegalArgumentException(
                        "Os tamanhos devem ser maiores que zero."
                );
            }

            if (i > 0 && copia[i] <= copia[i - 1]) {
                throw new IllegalArgumentException(
                        "Os tamanhos devem estar em ordem crescente e sem repetição."
                );
            }
        }

        return copia;
    }

    public int[] getTamanhos(FamiliaAlgoritmo familia) {

        if (familia == null) {
            throw new IllegalArgumentException(
                    "A família do algoritmo não pode ser nula."
            );
        }

        switch (familia) {

            case BUBBLE:
                return tamanhosBubble.clone();

            case QUICK:
                return tamanhosQuick.clone();

            case MERGE:
                return tamanhosMerge.clone();

            case INSERTION:
                return tamanhosInsertion.clone();

            default:
                throw new IllegalArgumentException(
                        "Família de algoritmo desconhecida."
                );
        }
    }

    public TipoDados[] getTiposDados() {
        return tiposDados.clone();
    }

    public int[] getQuantidadesThreads() {
        return quantidadesThreads.clone();
    }

    public int getQuantidadeAmostras() {
        return quantidadeAmostras;
    }

    public int getQuantidadeAquecimento() {
        return quantidadeAquecimento;
    }

    public long getSementeBase() {
        return sementeBase;
    }
}
