# Plataforma de Reservas
Aplicação Java para administrar reservas de recursos em um período,
desenvolvida na disciplina de Programação Orientada a Objetos (Fagammon, 2026/2).

## Equipe

- Integrante 1: Victor Henrique Rezende Andrade
- Integrante 2: Arthur Emanuel de Sales Naves

## Github
- Link Repositório: https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras

## Problema

- Quadras esportivas são recursos disputados: duas pessoas não podem usar a mesma
  quadra no mesmo horário, e nem toda quadra comporta toda modalidade. O sistema
  cadastra participantes e quadras, e registra reservas respeitando essas regras.
- 
## Variante

- Variante: Quadras Esportivas
- Regra específica: Compatibilidade de modalidade - cada quadra possui uma lista de modalidades permitidas; o sistema recusa reservas com modalidade incompatível.


## Escopo

**Incluído no checkpoint 1:**

- Entidades `Participante`, `Quadra` e `Reserva`, com identidade por id.
- Objeto de valor imutável `PeriodoReserva` (record), com validação de início/fim
  e detecção de sobreposição (`sobrepoe`).
- Enums `Modalidade` e `StatusReserva`.
- Invariantes validadas nos construtores (dados obrigatórios, período válido,
  quadra ativa, modalidade compatível).
- Testes automatizados de unidade e build reproduzível com Maven Wrapper.

## Diagrama de classes

![Diagrama de classes](docs/diagrama_de_classes_checkpoint1.svg)


## Requisitos atendidos (checkpoint 1)

- RF1 : cadastro e validação de Participante.
- RF2 : cadastro, ativação/desativação de Quadra.
- RF3 : Período como objeto de valor imutável, com validação de início/fim.
- RF4 : Reserva recusa quadra inativa e modalidade incompatível;
  detecção de sobreposição entre reservas fica para o checkpoint 2.
- Regra própria da variante: compatibilidade de modalidade implementada e testada.

## Pré-requisitos

- JDK 25 (LTS)
- Não é necessário instalar Maven: o projeto usa o Maven Wrapper.

## Como executar

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```


## Documentação

- [Decisões do projeto](docs/decisoes.md)
- [Uso de IA](docs/IA_USAGE.md)

## Entregas

## CheckPoint 1
- Data: 16/10
- url commit: https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras/commit/968b873d5bb9386d69ec47963f888fc4af754479
- branch: master
- hash: 968b873d5bb9386d69ec47963f888fc4af754479
