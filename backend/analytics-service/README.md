# Analytics Service

O **Analytics Service** é o serviço em que serão criados os dados de análise a partir do _Market Data_.


## Componentes do código

### MarketDataEventConsumer

Ele escuta (consome), pelo Kafka, o _market-data.updated_, que basicamente traz os dados de mercado atualizados.
Depois,ele chama o serviço _AnalyticsService_ e analisa os dados, retornando novas informações.
Em seguida, essa análise é publicada como _analytics.updated_ pelo Kafka.

### AnalyticsEventProducer

Depois do serviço _AnalyticsService_ realizar a análise do novo dado, ocorre a publicação desse evento, com as informações já atualizadas.

### AnalyticsService

O `AnalyticsService` realiza a análise dos dados recebidos do `Market Data Service`.

Atualmente, ele calcula a **volatilidade (`VOLATILITY_20`)** de cada ativo.

A volatilidade representa o quanto o preço de um ativo está oscilando recentemente. Quanto maior a volatilidade, maiores são as variações de preço. Quanto menor, mais estável está o ativo.

Para realizar esse cálculo, o serviço mantém separadamente os últimos **21 preços de cada símbolo** recebido.

Por exemplo:

    AAPL  → [21 preços]
    GOOGL → [21 preços]
    NVDA  → [21 preços]

Cada ativo possui seu próprio histórico.

Com os 21 preços, são calculados **20 retornos percentuais consecutivos**, pois cada retorno depende de dois preços:

    preço 1 → preço 2 = retorno 1
    preço 2 → preço 3 = retorno 2
    ...
    preço 20 → preço 21 = retorno 20

O retorno percentual é calculado por:

\[
retorno = \frac{preçoAtual - preçoAnterior}{preçoAnterior} \times 100
\]

Depois de calcular os 20 retornos, o serviço calcula o **desvio-padrão** deles.

O desvio-padrão indica o quanto esses retornos estão variando e, portanto, representa a volatilidade recente do ativo.

O processo pode ser resumido como:

    21 preços
        ↓
    20 retornos percentuais
        ↓
    desvio-padrão
        ↓
    VOLATILITY_20

Quando ainda não existem 21 preços para determinado ativo, o `AnalyticsService` não gera nenhum `AnalyticsUpdated`.

Quando o 21º preço chega, a primeira volatilidade pode ser calculada.

Depois disso, cada novo preço faz a janela avançar, o que é chamado de **janela móvel (sliding window)**.

Dessa forma, a volatilidade está sempre sendo calculada com base nos dados mais recentes do ativo.

O resultado é transformado em um evento `AnalyticsUpdated`, contendo informações como:

    symbol = AAPL
    indicator = VOLATILITY_20
    value = valor calculado

Esse evento é enviado ao `AnalyticsEventProducer`, que o publica no Kafka.

### target

A pasta target/ é criada automaticamente durante o build do projeto Java, principalmente pelo Maven.
Ela guarda os resultados gerados a partir do código-fonte, como arquivos .class compilados, classes criadas automaticamente por ferramentas como o Avro e o .jar final da aplicação.
