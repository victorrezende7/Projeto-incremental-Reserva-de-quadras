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
  compatível e teste de rejeição de reserva com modalidade incompatível (a implementar
  no checkpoint 1).
