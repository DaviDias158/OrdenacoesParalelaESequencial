package algoritmos.sequenciais;

import algoritmos.AlgoritmoOrdenacao;

public class MergeSort implements AlgoritmoOrdenacao {

    @Override
    public String getNome() {
        return "Merge Sort";
    }

    @Override
    public void ordenar(int[] vetor) {

        if (vetor == null || vetor.length <= 1) {
            return;
        }

        int[] auxiliar = new int[vetor.length];

        ordenarRecursivo(vetor, auxiliar, 0, vetor.length - 1);
    }

    private void ordenarRecursivo(int[] vetor, int[] auxiliar, int inicio, int fim) {

        if (inicio < fim) {

            int meio = inicio + (fim - inicio) / 2;

            ordenarRecursivo(vetor, auxiliar, inicio, meio);
            ordenarRecursivo(vetor, auxiliar, meio + 1, fim);

            intercalar(vetor, auxiliar, inicio, meio, fim);
        }
    }

    private void intercalar(int[] vetor, int[] auxiliar, int inicio, int meio, int fim) {

        for (int i = inicio; i <= fim; i++) {
            auxiliar[i] = vetor[i];
        }

        int esquerda = inicio;
        int direita = meio + 1;
        int posicao = inicio;

        while (esquerda <= meio && direita <= fim) {

            if (auxiliar[esquerda] <= auxiliar[direita]) {
                vetor[posicao] = auxiliar[esquerda];
                esquerda++;
            } else {
                vetor[posicao] = auxiliar[direita];
                direita++;
            }

            posicao++;
        }

        while (esquerda <= meio) {
            vetor[posicao] = auxiliar[esquerda];
            esquerda++;
            posicao++;
        }

        while (direita <= fim) {
            vetor[posicao] = auxiliar[direita];
            direita++;
            posicao++;
        }
    }
}
