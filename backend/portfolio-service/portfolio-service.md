# Diretório: Portfolio Service

Serviço responsável por manter e atualizar a carteira do usuário.
Todas os efeitos das ordens de compra e venda são registradas na carteira a partir do `portfolio-service`.

## 1. maven-wrapper.properties

* O _Maven_ é uma ferramenta usada em projetos Java para gerenciar dependências, compilar o código, executar testes e gerar a aplicação.
* O _Maven Wrapper_ permite que o projeto use uma versão do Maven sem depender da versão instalada no computador.

### 2. Dockerfile

* "Receita" de como preparar o ambiente para executar o código.
* A _Imagem Docker_ é o molde pronto seguindo o _Dockerfile_. Tem tudo que precisa para executar, mas não está rodando.
* O _Container_ é uma cópia dessa imagem efetivamente em execução.

### 3. pom.xml

* Arquivo principal de configuração do Maven.
* Diz quais são as dependências do projeto, qual versão Java usar...
