# Diretório: Market Data Service

Serviço responsável por buscar dados de mercado de uma fonte externa.

### 1. maven-wrapper.properties

* O _Maven_ é uma ferramenta usada em projetos Java para gerenciar dependências, compilar o código, executar testes e gerar a aplicação.
* O _Maven Wrapper_ permite que o projeto use uma versão do Maven sem depender da versão instalada no computador.

### 2. Dockerfile

* "Receita" de como preparar o ambiente para executar o código.
* A _Imagem Docker_ é o molde pronto seguindo o _Dockerfile_. Tem tudo que precisa para executar, mas não está rodando.
* O _Container_ é uma cópia dessa imagem efetivamente em execução.

### 3. pom.xml

* Arquivo principal de configuração do Maven.
* Diz quais são as dependências do projeto, qual versão Java usar...

## backend/portfolio-service/src/main/resources

* Onde estão as configurações do Market Data Service.
* Porta da aplicação, conexão com Kafka, propriedades que o Spring Boot precisa para rodar...

## backend/portfolio-service/src/main/java/com/pulsedesk/marketdata

### 1. PortfolioController

* Recebe as requisições HTTP relacionadas às ações.
* “Porta de entrada” da API: recebe pedidos do frontend ou de outros clientes, como buscar informações sobre os ativos.
* Importante: ele separa a comunicação HTTP da lógica de negócio.

### 2. /dto

* _Data Transfer Object_ são objetos usados para transportar dados entre camadas da aplicação ou entre frontend e backend.
* _Market Data Response_: define quais dados e o formato que eles serão enviados em resposta da API. Temos: symbol, price, timestamp e percentageChange.

### 3. MarketDataEventProducer

* Transforma um dado de market data em um evento, que será publicado no Kafka.
* _KafkaTemplate<String, MarketDataUpdated>_: objeto que faz o envio para o Kafka.
* _publish()_: recebe um MarketDataResponse, cria um evento e envia para o tópico "market-data.updated".

### 4. FinnhubMarketDataProvider

* Responsável por buscar cotações da API Finnhub.
* Implementa ExternalMarketDataProvider para facilitar quando a fonte for trocada.
* Retorna no formato de MarketDataResponse.

### 5. MarketDataService

* Recebe um símbolo e pede a cotação para o provedor (finnhub).
