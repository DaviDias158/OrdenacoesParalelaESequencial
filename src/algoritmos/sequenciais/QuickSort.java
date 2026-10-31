package algoritmos.sequenciais;

import algoritmos.AlgoritmoOrdenacao;

public class QuickSort implements AlgoritmoOrdenacao {

    @Override
    public String getNome() {
        return "Quick Sort";
    }

    @Override
    public void ordenar(int[] vetor) {
        if (vetor == null || vetor.length <= 1) {
            return;
        }
        ordenarRecursivo(vetor, 0, vetor.length - 1);
    }

    private void ordenarRecursivo(int[] vetor, int inicio, int fim) {
        while (inicio < fim) {
            int indice = particionar(vetor, inicio, fim);

            if (indice - inicio < fim - (indice + 1)) {
                ordenarRecursivo(vetor, inicio, indice);
                inicio = indice + 1;
            } else {
                ordenarRecursivo(vetor, indice + 1, fim);
                fim = indice;
            }
        }
    }

    private int particionar(int[] vetor, int inicio, int fim) {

        int meio = inicio + (fim - inicio) / 2;

        int pivo = medianaDeTres(
                vetor[inicio],
                vetor[meio],
                vetor[fim]
        );

        int esquerda = inicio - 1;
        int direita = fim + 1;

        while (true) {
            do {
                esquerda++;
            } while (vetor[esquerda] < pivo);

            do {
                direita--;
            } while (vetor[direita] > pivo);

            if (esquerda >= direita) {
                return direita;
            }

            int temporario = vetor[esquerda];

            vetor[esquerda] = vetor[direita];
            vetor[direita] = temporario;
        }
    }

    private int medianaDeTres(int a, int b, int c) {
        if (a < b) {
            if (b < c) {
                return b;
            }
            return (a < c) ? c : a;
        }

        if (a < c) {
            return a;
        }
        return (b < c) ? c : b;
    }
}
