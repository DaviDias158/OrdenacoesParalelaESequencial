package algoritmos.sequenciais;

import algoritmos.AlgoritmoOrdenacao;

public class BubbleSort implements AlgoritmoOrdenacao {

    @Override
    public String getNome() {
        return "Bubble Sort";
    }

    @Override
    public void ordenar(int[] vetor) {

        int tamanho = vetor.length;
        boolean houveTroca;

        for (int i = 0; i < tamanho - 1; i++) {

            houveTroca = false;

            for (int j = 0; j < tamanho - 1 - i; j++) {

                if (vetor[j] > vetor[j + 1]) {

                    int temporario = vetor[j];

                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temporario;

                    houveTroca = true;
                }
            }

            // Interrompe caso o vetor já esteja ordenado.
            if (!houveTroca) {
                break;
            }
        }
    }
}
