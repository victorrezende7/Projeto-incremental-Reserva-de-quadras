# Decisões do projeto

Registre decisões que afetem o modelo, a API, as dependências ou a evolução.

## Modelo de registro

### Título

- Data: 
- Problema observado: 
- Alternativas consideradas:
- Decisão:
- Consequências:
- Teste ou evidência que verifica a decisão:


### Escolha da variante e regra própria

- Data: 07/09/2026
- Problema observado: era necessário confirmar uma variante do domínio de reservas e
  definir uma regra própria não coberta pelos requisitos comuns.

- Alternativas consideradas: Salas e laboratórios, Quadras esportivas, Equipamentos,
  Serviços e atendimentos, Espaços para eventos.

- Decisão: adotar a variante Quadras esportivas, com a regra própria de compatibilidade
  de modalidade — cada quadra possui uma lista de modalidades permitidas; reservas com
  modalidade incompatível são recusadas.

- Consequências: a classe Quadra precisará manter uma coleção de modalidades permitidas;
  a criação de reserva deverá validar essa compatibilidade antes de aceitar o pedido.

- Teste ou evidência que verifica a decisão: teste de aceitação de reserva com modalidade
  compatível e teste de rejeição de reserva com modalidade incompatível.

### Modalidades como enum e Set na Quadra, com cópia defensiva

- Data: 01/10/2026
- Problema observado: a regra própria exige que cada quadra tenha um conjunto
  de modalidades permitidas, sem repetição, e que ninguém de fora possa
  alterá-lo e burlar a regra.
- Alternativas consideradas: `String` para a modalidade; `List<Modalidade>`;
  classe `Modalidade` comum; `Set` recebido do chamador e guardado diretamente.
- Decisão: `Modalidade` como `enum` (conjunto fixo e conhecido) e `Set`
  na `Quadra`, copiado na entrada (`EnumSet.copyOf`) e na saída
  (`getModalidadesPermitidas`).
- Consequências: valores inválidos não compilam e não há modalidades repetidas.
  Alterar o conjunto original depois de criar a quadra não afeta a quadra.
  Adicionar uma nova modalidade exige alterar o enum. Isso é aceitável porque
  o conjunto é pequeno e estável; se virasse dinâmico, seria preciso
  reavaliar. Observação: `EnumSet.copyOf` lança exceção para coleção vazia
  que não seja um `EnumSet`; hoje isso é coberto pela validação anterior.
- Teste ou evidência que verifica a decisão: `QuadraTest`
  (`getModalidadesPermitidasDeveRetornarCopia`,
  `deveRecusarQuadraSemModalidades`, `deveAceitarModalidadePermitida`).

### PeriodoReserva como record imutável

- Data: 03/10/2026
- Problema observado: o intervalo de uma reserva (início e fim) precisa ser
  validado sempre que existe, e não pode mudar depois de criado, senão uma
  reserva já aceita poderia ter o horário alterado sem nova validação.
- Alternativas consideradas: classe comum com getters e setters; classe final
  com campos privados e equals/hashCode escritos à mão; dois atributos
  `LocalDateTime` soltos dentro de `Reserva`.
- Decisão: usar um `record` com construtor compacto que valida que início e fim
  não são nulos e que o início é anterior ao fim.
- Consequências: o objeto nunca existe em estado inválido e é imutável. Igualdade
  e hashCode por valor vêm de graça: dois períodos com os mesmos horários são
  iguais. O record não pode ser alterado, então mudar o horário exige criar um
  novo período.
- Teste ou evidência que verifica a decisão: `PeriodoReservaTest`.


### Identidade das entidades e igualdade por valor

- Data: 03/10/2026
- Problema observado: era necessário decidir quando dois objetos são "o mesmo"
  para `Participante`, `Quadra` e `Reserva` (entidades) e para `PeriodoReserva`
  (valor).
- Alternativas consideradas: igualdade por todos os campos nas entidades;
  não sobrescrever `equals` e usar a identidade de memória.
- Decisão: entidades comparam apenas pelo identificador (`equals` e `hashCode`
  baseados no id); o `PeriodoReserva` compara por valor, por ser record.
- Consequências: um participante que muda de nome continua sendo o mesmo
  participante. Duas entidades com o mesmo id são consideradas a mesma, então
  a unicidade dos ids precisa ser garantida por quem cadastra.
- Teste ou evidência que verifica a decisão: `ParticipanteTest`
  (`participantesComMesmoIdDevemSerIguais`), `QuadraTest`
  (`quadrasComMesmoIdDevemSerIguais`), `ReservaTest`
  (`duasReservasDiferentesNaoDevemSerIguais`)

### Identificador da Reserva gerado por UUID

- Data: 03/10/2026
- Problema observado: reservas precisam de um identificador estável e único, e
  não há um dado natural que sirva (a mesma pessoa pode reservar a mesma quadra
  no mesmo horário em dias diferentes, por exemplo).
- Alternativas consideradas: id informado pelo chamador (como em `Participante`
  e `Quadra`); contador sequencial estático.
- Decisão: gerar o id no construtor com `UUID.randomUUID()`.
- Consequências: não há risco de duas reservas receberem o mesmo id nem de o
  chamador esquecer de informá-lo. Contador estático foi descartado porque
  complica os testes e a restauração de dados. Como o id é gerado dentro do
  construtor, ao implementar a persistência (checkpoint 3) será preciso
  permitir restaurar uma reserva com o id original, por exemplo com um
  construtor ou fábrica própria para restauração.
- Teste ou evidência que verifica a decisão: `ReservaTest`
  (`duasReservasDiferentesNaoDevemSerIguais`).


### Gerenciador de Reservas atraves da classe GerenciadorReservas

- Data: 26/10
- Problema observado: Reservas soltas no codigo
- Alternativas consideradas: Criar um array para armazenar as reservas ou uma lista
- Decisão: Utilizar um arraylist para armazenar as reservas
- Consequências: Mais organização e facilidade para operações envolvendo as reservas.
- Teste ou evidência que verifica a decisão: `GerenciadorReservasTest`


### Consultas e ordenação

- Data: 28/10/2026
- Problema observado: Ausência de mecanismos para consultar reservas por critérios específicos e ordená-las cronologicamente.
- Alternativas consideradas: Utilizar laços tradicionais, como for, ou a API Stream do Java.
- Decisão: Utilizar Streams e expressões lambda para filtrar reservas por quadra, participante e status, além de Comparator.comparing() para ordenar pelo início do período.
- Consequências: Código mais conciso, legível e facilidade para adicionar novas consultas.
- Teste ou evidência que verifica a decisão: inicialmente verificação manual na classe Aplicacao;
  a evolução de 31/10 inclui consultas, ordenação estável, cópias e filtros em `GerenciadorReservasTest`.


### Políticas de cancelamento

- Data: 30/10/2026
- Problema observado: Reservas podem ser canceladas sem ter nenhuma política de cancelamento.
- Alternativas consideradas: Criar uma interface para implementar 2 políticas.
- Decisão: Criamos uma interface onde implementamos uma política de cancelamento livre e uma com antecedência de 24 horas para reservas específicas.
- Consequências: Facilidade para implementar 2 tipos de cancelamento diferentes usando uma classe de interface como base;
- Teste ou evidência que verifica a decisão: Testes realizados nas classes GerenciadorReservasTest, CancelamentoComAntecedenciaTest, e testes manuais na classe de aplicação.

### Identidade comum e repositório genérico em memória

- Data: 31/10/2026.
- Problema observado: a identidade estava repetida nas entidades e não havia
  abstração própria com genéricos nem cadastro com proteção contra ids repetidos.
- Alternativas consideradas: listas separadas por entidade; classe base com
  estado compartilhado; interface de identidade e repositório genérico.
- Decisão: `Identificavel` define `getId()` e `Repositorio<T extends Identificavel>`
  define inclusão, consulta opcional/obrigatória e listagem. `RepositorioEmMemoria`
  usa `LinkedHashMap` para unicidade e ordem de inclusão. Substitui a lista interna
  da agenda; participantes e quadras usam a mesma implementação na aplicação.
- Consequências: não há casts ou repositórios duplicados por tipo. Inserções
  duplicadas falham sem substituir o objeto original. As listas são cópias
  estruturais não modificáveis; as entidades preservam suas operações de domínio.
  A implementação é local e não promete persistência ou acesso concorrente.
- Teste ou evidência: `RepositorioEmMemoriaTest`, `GerenciadorReservasTest`,
  `EvolucaoReservasTest` e demonstração em `Aplicacao`.

### Preservar ocupação das reservas concluídas

- Data: 31/10/2026.
- Problema observado: o conflito considerava apenas `CONFIRMADA`. Concluir uma
  reserva permitia cadastrar outra no mesmo intervalo, contradizendo o histórico
  de uso da mesma quadra.
- Alternativas consideradas: manter o comportamento; proibir toda reserva passada;
  desconsiderar somente as canceladas na verificação de conflito.
- Decisão: somente `CANCELADA` libera o intervalo. `CONFIRMADA` e `CONCLUIDA`
  bloqueiam sobreposição pela identidade da quadra. A consulta de disponibilidade
  usa o mesmo método de detecção de conflito da criação.
- Consequências: o histórico não admite usos duplicados após conclusão. Intervalos
  adjacentes continuam válidos. Não se acrescentou restrição temporal à criação
  ou à conclusão, preservando o contrato local anterior.
- Teste ou evidência: testes de reserva concluída, intervalos adjacentes, identidade
  da quadra, sobreposição parametrizada e fluxo de integração.

### Falhas de domínio específicas e preservação de estado

- Data: 31/10/2026.
- Problema observado: mensagens genéricas dificultavam distinguir conflito,
  duplicidade, ausência, transição inválida e política recusada. Política nula
  produzia `NullPointerException`, diferente das demais entradas obrigatórias.
- Alternativas consideradas: códigos de retorno; exceções checked; subclasses
  das exceções já utilizadas pelo projeto.
- Decisão: adotar cinco exceções específicas derivadas de `IllegalArgumentException`
  ou `IllegalStateException` e validar entradas obrigatórias com mensagem clara.
  Validar antes da inclusão na agenda ou alteração de status.
- Consequências: tratamentos antigos continuam capturando as mesmas categorias,
  e o chamador pode distinguir as falhas. Filtros nulos são recusados explicitamente;
  filtros válidos sem resultado retornam listas vazias.
- Teste ou evidência: testes de duplicidade, criação recusada, política recusada,
  ids ausentes, filtros nulos e transições em `ReservaTest` e `GerenciadorReservasTest`.

### Relógio e antecedência por composição

- Data: 31/10/2026.
- Problema observado: `LocalDateTime.now()` dentro da política impedia verificar
  de forma estável o limite inclusivo de 24 horas e os instantes vizinhos.
- Alternativas consideradas: esperar em testes; passar o horário em toda chamada;
  injetar `Clock` na estratégia existente.
- Decisão: manter o construtor sem argumentos (24 horas e relógio local) e adicionar
  construtores com `Clock` e com `Duration, Clock`. Antecedência negativa e relógio
  nulo são recusados. Zero permite cancelar no início, sem permitir o passado.
- Consequências: não há alteração na assinatura da estratégia nem na entidade para
  introduzir novas políticas. Testes usam relógio fixo e verificam 24 horas exatas,
  um nanossegundo antes/depois, horários passados e antecedência personalizada.
- Teste ou evidência: `CancelamentoComAntecedenciaTest`, `CancelamentoLivreTest`
  e teste de composição com lambda em `ReservaTest`.

### Processo de verificação da evolução

- Data: 31/10/2026.
- Decisão: manter o Maven Wrapper e as versões do `pom.xml`, documentar a matriz
  dos cinco tópicos e executar testes e demonstração antes da entrega.
- Consequências: o JRE 8 disponível inicialmente não compila o projeto Java 25;
  um JDK portátil e os caches necessários foram preparados em `target/`, sem
  alterar configurações globais ou arquivos do Git. A primeira compilação sob
  restrição de execução falhou no compilador e foi repetida com permissão.
- Evidência: relatórios em `target/surefire-reports/` e resultado final registrado
  em `docs/IA_USAGE.md`. Não foram realizados commit ou push nesta implementação.
