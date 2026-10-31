package algoritmos;

public interface AlgoritmoOrdenacao extends AutoCloseable {
    String getNome();
    void ordenar(int[] vetor);

    @Override
    default void close() {
    }
}