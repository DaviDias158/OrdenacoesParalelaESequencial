package dados;

import java.util.Random;

public class GeradorDados {
    public enum TipoDados {
        ALEATORIOS,
        ORDENADOS,
        DECRESCENTES,
        VALORES_REPETIDOS,
        QUASE_ORDENADOS
    }

    private static final int LIMITE_VALORES_ALEATORIOS = 1_000_000;
    private static final int QUANTIDADE_VALORES_REPETIDOS = 11;

    public static int[] gerar(int tamanho, TipoDados tipo) {
        return gerar(tamanho, tipo, new Random().nextLong());
    }

    public static int[] gerar(int tamanho, TipoDados tipo, long semente) {
        if (tamanho < 0) {
            throw new IllegalArgumentException(
                    "O tamanho do conjunto não pode ser negativo."
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo do conjunto de dados não pode ser nulo."
            );
        }

        int[] vetor = new int[tamanho];
        Random aleatorio = new Random(semente);

        switch (tipo) {
            case ALEATORIOS:
                for (int i = 0; i < tamanho; i++) {

                    vetor[i] = aleatorio.nextInt(
                            2 * LIMITE_VALORES_ALEATORIOS + 1
                    ) - LIMITE_VALORES_ALEATORIOS;
                }
                break;

            case ORDENADOS:
                for (int i = 0; i < tamanho; i++) {
                    vetor[i] = i;
                }
                break;

            case DECRESCENTES:
                for (int i = 0; i < tamanho; i++) {
                    vetor[i] = tamanho - i;
                }
                break;

            case VALORES_REPETIDOS:
                for (int i = 0; i < tamanho; i++) {
                    vetor[i] = aleatorio.nextInt(
                            QUANTIDADE_VALORES_REPETIDOS
                    );
                }
                break;

            case QUASE_ORDENADOS:
                for (int i = 0; i < tamanho; i++) {
                    vetor[i] = i;
                }
                int quantidadeTrocas = Math.max(1, tamanho / 100);

                for (int i = 0; i < quantidadeTrocas && tamanho > 1; i++) {
                    int posicao1 = aleatorio.nextInt(tamanho);
                    int posicao2 = aleatorio.nextInt(tamanho);
                    int temporario = vetor[posicao1];

                    vetor[posicao1] = vetor[posicao2];
                    vetor[posicao2] = temporario;
                }
                break;
        }
        return vetor;
    }
}
