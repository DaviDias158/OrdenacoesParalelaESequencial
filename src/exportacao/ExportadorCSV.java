package exportacao;

import testes.ResultadoTeste;

import java.io.BufferedWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.Locale;

public class ExportadorCSV {

    private static final String SEPARADOR = ";";

    public static void exportar(
            List<ResultadoTeste> resultados,
            String caminhoArquivo
    ) throws IOException {
        if (resultados == null || resultados.isEmpty()) {
            throw new IllegalArgumentException(
                    "Não existem resultados para exportar."
            );
        }

        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Informe o caminho do arquivo CSV."
            );
        }

        Path caminho = Paths.get(caminhoArquivo);

        Path diretorio = caminho.getParent();
        if (diretorio != null) {
            Files.createDirectories(diretorio);
        }

        try (BufferedWriter escritor = Files.newBufferedWriter(
                caminho,
                StandardCharsets.UTF_8
        )) {
            escreverCabecalho(escritor);

            for (ResultadoTeste resultado : resultados) {
                escreverResultado(escritor, resultado);
            }
        }
        System.out.println(
                "\nArquivo CSV exportado com sucesso: "
                        + caminho.toAbsolutePath()
        );
    }

    private static void escreverCabecalho(
            BufferedWriter escritor
    ) throws IOException {
        escritor.write(
                "algoritmo;"
                        + "modo_execucao;"
                        + "tipo_dados;"
                        + "tamanho_vetor;"
                        + "quantidade_threads;"
                        + "numero_amostra;"
                        + "tempo_nanos;"
                        + "tempo_milissegundos;"
                        + "semente"
        );
        escritor.newLine();
    }

    private static void escreverResultado(
            BufferedWriter escritor,
            ResultadoTeste resultado
    ) throws IOException {
        StringBuilder linha = new StringBuilder();

        adicionarCampo(linha, resultado.getNomeAlgoritmo());
        adicionarCampo(linha, resultado.getModoExecucao().name());
        adicionarCampo(linha, resultado.getTipoDados().name());

        adicionarCampo(linha, resultado.getTamanhoVetor());
        adicionarCampo(linha, resultado.getQuantidadeThreads());
        adicionarCampo(linha, resultado.getNumeroAmostra());

        adicionarCampo(linha, resultado.getTempoNanos());

        adicionarCampo(
                linha,
                String.format(
                        Locale.US,
                        "%.6f",
                        resultado.getTempoMilissegundos()
                )
        );

        adicionarCampo(linha, resultado.getSemente());

        linha.setLength(linha.length() - SEPARADOR.length());

        escritor.write(linha.toString());
        escritor.newLine();
    }

    private static void adicionarCampo(
            StringBuilder linha,
            Object valor
    ) {
        String texto = String.valueOf(valor);

        if (texto.contains(SEPARADOR)
                || texto.contains("\"")
                || texto.contains("\n")
                || texto.contains("\r")) {
            texto = "\"" + texto.replace("\"", "\"\"") + "\"";
        }

        linha.append(texto).append(SEPARADOR);
    }
}
