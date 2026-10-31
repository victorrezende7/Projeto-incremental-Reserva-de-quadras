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


## Escopo implementado

- Entidades `Participante`, `Quadra` e `Reserva`, com identidade estável por id.
- `PeriodoReserva` imutável, com início anterior ao fim e sobreposição de intervalos.
- Cadastro genérico `Repositorio<T extends Identificavel>`, implementado em memória
  e reutilizado para participantes, quadras e reservas. IDs duplicados são recusados.
- Reserva de quadra ativa e compatível com a modalidade, sem conflitos de horário.
- Cancelamento e conclusão com transições de estado protegidas, inclusive por id.
- Composição de `Reserva` com uma `PoliticaCancelamento`: cancelamento livre ou
  antecedência mínima (24 horas por padrão), sem condicionais por tipo de política.
- Consultas por quadra, participante, status e período, ordenação cronológica e
  verificação de disponibilidade. As listas retornadas não permitem alteração estrutural.
- Exceções específicas, testes automatizados e demonstração executável no console.

O armazenamento é em memória, para uso local em uma única thread. Persistência,
interface gráfica, autenticação e cobrança não fazem parte desta evolução.

## Diagrama de classes

[Diagrama atual e transições de estado](docs/diagrama_de_classes.md).

O [diagrama do checkpoint 1](docs/diagrama_de_classes_checkpoint1.svg) foi
preservado como registro histórico.


## Requisitos e evolução

- RF1 : cadastro e validação de Participante.
- RF2 : cadastro, ativação/desativação de Quadra.
- RF3 : Período como objeto de valor imutável, com validação de início/fim.
- RF4 : Reserva recusa quadra inativa, modalidade incompatível e sobreposição de horário.
- Regra própria da variante: compatibilidade de modalidade implementada e testada.

| Tópico da atividade | Implementação e evidência |
| --- | --- |
| Evolução do modelo e das invariantes | Identidade comum, períodos imutáveis, transições protegidas, ocupação preservada no histórico e cópias defensivas |
| Composição, polimorfismo e genéricos | Estratégias `PoliticaCancelamento`, `Clock` injetável e repositório limitado por `Identificavel` |
| Requisitos e tratamento de falhas | Exceções para duplicidade, ausência, conflito, transição inválida e cancelamento recusado; falhas preservam o estado |
| Testes da evolução | Testes de unidade, parametrizados e de integração, incluindo limite de 24 horas e sobreposições parciais |
| Processo e documentação | Matriz de requisitos, decisões, diagrama, registro de IA e instruções reproduzíveis |

Consulte a [análise das lacunas e matriz de verificação](docs/evolucao.md).
O enunciado completo de RF1–RF10 não está no repositório; a matriz descreve o
escopo verificável a partir do código, da documentação e dos cinco tópicos fornecidos.

## Regras de uso

- Intervalos são tratados como `[início, fim)`: 14h–15h e 15h–16h são compatíveis.
- Reservas confirmadas e concluídas impedem sobreposição na mesma quadra, mesmo
  com outra instância de `Quadra` que possua o mesmo id. Cancelar libera o horário
  e preserva a reserva no histórico.
- Somente `CONFIRMADA → CANCELADA` e `CONFIRMADA → CONCLUIDA` são permitidas.
  Concluir é uma operação administrativa; não há restrição temporal adicional.
- O cancelamento com antecedência permite exatamente 24 horas; abaixo desse
  limite a reserva continua confirmada. O construtor aceita `Duration` e `Clock`
  para configurar a regra e testar seus limites sem esperar o tempo passar.
- Desativar uma quadra impede novas reservas e não altera as existentes.
- Crie reservas pelo `GerenciadorReservas` para validar conflitos. O construtor
  de `Reserva` valida apenas as invariantes locais, pois não conhece a agenda.
- Para cadastros, utilize o objeto retornado pelo repositório como referência
  da entidade. Repositórios distintos são independentes e não substituem um banco de dados.

## Pré-requisitos

- JDK 25 (LTS)
- Não é necessário instalar Maven: o projeto usa o Maven Wrapper.
- `JAVA_HOME` deve apontar para o JDK 25 (um JRE 8 não contém o compilador).
- A primeira execução precisa de rede para baixar Maven e dependências.

## Como executar

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

Para validar o build completo:

```powershell
.\mvnw.cmd verify
```

Para executar a demonstração após compilar:

```powershell
.\mvnw.cmd compile
& "$env:JAVA_HOME\bin\java.exe" -cp target/classes br.edu.fagammon.poo.dominio.Aplicacao
```

Em Linux/macOS, após `./mvnw compile`:

```bash
java -cp target/classes br.edu.fagammon.poo.dominio.Aplicacao
```

A demonstração cadastra participantes e quadras, cria reservas, trata um conflito,
conclui e cancela reservas e mostra a disponibilidade e o histórico ordenado.
Relatórios de teste são gerados em `target/surefire-reports/`.

## Documentação

- [Decisões do projeto](docs/decisoes.md)
- [Uso de IA](docs/IA_USAGE.md)
- [Evolução e verificação dos requisitos](docs/evolucao.md)
- [Diagrama atual](docs/diagrama_de_classes.md)

## Entregas

## CheckPoint 1
- Data: 16/10
- url commit: https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras/commit/968b873d5bb9386d69ec47963f888fc4af754479
- branch: master
- hash: 968b873d5bb9386d69ec47963f888fc4af754479
