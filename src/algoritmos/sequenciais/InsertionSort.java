package algoritmos.sequenciais;

import algoritmos.AlgoritmoOrdenacao;

public class InsertionSort implements AlgoritmoOrdenacao {

    @Override
    public String getNome() {
        return "Insertion Sort";
    }

    @Override
    public void ordenar(int[] vetor) {

        if (vetor == null || vetor.length <= 1) {
            return;
        }

        for (int i = 1; i < vetor.length; i++) {

            int valorAtual = vetor[i];
            int j = i - 1;

            while (j >= 0 && vetor[j] > valorAtual) {

                vetor[j + 1] = vetor[j];
                j--;
            }

            vetor[j + 1] = valorAtual;
        }
    }
}
