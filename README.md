# PulseDesk

O **PulseDesk** é uma workstation de investimentos construída para simular, de forma simplificada, o funcionamento de uma plataforma de negociação de ações.

A ideia do projeto não é apenas permitir que o usuário veja preços ou registre compras e vendas. O principal objetivo é conectar diferentes partes de um sistema financeiro usando uma **arquitetura orientada a eventos**.

No PulseDesk, cada serviço tem uma responsabilidade específica. Em vez de todos os componentes dependerem diretamente uns dos outros, os principais acontecimentos do sistema são transformados em **eventos** e publicados no **Apache Kafka**.

Assim, quando alguma coisa acontece — por exemplo, uma cotação é atualizada, uma ordem é executada ou o portfólio muda — outros serviços podem reagir a esse acontecimento sem precisar conhecer diretamente quem o produziu.

O projeto utiliza:

- **Java + Spring Boot** no backend;
- **Angular + TypeScript** no frontend;
- **Apache Kafka** para comunicação assíncrona entre serviços;
- **Apache Avro** para definir os contratos dos eventos;
- **Schema Registry** para armazenar e validar esses contratos;
- **PostgreSQL** para persistência dos dados;
- **Finnhub** como fonte externa de market data;
- **WebSocket** para enviar atualizações em tempo real para a interface;
- **FDC3** para comunicação padronizada entre aplicações financeiras;
- **Docker e Docker Compose** para executar toda a infraestrutura localmente.

---

# Arquitetura orientada a eventos

O PulseDesk utiliza uma **Event-Driven Architecture (EDA)**.

Em uma arquitetura tradicional, um serviço normalmente chama outro diretamente:

Isso cria uma dependência maior entre eles.

No PulseDesk, vários fluxos utilizam eventos:

Ou seja:

- um serviço percebe que alguma coisa aconteceu;
- transforma esse acontecimento em um evento;
- publica o evento em um tópico Kafka;
- os serviços interessados consomem esse tópico;
- cada consumidor decide o que fazer com o evento.

ele pode publicar um evento:

no tópico:

O Market Data Service não precisa saber exatamente o que todos os outros serviços farão com essa informação.

O Analytics Service pode usar o evento para atualizar indicadores.

O WebSocket Gateway pode usar o mesmo evento para atualizar a tela.

Essa separação é uma das principais ideias do projeto.

---

# Principais eventos

Os eventos representam acontecimentos importantes dentro do PulseDesk.

### `market-data.updated`

- Informa que uma nova cotação de mercado foi recebida.
- É produzido pelo **Market Data Service**.
- Pode ser consumido pelo **Analytics Service** e pelo **WebSocket Gateway**.

### `analytics.updated`

- Informa que algum dado analítico foi recalculado.
- É produzido pelo **Analytics Service**.
- Pode carregar, por exemplo, o símbolo do ativo, tipo do indicador, valor e timestamp.
- Pode ser consumido pelo **WebSocket Gateway** para atualizar a interface.

### `order.created`

- Representa uma nova ordem criada pelo usuário.
- Pode representar uma compra ou uma venda.
- Permite que o processamento da ordem aconteça de forma assíncrona.

### `order.executed`

- Informa que uma ordem foi executada com sucesso.
- O Portfolio Service pode consumir esse evento para atualizar saldo e posições.

### `order.rejected`

- Informa que uma ordem não pôde ser executada.
- Pode acontecer, por exemplo, quando alguma regra necessária para a operação não é satisfeita.

### `portfolio.updated`

- Informa que o portfólio do usuário mudou.
- Pode acontecer depois de uma compra ou venda.
- O WebSocket Gateway pode consumir esse evento para atualizar a interface.

---

# Tecnologias

## 1. Java

- Linguagem utilizada no backend do PulseDesk.
- Cada serviço backend é uma aplicação Java independente.
- É onde ficam as regras de negócio, integração com Kafka, persistência no banco e comunicação com APIs externas.

No projeto, Java é utilizado principalmente para:

- criar APIs REST;
- implementar serviços;
- consumir e produzir eventos Kafka;
- manipular os objetos Avro;
- acessar o PostgreSQL;
- implementar regras de compra, venda, portfólio e market data.

---

## 2. Spring Boot

- Framework utilizado para construir os serviços backend.
- Facilita a criação de aplicações Java porque já fornece grande parte da infraestrutura necessária para uma aplicação web.

Em vez de configurar manualmente servidor HTTP, serialização, injeção de dependências e várias integrações, o Spring Boot fornece essas funcionalidades através das dependências e configurações do projeto.

### Dentro do PulseDesk, ele é usado para:

- criar os serviços backend;
- criar endpoints HTTP;
- fazer injeção de dependências;
- conectar os serviços ao Kafka;
- conectar os serviços ao PostgreSQL;
- organizar as camadas da aplicação;
- carregar configurações do `application.yml` ou `application.properties`.

Cada camada possui uma responsabilidade diferente.

---

## 3. Spring Web

- Parte do Spring usada para criar APIs HTTP.
- Permite criar endpoints que o frontend consegue chamar.

O Angular pode fazer essa requisição e receber uma resposta com os dados do ativo.

O Spring Web é responsável por receber a requisição HTTP, transformá-la em objetos Java e devolver uma resposta HTTP.

---

## 4. Spring Data JPA

- Facilita a comunicação entre o código Java e o banco de dados.
- É usado principalmente nos serviços que precisam persistir informações.

No PulseDesk, é especialmente importante no **Portfolio Service** e no **Trading Service**.

Em vez de escrever SQL manualmente para todas as operações, podem ser utilizados `Repository`.

Eles fazem a ponte entre as entidades Java e as tabelas do PostgreSQL.

---

## 5. Hibernate

- É a implementação de ORM utilizada normalmente pelo Spring Data JPA.
- ORM significa **Object-Relational Mapping**.

A função dele é fazer a conversão entre:

pode representar uma linha de uma tabela de posições no PostgreSQL.

Assim, o código trabalha principalmente com objetos Java, enquanto Hibernate/JPA cuidam de grande parte da comunicação com o banco.

---

## 6. Maven

- Ferramenta utilizada para gerenciar projetos Java.
- Controla dependências, compilação, testes e geração da aplicação.

O arquivo principal é:

Nele ficam informações como:

- versão do Java;
- dependências do Spring;
- dependências do Kafka;
- dependências do Avro;
- plugins;
- configurações de build.

### Maven Wrapper

Os arquivos:

formam o **Maven Wrapper**.

Ele permite utilizar a versão configurada do Maven sem depender da versão instalada manualmente no computador.

---

## 7. Apache Kafka

- É o broker de eventos do PulseDesk.
- Funciona como o intermediário entre os serviços.

Um serviço pode produzir uma mensagem:

e vários consumidores podem receber essa informação:

### Conceitos principais

#### Producer

- Serviço que publica um evento no Kafka.

#### Consumer

- Serviço que escuta um tópico e reage às mensagens recebidas.

#### Topic

- Canal lógico onde determinado tipo de evento é publicado.

#### Key

#### Partition

- Um tópico pode ser dividido em várias partições.
- As partições permitem distribuir o processamento das mensagens.

#### Consumer Group

- Conjunto de consumidores que trabalham juntos.
- Dentro do mesmo grupo, cada mensagem de uma partição é processada por apenas um consumidor daquele grupo.

### Por que usar Kafka no PulseDesk?

Porque várias partes do sistema precisam reagir aos mesmos acontecimentos.

Kafka permite:

- desacoplar os serviços;
- processar eventos de forma assíncrona;
- adicionar novos consumidores sem alterar o produtor;
- manter um histórico de eventos durante o período de retenção configurado;
- aproximar a arquitetura do comportamento de sistemas financeiros distribuídos reais.

---

## 8. KafkaTemplate

- Classe do Spring Kafka utilizada pelos produtores.
- É a interface que o código Java usa para enviar mensagens para o Kafka.

No Market Data Service, por exemplo, um produtor pode criar um `MarketDataUpdated` e enviar o objeto usando o `KafkaTemplate`.

---

## 9. Apache Avro

- Formato de serialização utilizado para os eventos do Kafka.
- Também permite definir explicitamente a estrutura que um evento deve possuir.

Os contratos são descritos através de schemas.

O schema define:

- nome do evento;
- campos;
- tipos dos campos;
- quais informações fazem parte daquele contrato.

### Por que isso é importante?

Avro reduz esse tipo de problema porque produtor e consumidor trabalham a partir de um contrato definido.

---

## 10. Schema Registry

- Serviço responsável por armazenar e controlar os schemas utilizados pelos eventos.
- Trabalha junto com Kafka e Avro.

O Kafka armazena as mensagens.

O Schema Registry armazena a definição do formato dessas mensagens.

### Outra função importante

Ele também ajuda a controlar **compatibilidade entre versões**.

Por exemplo, se uma nova versão de um evento adicionar ou alterar campos, o Schema Registry pode verificar se a mudança continua compatível com consumidores que utilizam versões anteriores.

No PulseDesk, isso permite evoluir os contratos sem alterar os eventos de forma descontrolada.

---

## 11. Contracts

Os contratos dos eventos ficam separados da lógica dos serviços.

A ideia é existir uma definição comum para eventos como:

Os arquivos Avro funcionam como a fonte do contrato.

A partir deles podem ser geradas classes Java.

Assim:

Os serviços importam essas classes em vez de cada serviço inventar sua própria versão do mesmo evento.

Isso reduz inconsistências entre produtores e consumidores.

---

## 12. PostgreSQL

- Banco de dados relacional utilizado pelo PulseDesk.
- Guarda informações que precisam continuar existindo mesmo depois que um serviço é reiniciado.

Exemplos de informações persistentes:

- usuário;
- portfólio;
- saldo;
- posições;
- ordens;
- histórico de operações.

### Diferença importante

Kafka e PostgreSQL possuem funções diferentes.

Kafka não substitui o banco de dados.

O banco guarda o estado necessário para a aplicação.

Kafka transporta os acontecimentos do sistema.

---

## 13. Docker

- Ferramenta usada para executar aplicações em ambientes isolados chamados containers.

### Dockerfile

O `Dockerfile` funciona como uma "receita".

Ele explica como criar a imagem de uma aplicação.

### Imagem

- É o molde pronto criado a partir do Dockerfile.
- Contém o ambiente e os arquivos necessários para executar a aplicação.
- Ainda não significa que a aplicação está rodando.

### Container

- É uma instância da imagem efetivamente em execução.

Fluxo:

---

## 14. Docker Compose

- Ferramenta utilizada para executar vários containers juntos.
- É especialmente útil no PulseDesk porque o projeto depende de vários componentes.

Em vez de iniciar manualmente:

- Kafka;
- Schema Registry;
- PostgreSQL;
- Market Data Service;
- Analytics Service;
- Trading Service;
- Portfolio Service;
- WebSocket Gateway;
- frontend;

o Docker Compose descreve esses componentes em um arquivo e permite iniciar o ambiente de forma coordenada.

O arquivo principal normalmente é:

ou:

---

## 15. Finnhub

- API externa utilizada como fonte de market data.
- É de onde o Market Data Service busca as cotações reais.

Fluxo:

O restante do sistema não precisa conhecer diretamente o formato retornado pela Finnhub.

Essa responsabilidade fica isolada no provider.

Isso é importante porque, se a fonte de dados for trocada no futuro, a alteração fica concentrada na camada de integração.

---

## 16. DTO

`DTO` significa **Data Transfer Object**.

- São objetos utilizados para transportar dados entre partes da aplicação.
- Não representam necessariamente uma entidade salva no banco.
- Servem para definir exatamente quais informações entram ou saem de determinada camada.

pode conter:

Isso evita expor diretamente objetos internos da aplicação.

---

## 17. Provider

- Camada responsável por conversar com algum serviço externo.
- Evita misturar a lógica da API externa com a lógica principal do sistema.

No Market Data Service:

`ExternalMarketDataProvider` define o comportamento esperado.

`FinnhubMarketDataProvider` implementa esse comportamento usando a Finnhub.

Dessa forma, o restante do sistema depende da abstração e não diretamente da API específica.

---

## 18. Angular

- Framework utilizado para construir o frontend do PulseDesk.
- É responsável pela interface que o usuário utiliza.

No frontend ficam elementos como:

- tela de market data;
- visualização de ativos;
- informações analíticas;
- formulário de compra e venda;
- posições do portfólio;
- saldo;
- histórico;
- atualizações em tempo real.

Angular organiza a interface em componentes e serviços.

O componente cuida principalmente da interface.

O service pode concentrar a comunicação HTTP ou WebSocket com o backend.

---

## 19. TypeScript

- Linguagem utilizada pelo frontend Angular.
- É baseada em JavaScript, mas adiciona tipagem.

A tipagem ajuda a detectar erros antes da aplicação ser executada e deixa o código mais previsível.

Depois, o TypeScript é compilado para JavaScript, que é executado pelo navegador.

---

## 20. RxJS

- Biblioteca utilizada pelo Angular para trabalhar com programação reativa.
- É baseada principalmente em `Observable`.

Ela é útil quando os dados chegam ao longo do tempo.

Isso combina bem com o PulseDesk, porque market data e eventos de WebSocket não são necessariamente respostas únicas: novos dados podem continuar chegando.

---

## 21. WebSocket

HTTP normalmente funciona como:

Depois da resposta, aquela comunicação termina.

WebSocket permite manter uma conexão aberta.

Isso permite que o servidor envie informações para a tela assim que algo acontecer.

No PulseDesk, isso é útil para:

- novas cotações;
- atualização de analytics;
- execução ou rejeição de ordens;
- atualização do portfólio.

Sem WebSocket, o frontend teria que perguntar repetidamente ao backend se alguma coisa mudou.

---

## 22. WebSocket Gateway

- Serviço responsável por fazer a ponte entre os eventos internos e o frontend.
- Consome eventos Kafka relevantes.
- Envia essas atualizações para o Angular através de WebSocket.

Fluxo:

Isso evita que o frontend tenha que conversar diretamente com Kafka.

O navegador não precisa conhecer os detalhes da infraestrutura de eventos.

---

## 23. FDC3

`FDC3` é um padrão criado para melhorar a interoperabilidade entre aplicações financeiras de desktop.

Ele não substitui Kafka, WebSocket, Angular ou Spring Boot.

A função é diferente.

Kafka resolve comunicação de eventos **entre serviços backend**.

FDC3 ajuda aplicações financeiras **na camada de desktop/frontend** a compartilhar contexto e comandos de maneira padronizada.

### Conceitos importantes

#### Context

Representa a informação que está sendo compartilhada.

pode representar um ativo financeiro.

#### Intent

Representa uma ação que uma aplicação deseja executar.

Por exemplo, uma aplicação pode solicitar que outra aplicação visualize informações de determinado instrumento.

#### Channel

Permite que aplicações compartilhem contexto através de um canal comum.

### No PulseDesk

FDC3 adiciona ao projeto uma ideia comum em plataformas financeiras profissionais: aplicações diferentes conseguirem compartilhar contexto sem depender de integrações específicas entre cada par de aplicações.

---

## 24. JSON

- Formato utilizado principalmente nas comunicações HTTP.
- Também é o formato retornado pela Finnhub.

O formato externo da Finnhub é convertido para objetos internos do PulseDesk.

É importante diferenciar:

Cada formato está sendo utilizado onde faz mais sentido dentro da arquitetura.

---

## 25. Jackson

- Biblioteca utilizada pelo Spring para converter JSON em objetos Java e objetos Java em JSON.
- Também permite mapear nomes de campos diferentes.

Por exemplo, se a Finnhub retornar:

o código pode mapear `"c"` para algo mais legível internamente:

Assim, o formato ruim ou abreviado de uma API externa não precisa se espalhar pelo restante da aplicação.

---

# Serviços do backend

## 1. Market Data Service

Serviço responsável por buscar e distribuir dados de mercado.

### Responsabilidades

- receber um símbolo;
- buscar a cotação na Finnhub;
- converter a resposta externa para o modelo interno;
- devolver market data pela API;
- transformar atualizações relevantes em eventos;
- publicar `market-data.updated` no Kafka.

### Fluxo

Depois da cotação:

---

## 2. Analytics Service

Serviço responsável por transformar market data em informações analíticas.

### Responsabilidades

- consumir `market-data.updated`;
- executar os cálculos definidos pelo sistema;
- representar o resultado através de um evento analítico;
- publicar `analytics.updated`.

Fluxo:

A vantagem de separar Analytics de Market Data é que buscar uma cotação e analisar uma cotação são responsabilidades diferentes.

O Market Data Service fornece o dado.

O Analytics Service interpreta o dado.

---

## 3. Trading Service

Serviço responsável pelo fluxo de negociação.

### Responsabilidades

- receber pedidos de compra e venda;
- criar ordens;
- persistir informações relacionadas às ordens;
- publicar eventos relacionados ao ciclo da ordem;
- processar a execução simulada;
- informar se a ordem foi executada ou rejeitada.

Fluxo conceitual:

---

## 4. Portfolio Service

Serviço responsável pelo estado financeiro do usuário dentro da simulação.

### Responsabilidades

- armazenar o usuário;
- armazenar o portfólio;
- armazenar posições;
- controlar quantidades dos ativos;
- controlar preço médio;
- controlar saldo;
- reagir às ordens executadas;
- publicar `portfolio.updated`.

Uma venda rejeitada ou uma ordem ainda não executada não deve alterar o portfólio como se a negociação tivesse sido concluída.

---

## 5. WebSocket Gateway

Serviço responsável por entregar atualizações assíncronas para o frontend.

### Responsabilidades

- consumir eventos Kafka relevantes;
- manter comunicação WebSocket com o Angular;
- encaminhar atualizações para a interface.

Ele funciona como uma ponte:

---

# Estrutura conceitual dos serviços Spring

Mesmo que cada serviço tenha arquivos diferentes, a organização segue responsabilidades parecidas.

## `/controller`

- Porta de entrada HTTP.
- Recebe requisições.
- Extrai os dados necessários.
- Chama a camada de serviço.
- Retorna uma resposta.

O Controller não deve concentrar regra de negócio.

---

## `/service`

- Onde fica a lógica principal daquele caso de uso.
- Coordena providers, repositories e producers quando necessário.

---

## `/dto`

- Objetos utilizados para transportar dados.
- Definem formatos de entrada e saída.
- Evitam expor diretamente entidades internas.

---

## `/entity`

- Objetos que representam informações persistidas no banco.
- São utilizados principalmente com JPA/Hibernate.

---

## `/repository`

- Camada de acesso ao banco.
- Utiliza Spring Data JPA.
- Faz operações como buscar, salvar e consultar entidades.

---

## `/producer`

- Componentes responsáveis por publicar eventos Kafka.

---

## `/consumer`

- Componentes responsáveis por consumir eventos Kafka.
- Reagem quando uma nova mensagem chega em determinado tópico.

---

## `/provider`

- Integrações com sistemas externos.
- No Market Data Service, é onde fica a comunicação com a Finnhub.

---

## `/config`

- Configurações específicas da aplicação.
- Pode conter configurações relacionadas a Kafka, WebSocket, clients HTTP ou outros componentes.

---

## `/resources`

Normalmente contém:

ou:

É onde ficam várias configurações da aplicação, como:

- porta;
- endereço do Kafka;
- endereço do Schema Registry;
- conexão com PostgreSQL;
- propriedades do Spring;
- configurações específicas do serviço.

---

# Arquivos importantes

## `pom.xml`

- Arquivo principal de configuração Maven de um serviço Java.
- Define dependências e plugins.

---

## `Dockerfile`

- Receita usada para construir a imagem Docker de um serviço.

---

## `compose.yml` / `docker-compose.yml`

- Define como os vários componentes do PulseDesk são executados juntos.

---

## `application.yml`

- Configuração de uma aplicação Spring Boot.

---

## `.avsc`

- Arquivo de schema do Apache Avro.
- Define o contrato de um evento.

---

## `package.json`

- Arquivo utilizado no frontend.
- Define dependências e scripts do projeto Angular/Node.

---

## `angular.json`

- Arquivo de configuração do workspace Angular.

---

# Separação entre comunicação síncrona e assíncrona

O PulseDesk utiliza os dois tipos.

## Comunicação síncrona

O usuário faz um pedido e espera uma resposta.

É usada quando existe uma relação direta de requisição e resposta.

---

## Comunicação assíncrona

O produtor publica o evento e não precisa esperar todos os consumidores terminarem o trabalho.

É utilizada principalmente para propagar acontecimentos do sistema.

---

# Por que não fazer tudo em um único backend?

Seria possível construir um MVP menor com apenas uma aplicação Spring Boot.

Mas o PulseDesk foi separado em serviços para explorar conceitos utilizados em sistemas distribuídos.

Essa separação permite estudar na prática:

- microsserviços;
- Event-Driven Architecture;
- producers e consumers;
- contratos de eventos;
- serialização;
- compatibilidade de schemas;
- comunicação síncrona e assíncrona;
- persistência;
- integração com APIs externas;
- atualizações em tempo real;
- interoperabilidade entre aplicações financeiras.

A complexidade adicional, portanto, faz parte do objetivo técnico do projeto.

---

# Objetivo final

O objetivo do PulseDesk é juntar, em um único projeto, várias partes que aparecem em sistemas financeiros reais.

O usuário consegue interagir com uma interface de investimentos, consultar ativos, acompanhar informações de mercado, visualizar análises, simular operações e acompanhar o próprio portfólio.

Por trás da interface, o sistema é dividido em serviços especializados.

O Market Data Service busca informações externas.

O Analytics Service transforma market data em informações derivadas.

O Trading Service controla o fluxo das ordens.

O Portfolio Service mantém o estado financeiro do usuário.

O WebSocket Gateway transforma eventos internos em atualizações em tempo real para a interface.

Kafka conecta esses serviços através de eventos.

Avro e Schema Registry garantem que esses eventos possuam contratos bem definidos.

PostgreSQL mantém as informações persistentes.

Angular constrói a interface utilizada pelo usuário.

FDC3 adiciona interoperabilidade entre aplicações financeiras.

Mais do que uma aplicação para comprar e vender ações, o PulseDesk é uma forma de estudar como diferentes tecnologias podem trabalhar juntas dentro de uma **arquitetura financeira distribuída e orientada a eventos**.
