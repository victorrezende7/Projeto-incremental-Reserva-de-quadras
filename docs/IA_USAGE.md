# Registro de uso de inteligência artificial

Registre usos materiais de IA que tenham influenciado código, testes,
documentação ou decisões.

## Modelo de registro

### Uso

- Data:
- Ferramenta:
- Objetivo:
- Parte do projeto afetada:
- Resultado aproveitado:
- Verificações executadas:
- Correções ou decisões da equipe:


### Uso
- Data: 21/09/2026
- Ferramenta: Claude (Anthropic)
- Objetivo: apoiar a definição da variante do projeto, da regra própria da variante, do
  escopo inicial e da estrutura de pastas/arquivos do projeto Maven.
- Parte do projeto afetada: ficha de confirmação da variante, README.md, docs/decisoes.md.
- Verificações executadas: conferência manual de cada campo preenchido contra o enunciado
  (RF1–RF10, invariantes) e contra o template já fornecido pelo professor; nenhuma linha
  de código de domínio foi gerada nesta etapa.
- Correções ou decisões da equipe: adaptação do texto sugerido ao vocabulário do
  enunciado; decisão de manter apenas a compatibilidade de modalidade como regra própria
  inicial.

## Uso
- Data: 01/10/2026
- Ferramenta:  chat-gpt
- Objetivo: Entender a classe Enum e como usar Set.
- Parte do projeto afetada: Implementar na classe quadra para as modalidades permitidas

## Uso
- Data:01/10/2026
- Ferramenta: chat-gpt
- Objetivo: Usar UUID para gerar ids automaticos para reservas
- Parte do projeto afetada: Classe Reserva
- Resultado aproveitado: Ids automaticos ao criar uma classe Reserva
- Decisões da equipe: Utilizar Ids automáticos para identificar cada reserva.

## Uso

- Data: 03-04/10/2026
- Ferramenta: Claude e chat-gpt
- Objetivo: Gerar ideias para realizar testes.
- Parte do projeto afetada: Os testes de Participante, PeriodoReserva, Quadra, Reserva.
- Resultado aproveitado: Ideias de testes válidos e coeerentes com as validções das classes.

## Uso
- Data: 28/10/2026
- Ferramenta: ChatGPT (OpenAI).
- Objetivo:
  - Auxiliar na compreensão dos requisitos do Checkpoint 2.
  - Esclarecer conceitos de Streams, lambdas, Comparator e exceções.
  - Orientar a implementação das consultas
- Parte do projeto afetada:
  - Classe Reserva: validação das transições de status.
  - Classe GerenciadorReservas: consultas, ordenação e retorno da criação de reservas.
- Resultado aproveitado: Organização e possibilidade de realizar consultas com listas atraves da classe GerenciadorReservas
- Verificações executadas:As implementações foram analisadas e testadas manualmente na classe de aplicacao, utilizando diferentes reservas, participantes e quadras.
- Correções ou decisões da equipe: As sugestões foram avaliadas e adaptadas à estrutura existente. A equipe optou por utilizar Streams e lambdas nas consultas


### Uso

- Data: 30/10/2026
- Ferramenta: ChatGPT (OpenAI)
- Objetivo: Auxiliar na compreensão sobre interfaces e nas políticas de cancelamento
- Parte do projeto afetada: Interface PoliticaCancelamento, CancelamentoComAntecedencia, CancelamentoLivre
- Resultado aproveitado: Possibilidade de adicionar políticas de cancelamentos para determinada reserva, podendo ser livre ou com antecedência
- Verificações executadas: As implementações foram analisadas e testadas manualmente na classe de aplicacao e nos testes Junit
- Correções ou decisões da equipe: As sugestões foram avaliadas e decidimos adaptar para a estrutura do nosso projeto.  

### Implementação e verificação da evolução

- Data: 31/10/2026.
- Ferramenta: Codex (OpenAI).
- Objetivo: analisar os arquivos do projeto e implementar as lacunas dos cinco
  tópicos da atividade: modelo/invariantes; composição, polimorfismo e genéricos;
  requisitos/falhas; testes; processo/documentação.
- Parte do projeto afetada: domínio, políticas de cancelamento, gerenciador,
  aplicação de demonstração, novo pacote de repositórios, novo pacote de exceções,
  testes e documentação. A relação completa está em `docs/evolucao.md`.
- Resultado aproveitado: contrato `Identificavel`, repositório genérico em memória,
  exceções específicas compatíveis com as categorias anteriores, busca/transições
  por id, consulta por período, disponibilidade, validações de entradas e relógio
  injetável. Reservas concluídas passam a preservar a ocupação histórica; somente
  canceladas liberam o intervalo. Foram acrescentados testes de limite e integração.
- Verificações executadas:
  - Inspeção das fontes de produção e teste, configuração Maven, documentação e diagramas.
  - `mvnw.cmd -B -Dmaven.repo.local=target/m2 test`: 107 testes aprovados na primeira
    rodada completa, antes de acrescentar o teste de integração.
  - `mvnw.cmd -B -Dmaven.repo.local=target/m2 verify`: **108 testes executados,
    zero falhas, zero erros e zero ignorados**, incluindo o teste de integração;
    `BUILD SUCCESS` e JAR `target/plataforma-reservas-1.0.0-SNAPSHOT.jar` gerado.
  - Execução de `br.edu.fagammon.poo.dominio.Aplicacao`: conflito esperado tratado,
    três participantes, duas quadras, uma reserva concluída, duas canceladas e
    disponibilidade liberada no horário cancelado; encerramento com código 0.
  - `git diff --check`: sem erros de espaços em branco.
- Ambiente: o Java padrão era JRE 8. Foi preparado um Eclipse Temurin JDK 25
  portátil em `target/tools/`, com SHA256 conferido contra a API de distribuição.
  `JAVA_HOME` e `MAVEN_USER_HOME` foram definidos apenas para os processos de
  validação. Dependências e ferramentas ficaram em `target/`. A execução exigiu
  permissão de rede e repetição fora da restrição que impediu o compilador.
- Decisões aplicadas para revisão da equipe: manter APIs existentes e versões do
  build; não impor cadastro prévio nem novas restrições de data à criação/conclusão;
  documentar o escopo verificável sem afirmar cobertura do enunciado completo,
  ausente do repositório. Não foram realizados commit, push ou alterações na
  configuração do Git. A equipe ainda deve revisar as decisões acadêmicas antes
  da entrega, conforme seu processo normal.
