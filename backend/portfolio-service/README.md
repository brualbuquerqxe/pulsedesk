# Diretório: Portfolio Service

Serviço responsável por manter e atualizar a carteira do usuário.
Todas os efeitos das ordens de compra e venda são registradas na carteira a partir do `portfolio-service`.

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

## backend/portfolio-service/src/main/java/com/pulsedesk/portfolio

### 1. PortfolioController

* Recebe as requisições HTTP relacionadas à carteira.
* “Porta de entrada” da API: recebe pedidos do frontend, como buscar a carteira de um usuário, e encaminha para o PortfolioService, onde fica a lógica de negócio.
* Devolve a resposta para o frontend, normalmente em JSON.
* Importante: ele mistura a comunicação HTTP da lógica de negócio.

### 4. /dto

* _Data Transfer Object_ são objetos usados para transportar dados entre camadas da aplicação ou entre frontend e backend.
* _Portfolio Service_: define quais dados e o formato que eles serão enviados pro frontend em resposta da API. Temos: portfolioId, cashBalance, lista das positions.
* _Position Response_: define quais dados e o formato que eles serão enviados pro frontend em resposta da API. Temos: symbol, quantity, averagePrice e lastPrice.

### 5. /entity

* Classes que representam informações armazenadas em bancos de dados.

### 6. /repository

* Responsável por acessar o banco de dados, já a _Entity_ é o dado.
* Usa JpaRepository, uma interface fornecida pelo Spring Data JPA.

### 7. PortfolioService

* Onde fica a lógica de negócio da carteira.
* Recebe pedidos do PortfolioController, usa os Repositories para buscar ou alterar dados no banco e aplica as regras necessárias antes de devolver o resultado.





















