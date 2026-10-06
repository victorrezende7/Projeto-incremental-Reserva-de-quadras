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


  

