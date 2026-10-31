# Análise de Desempenho de Algoritmos de Ordenação em Ambientes Concorrentes e Paralelos: Um Estudo Comparativo em Java

## Como executar o projeto

### Requisitos

Para executar o projeto, é necessário possuir:

- **Java JDK 8 ou superior** instalado.
- Uma IDE compatível com Java. Recomenda-se o IntelliJ IDEA.
- Sistema operacional compatível com a execução de aplicações Java com interface gráfica.

Não são necessárias bibliotecas externas, pois o projeto utiliza recursos presentes na biblioteca padrão do Java, incluindo Java Swing, Java2D e `java.util.concurrent`.

### Alunos
 - Davi Dias Vale - 2410485
 - João Victor Martins Cid - 2410447

### Execução

1. Baixe ou clone o repositório do projeto no GitHub.
2. Abra a pasta do projeto no IntelliJ IDEA.
3. Verifique se o JDK está configurado corretamente nas configurações do projeto.
4. Localize o arquivo `Main.java`, dentro do pacote `aplicacao`, no caminho `src/aplicacao/Main.java`.
5. Execute o método `main()` da classe `Main.java`.

A execução iniciará automaticamente os experimentos, percorrendo os algoritmos de ordenação, os diferentes tamanhos e tipos de conjuntos de dados e as configurações de threads definidas no programa.

Após a conclusão dos experimentos, será gerado o arquivo CSV e a interface gráfica será aberta automaticamente.

### Arquivo de resultados

Os resultados são armazenados no seguinte caminho:

```text
ProjetoOrdenacao/
└── resultados/
    └── resultados_testes.csv
```

A pasta `resultados` é criada automaticamente caso ainda não exista.

O arquivo `resultados_testes.csv` contém os registros individuais das execuções, incluindo o algoritmo utilizado, modo de execução, tipo de dados, tamanho do vetor, quantidade de threads, número da amostra e tempo de execução.

**Importante:** a cada nova execução do programa, o arquivo CSV anterior será sobrescrito pelos novos resultados. Portanto, recomenda-se salvar uma cópia dos resultados anteriores caso seja necessário preservá-los.

### Interface gráfica

Após a geração do CSV, a interface desenvolvida com Java Swing será aberta automaticamente.

Nela, é possível selecionar o algoritmo, o tipo de conjunto de dados e o tamanho do vetor para visualizar os gráficos comparativos dos tempos de execução, além dos indicadores de speedup e eficiência paralela.

O caminho do arquivo CSV utilizado pela interface é o mesmo caminho informado na etapa anterior, não sendo necessário importar os resultados manualmente.

## Resumo
Este trabalho apresenta uma análise comparativa do desempenho de quatro algoritmos de ordenação, Bubble Sort, Quick Sort, Merge Sort e Insertion Sort, em implementações sequenciais e paralelas, utilizando a linguagem Java. Os experimentos consideraram conjuntos de dados aleatórios, ordenados em ordem crescente, em ordem decrescente, com valores repetidos e quase ordenados. Os tamanhos dos vetores foram definidos por algoritmo, e as execuções paralelas foram realizadas com 1, 2, 4 e 8 threads. Para cada combinação de algoritmo, tipo de dado, tamanho e quantidade de threads, foram coletadas cinco amostras, após uma execução de aquecimento. Os tempos foram medidos com `System.nanoTime()` e registrados em arquivo CSV. Também foi desenvolvida uma interface gráfica com Java Swing para visualizar e comparar as medições. Os resultados indicaram ganhos de desempenho para Quick Sort e Merge Sort em configurações com múltiplas threads, enquanto as versões paralelas de Bubble Sort e Insertion Sort apresentaram maior custo de execução nas condições avaliadas.

## Introdução

A ordenação de dados é uma operação comum em sistemas computacionais e pode ser realizada por diferentes algoritmos, cada um com características próprias de desempenho. O tempo necessário para ordenar um conjunto depende, entre outros fatores, do algoritmo utilizado, da quantidade de elementos e da disposição inicial dos dados.

A programação concorrente e paralela permite dividir determinadas atividades entre múltiplas threads. Entretanto, aumentar a quantidade de threads não garante, por si só, uma redução no tempo total: a divisão do trabalho, a criação e o gerenciamento de tarefas e a sincronização também possuem custos.

Este trabalho compara as versões sequenciais e paralelas de Bubble Sort, Quick Sort, Merge Sort e Insertion Sort. A implementação foi desenvolvida em Java, utilizando recursos da biblioteca `java.util.concurrent` para organizar e executar tarefas paralelas. A fundamentação dos algoritmos de ordenação foi consultada no material didático OpenDSA [1], enquanto a documentação oficial do Java foi utilizada como referência para os recursos de concorrência [2] e para a interface gráfica Swing [3].

## Metodologia

O projeto foi desenvolvido em Java e organizado em pacotes separados para aplicação, algoritmos, testes, geração de dados, exportação e visualização. Foram implementadas quatro versões sequenciais e quatro versões paralelas.

Nas versões paralelas, foram utilizados recursos distintos de acordo com a estratégia de cada implementação:

- **Quick Sort:** divisão recursiva das partições em tarefas executadas por um `ForkJoinPool`, com processamento sequencial de partições pequenas.
- **Merge Sort:** divisão recursiva do vetor em duas partes, execução das subtarefas e intercalação dos resultados após a conclusão das duas partes.
- **Bubble Sort:** adaptação paralela baseada no método de transposição ímpar-par (*Odd-Even Transposition Sort*), com divisão das comparações entre trabalhadores e sincronização das fases por `CyclicBarrier`.
- **Insertion Sort:** mantém sequencial a procura da posição de inserção e paraleliza cópias dos deslocamentos quando a quantidade de elementos deslocados atinge o limite definido na implementação.

Foram gerados cinco tipos de conjuntos de dados:

- **Aleatórios:** valores inteiros gerados aleatoriamente no intervalo de -1.000.000 a 1.000.000.
- **Ordenados:** valores organizados em ordem crescente.
- **Decrescentes:** valores organizados em ordem decrescente.
- **Valores repetidos:** valores inteiros entre 0 e 10.
- **Quase ordenados:** vetores inicialmente crescentes, nos quais são realizadas algumas trocas aleatórias de posições.

Os tamanhos utilizados foram diferentes para cada família de algoritmo:

| Algoritmo | Tamanhos dos vetores |
|---|---:|
| Bubble Sort | 250, 500 e 1.000 elementos |
| Insertion Sort | 1.000, 2.000 e 4.000 elementos |
| Quick Sort | 10.000, 50.000 e 100.000 elementos |
| Merge Sort | 10.000, 50.000 e 100.000 elementos |

As execuções sequenciais foram realizadas com uma thread. Para as versões paralelas, foram configuradas execuções com 1, 2, 4 e 8 threads. O terminal registrou 12 processadores disponíveis para a JVM no ambiente utilizado.

Cada configuração experimental contou com uma execução de aquecimento e cinco amostras oficiais. O tempo foi medido em nanossegundos com `System.nanoTime()`. A geração e a cópia do vetor de entrada, assim como a verificação do resultado, ficaram fora do intervalo cronometrado. Para validar a ordenação, o vetor produzido por cada implementação foi comparado com uma cópia ordenada por `Arrays.sort()`.

Os dados foram exportados para CSV, com separador ponto e vírgula. Cada registro contém o algoritmo, o modo de execução, o tipo e o tamanho do conjunto, a quantidade de threads, o número da amostra, o tempo em nanossegundos e milissegundos e a semente usada para gerar os dados. A semente permite reproduzir as entradas correspondentes entre as versões sequencial e paralela.

No total, foram registrados 1.500 resultados, correspondentes a 300 combinações experimentais com cinco amostras cada. Para a visualização, foi criada uma interface com Java Swing, que permite selecionar o algoritmo, o tipo de dados e o tamanho do vetor. A interface apresenta um gráfico de barras com as medianas dos tempos e indicadores de speedup e eficiência.

## Resultados e Discussão

A execução da bateria experimental foi concluída e gerou 1.500 registros no arquivo CSV. O programa também realizou a validação de cada vetor ordenado por comparação com o resultado de referência produzido por `Arrays.sort()`.

Para avaliar a relação entre os tempos sequencial e paralelo, foi utilizado o speedup, calculado pela divisão do tempo sequencial pelo tempo paralelo correspondente. Valores maiores que 1 indicam que a execução paralela foi mais rápida; valores menores que 1 indicam que ela foi mais lenta.

A tabela apresenta a mediana dos speedups calculados para as 15 combinações de tipo de dado e tamanho de vetor de cada algoritmo, considerando separadamente cada quantidade de threads:

| Algoritmo | 1 thread | 2 threads | 4 threads | 8 threads |
|---|---:|---:|---:|---:|
| Bubble Sort* | 0,71x | 0,04x | 0,02x | 0,01x |
| Quick Sort | 0,60x | 1,45x | 2,14x | 2,42x |
| Merge Sort | 0,91x | 1,85x | 2,90x | 2,79x |
| Insertion Sort | 0,20x | 0,12x | 0,10x | 0,06x |

O Quick Sort apresentou ganho mediano a partir de duas threads. O maior valor mediano foi observado com oito threads, chegando a aproximadamente 2,42x. O resultado indica que a divisão das partições em tarefas permitiu aproveitar o processamento paralelo nos conjuntos avaliados. Entretanto, o ganho não aumentou proporcionalmente à quantidade de threads.

O Merge Sort apresentou o melhor resultado geral. Com quatro threads, alcançou speedup mediano de aproximadamente 2,90x. Com oito threads, o valor mediano foi de aproximadamente 2,79x, ligeiramente inferior ao resultado com quatro threads. Esse comportamento mostra que aumentar a quantidade de threads não implica necessariamente obter ganho adicional, pois o gerenciamento das tarefas e os recursos compartilhados também influenciam o tempo total.

O Bubble Sort paralelo apresentou tempos maiores que os da versão sequencial na maior parte das condições. A implementação paralela utiliza a estratégia de transposição ímpar-par, que exige sincronização entre as fases de comparação. Para os tamanhos avaliados, o custo dessas sincronizações superou o benefício da distribuição das comparações. Além disso, essa implementação paralela é uma adaptação baseada em comparações entre vizinhos, e não uma reprodução idêntica do Bubble Sort sequencial tradicional.

O Insertion Sort paralelo também apresentou desempenho inferior ao sequencial nas condições avaliadas. A busca pela posição de inserção continua sendo sequencial, e a paralelização é aplicada aos deslocamentos maiores. O custo de criar e executar as tarefas de cópia não foi compensado pelo trabalho distribuído entre as threads nos tamanhos testados.

Os resultados das versões paralelas com uma thread também foram, em geral, mais lentos que os das versões sequenciais. Isso é compatível com a presença de custos adicionais de gerenciamento dos pools e das tarefas, mesmo quando não há paralelismo efetivo entre múltiplos trabalhadores.

A quantidade de amostras e a execução de aquecimento adotadas permitiram realizar a comparação proposta, mas os resultados devem ser interpretados considerando que foram coletadas cinco amostras oficiais e apenas uma execução de aquecimento por configuração. Além disso, os arquivos fornecidos registram 12 processadores disponíveis para a JVM, mas não identificam o modelo do processador, a memória RAM ou a versão exata do Java. Esses dados não foram presumidos nesta análise.

A interface gráfica desenvolvida permitiu selecionar diferentes combinações de algoritmo, tipo de dado e tamanho de vetor, exibindo os tempos medianos, o speedup e a eficiência. Dessa forma, os resultados exportados em CSV puderam ser consultados visualmente, além de permanecerem disponíveis para processamento posterior.

## Conclusão

O trabalho permitiu implementar e comparar quatro algoritmos de ordenação em versões sequenciais e paralelas, utilizando Java. Foram realizados experimentos com diferentes tipos e tamanhos de conjuntos de dados e com configurações de 1, 2, 4 e 8 threads. Os resultados foram registrados em CSV e utilizados na geração de gráficos dinâmicos por meio de Java Swing.

Os resultados mostraram que o desempenho do processamento paralelo depende das características do algoritmo e dos custos de divisão e sincronização das tarefas. Entre as implementações avaliadas, o Merge Sort apresentou o melhor speedup mediano, com aproximadamente 2,90x em quatro threads. O Quick Sort também obteve ganhos, chegando a aproximadamente 2,42x em oito threads.

Por outro lado, as implementações paralelas de Bubble Sort e Insertion Sort foram mais lentas nas condições testadas. Isso evidencia que a utilização de múltiplas threads não é vantajosa para todos os algoritmos e que a estrutura do problema influencia diretamente o aproveitamento do paralelismo.

Conclui-se que a escolha de uma implementação paralela deve considerar o algoritmo, o tamanho e a natureza dos dados e o custo de gerenciamento das tarefas. O projeto também demonstrou a utilidade da coleta sistemática de tempos, da exportação em CSV e da visualização gráfica para comparar diferentes configurações de execução.

## Referências

[1] ORACLE. **Java Platform, Standard Edition 25: Package java.util.concurrent**. Documentação da API Java para programação concorrente, incluindo `ForkJoinPool`, `ExecutorService` e `CyclicBarrier`. Disponível em: <https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/package-summary.html>.

[2] ORACLE. **The Java Tutorials: Creating a GUI With Swing**. Tutorial de desenvolvimento de interfaces gráficas com Swing. Disponível em: <https://docs.oracle.com/javase/tutorial/uiswing/>. 
