# Sistema de Reservas de Quadras Esportivas

Sistema desenvolvido em **Java 25** para gerenciar reservas de quadras esportivas, prevenindo conflitos de horários, validando modalidades permitidas e aplicando regras de cancelamento e precificação. O projeto foi realizado na disciplina de **Programação Orientada a Objetos**, da **Fagammon**, no segundo semestre de 2026, como parte do projeto incremental *Plataforma de Reservas*.

> **Estado do projeto:** implementação dos recursos centrais, políticas de preço e cancelamento, consultas e persistência JSON; evolução e documentação em andamento para a entrega final. A classe `Aplicacao` disponibiliza uma demonstração no console, e não um menu interativo.

## Equipe

- **Victor Henrique Rezende Andrade**
- **Arthur Emanuel de Sales Naves**

**Repositório:** [Projeto-incremental-Reserva-de-quadras](https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras)

## 1. Problema e objetivo

Uma quadra não pode receber duas reservas conflitantes para o mesmo período. Além disso, cada quadra pode aceitar apenas determinadas modalidades, pode estar ativa ou inativa e precisa ter suas reservas consultadas e preservadas entre execuções.

O objetivo é aplicar **encapsulamento, composição, polimorfismo, objetos de valor, coleções, genéricos, exceções, testes automatizados e separação de responsabilidades** a um problema realista de agendamento. A solução é executada localmente, sem interface gráfica, aplicação web ou banco de dados.

### Variante e regra própria

A variante escolhida é **Quadras Esportivas**. Uma regra específica do domínio é a **compatibilidade de modalidade**: uma reserva só pode ser criada quando a modalidade solicitada pertence ao conjunto de modalidades permitidas da quadra.

Modalidades previstas no código: `FUTSAL`, `VOLEIBOL`, `BASQUETE`, `TENIS`, `HANDEBOL` e `SOCIETY`.

## 2. Funcionalidades implementadas

- Cadastro em memória e consulta de **participantes** e **quadras** por meio de repositório genérico, com rejeição de identificadores duplicados.
- Ativação e desativação de quadras, impedindo novas reservas em quadras inativas.
- Representação imutável dos períodos por `PeriodoReserva`, com validação de início e fim e detecção de sobreposição.
- Criação de reservas com identificação própria, participante, quadra, período, modalidade, política de cancelamento e preço calculado.
- Rejeição de reservas com quadra inativa, modalidade incompatível ou conflito com outra reserva não cancelada na mesma quadra.
- Ciclo de vida com estados `CONFIRMADA`, `CANCELADA` e `CONCLUIDA`, bloqueando transições inválidas.
- Duas políticas intercambiáveis de cancelamento: **livre** e **antecedência mínima**, configurada por padrão em 24 horas.
- Duas políticas intercambiáveis de preço: **valor fixo por hora** e **valor por hora conforme a modalidade**, ambas com `BigDecimal`.
- Consultas por identificador, quadra, participante, status e período, além de listagem cronológica e verificação de disponibilidade.
- Salvamento e restauração de reservas em **JSON**, com reconstrução de participantes e quadras associados, preço, status e políticas de cancelamento suportadas.
- Testes automatizados com JUnit e uma aplicação de demonstração no console.

**Importante:** o sistema **calcula o valor** das reservas; não realiza cobrança, integração com meios de pagamento ou emissão de comprovantes. O preço calculado fica registrado na reserva. A política de preço utilizada para o cálculo não é reconstruída como objeto durante o carregamento: o valor resultante é preservado.

## 3. Regras de negócio

| Regra | Comportamento |
| --- | --- |
| Período válido | O início deve ser anterior ao fim. |
| Identidade | Participantes, quadras e reservas possuem identificadores; o repositório impede IDs duplicados. |
| Quadra ativa | Quadras desativadas não recebem novas reservas. |
| Modalidade | A modalidade solicitada deve ser permitida na quadra. |
| Conflito de horários | Reservas não canceladas da mesma quadra não podem ter períodos sobrepostos. |
| Intervalos adjacentes | Um agendamento que termina exatamente quando o seguinte começa não gera conflito: `[início, fim)`. |
| Cancelamento | Apenas reservas confirmadas podem ser canceladas, respeitando a política escolhida. |
| Conclusão | Apenas reservas confirmadas podem ser concluídas. |
| Histórico | Reservas canceladas permanecem no histórico, mas deixam de bloquear o horário; reservas concluídas continuam contando na verificação de conflitos. |
| Preço | Calculado no momento da criação, com `BigDecimal` e arredondamento para duas casas decimais. |
| Restauração | Os dados carregados são reconstruídos e submetidos a validações do modelo, incluindo referências, duplicidades e conflitos. |

### Estratégias de cancelamento

- `CancelamentoLivre`: permite cancelar uma reserva confirmada sem antecedência mínima.
- `CancelamentoComAntecedencia`: exige, por padrão, **24 horas** de antecedência; o limite exato de 24 horas é aceito. A implementação permite configurar a duração e injetar `Clock`, facilitando testes determinísticos.

### Estratégias de preço

- `PrecoFixoPorHora`: aplica uma tarifa horária única ao tempo reservado.
- `PrecoPorModalidade`: utiliza uma tarifa horária específica para modalidades configuradas e uma tarifa padrão para as demais.

Por exemplo, uma reserva de 90 minutos com tarifa de **R$ 50,00/h** resulta em **R$ 75,00**. Esses valores são apenas ilustrativos; as tarifas são definidas na criação das estratégias.

## 4. Tecnologias

| Tecnologia | Utilização |
| --- | --- |
| Java 25 | Linguagem e API de data/hora (`LocalDateTime`, `Duration`, `Clock`). |
| Maven + Maven Wrapper | Compilação e execução reprodutível dos testes. |
| JUnit Jupiter 6.1.2 | Testes automatizados. |
| Jackson Databind 2.22.4 | Leitura e gravação de arquivos JSON. |
| `BigDecimal` | Cálculo de valores monetários. |
| Git e GitHub | Versionamento e colaboração. |

As versões acima correspondem ao `pom.xml` fornecido.

## 5. Estrutura do projeto

```text
Sistema-de-reservas/
├── .mvn/wrapper/                 # Configuração do Maven Wrapper
├── dados/
│   └── reservas.json            # Arquivo JSON de demonstração
├── docs/
│   ├── decisoes.md              # Decisões e alternativas de projeto
│   ├── evolucao.md              # Histórico e verificação da evolução
│   ├── diagrama_de_classes.md   # Diagrama atual (Checkpoint 3)
│   ├── diagrama_de_classes_checkpoint1.svg
│   ├── diagrama_de_classes_checkpoint2.md
│   └── IA_USAGE.md              # Registro de uso de IA
├── src/
│   ├── main/java/br/edu/fagammon/poo/
│   │   ├── dominio/             # Participante, Quadra, Reserva, período, enums e Aplicacao
│   │   ├── excecao/             # Exceções de negócio
│   │   ├── politica/            # Estratégias de cancelamento e preço
│   │   ├── repositorio/         # Contrato genérico e implementação em memória
│   │   ├── servico/             # GerenciadorReservas
│   │   └── persistencia/        # Conversão de dados e persistência JSON
│   └── test/java/br/edu/fagammon/poo/
│       └── ...                  # Testes de domínio, serviço, políticas e persistência
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

### Principais componentes

| Componente | Responsabilidade |
| --- | --- |
| `Participante` | Identificar o responsável pela reserva e validar seus dados. |
| `Quadra` | Armazenar identificação, disponibilidade administrativa e modalidades aceitas. |
| `PeriodoReserva` | Representar um intervalo de datas/horas imutável e detectar sobreposições. |
| `Reserva` | Reunir os elementos da reserva, seu valor e estado, protegendo transições. |
| `GerenciadorReservas` | Criar, localizar, filtrar, cancelar e concluir reservas, além de prevenir conflitos. |
| `Repositorio<T extends Identificavel>` | Definir operações genéricas de armazenamento e consulta. |
| `RepositorioEmMemoria` | Implementar o contrato de repositório usando memória. |
| `PoliticaCancelamento` e `PoliticaPreco` | Definir contratos para regras variáveis. |
| `ServicoPersistencia` | Orquestrar a gravação e a restauração de dados. |
| `ConversorDadosSistema` | Converter entre objetos de domínio e representações serializáveis. |
| `DadosSistema` | Estruturar os dados transferidos para/de JSON. |
| `PersistenciaJson` | Conter os detalhes de arquivo e serialização com Jackson. |

O projeto utiliza o padrão **Strategy** nas políticas de cancelamento e de preço: o gerenciador e as reservas dependem de contratos, não de verificações espalhadas sobre cada implementação. Para mais detalhes sobre as escolhas e seus custos, consulte [decisoes.md](docs/decisoes.md).

**Diagrama de classes atualizado:** [docs/diagrama_de_classes.md](docs/diagrama_de_classes.md).

## 6. Pré-requisitos

- **JDK 25** instalado e configurado no `PATH` (ou por `JAVA_HOME`).
- Terminal ou PowerShell para executar os comandos.
- Conexão com a internet na primeira execução, caso seja necessário baixar Maven e dependências.

Não é necessário instalar o Maven separadamente: o repositório inclui o **Maven Wrapper**.

Confirme o Java instalado:

```bash
java -version
```

## 7. Como instalar e executar

Clone o repositório:

```bash
git clone https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras.git
cd Projeto-incremental-Reserva-de-quadras
```

### Executar os testes

**Linux/macOS:**

```bash
./mvnw test
```

**Windows (PowerShell):**

```powershell
.\mvnw.cmd test
```

### Compilar e executar a demonstração

**Linux/macOS:**

```bash
./mvnw compile
java -cp target/classes br.edu.fagammon.poo.dominio.Aplicacao
```

**Windows (PowerShell):**

```powershell
.\mvnw.cmd compile
java -cp target/classes br.edu.fagammon.poo.dominio.Aplicacao
```

A classe `Aplicacao` executa um cenário de demonstração: cadastra participantes e quadras, cria reservas, tenta criar uma reserva conflitante, salva o estado em JSON e recupera os dados em outra instância do gerenciador. A execução usa datas relativas ao dia atual e **sobrescreve** o arquivo `dados/reservas.json`.

Para executar uma verificação mais ampla do projeto:

```bash
./mvnw verify
```

No Windows, substitua `./mvnw` por `.\mvnw.cmd`. Os relatórios da suíte podem ser consultados em `target/surefire-reports/`.

## 8. Exemplo de utilização da API

```java
import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.politica.*;
import br.edu.fagammon.poo.servico.GerenciadorReservas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;

Participante participante = new Participante("P01", "Pessoa Exemplo");
Quadra quadra = new Quadra("Q01", "Quadra Central", EnumSet.of(Modalidade.FUTSAL));

PeriodoReserva periodo = new PeriodoReserva(
    LocalDateTime.of(2026, 12, 10, 14, 0),
    LocalDateTime.of(2026, 12, 10, 15, 30)
);

GerenciadorReservas gerenciador = new GerenciadorReservas();
Reserva reserva = gerenciador.criarReserva(
    participante,
    quadra,
    periodo,
    Modalidade.FUTSAL,
    new CancelamentoLivre(),
    new PrecoFixoPorHora(new BigDecimal("50.00"))
);

System.out.println("ID: " + reserva.getId());
System.out.println("Preço: R$ " + reserva.getPreco()); // 75.00
System.out.println("Disponível? " + gerenciador.estaDisponivel(quadra, periodo, Modalidade.FUTSAL)); // false
```

O exemplo ilustra o uso das classes. Para detectar conflitos e preservar as regras da agenda, as reservas devem ser criadas pelo **`GerenciadorReservas`**.
Existe um exemplo pronto na classe [Aplicacao](src/main/java/br/edu/fagammon/poo/dominio/Aplicacao.java)
## 9. Persistência em JSON

Os dados são gravados em arquivo aberto e inspecionável, por meio de `ServicoPersistencia`. A aplicação de demonstração utiliza `dados/reservas.json`.

```java
import br.edu.fagammon.poo.persistencia.ServicoPersistencia;
import br.edu.fagammon.poo.servico.GerenciadorReservas;
import java.nio.file.Path;

ServicoPersistencia servico = new ServicoPersistencia();
Path arquivo = Path.of("dados", "reservas.json");

// 'gerenciador' representa uma instância com reservas cadastradas.
servico.salvar(gerenciador, arquivo);

// Recuperação em uma nova instância:
GerenciadorReservas restaurado = servico.carregar(arquivo);
System.out.println(restaurado.getReservas().size());
```

O JSON contém participantes, quadras e reservas referenciados por ID. Ao carregar, o sistema recria os objetos e valida informações essenciais, incluindo modalidades, períodos, referências, IDs duplicados e conflitos entre reservas não canceladas. Os estados e preços armazenados são preservados.

**Limite atual:** o método de salvamento recebe as reservas do gerenciador; portanto, **participantes e quadras sem reservas associadas não são incluídos no JSON**. Não se trata de um cadastro persistente independente desses recursos. As políticas de cancelamento reconhecidas pela conversão são `CancelamentoLivre` e `CancelamentoComAntecedencia`.

## 10. Testes automatizados

A suíte está organizada por responsabilidade e inclui testes para:

- Validação de participantes, quadras e períodos.
- Criação e regras de transição de reservas.
- Identificação de conflitos, consultas, ordenação e disponibilidade.
- Cancelamento livre e com antecedência mínima.
- Cálculo de preço fixo por hora.
- Repositório genérico em memória.
- Conversão dos dados e serviço de persistência.
- Cenários de evolução e regressão do gerenciador.

A presença dos arquivos de teste foi verificada no projeto enviado; **não foi possível executar a suíte neste ambiente com Java 25**, portanto não é declarado aqui um total de testes aprovados.

## 11. Requisitos do projeto

Esta tabela relaciona o enunciado geral às funcionalidades observadas no código enviado.

| Requisito | Situação no código | Evidência / observação |
| --- | --- | --- |
| RF1 — Participantes | Implementado em memória | `Participante`, `RepositorioEmMemoria`. |
| RF2 — Recursos | Implementado em memória | `Quadra`, ativação/desativação e repositório. |
| RF3 — Períodos | Implementado | `PeriodoReserva` (`record`) e `sobrepoe`. |
| RF4 — Reservas e conflitos | Implementado | `Reserva`, `GerenciadorReservas`. |
| RF5 — Ciclo de vida | Implementado | `StatusReserva`, `cancelar` e `concluir`. |
| RF6 — Duas políticas de cancelamento | Implementado | `CancelamentoLivre`, `CancelamentoComAntecedencia`. |
| RF7 — Duas políticas de preço | Implementado | `PrecoFixoPorHora`, `PrecoPorModalidade`, `BigDecimal`. |
| RF8 — Consultas e ordenação | Implementado | Filtros com streams e ordenação cronológica. |
| RF9 — Persistência | Implementado para dados associados às reservas | `ServicoPersistencia`, `ConversorDadosSistema`, `PersistenciaJson`; observar o limite da seção 9. |


O **Checkpoint 3** enfatiza persistência integrada, restauração validada, abstrações justificadas, testes de ida e volta/limites/regressão e separação entre domínio e infraestrutura. O projeto possui componentes específicos para esses objetivos; a demonstração e a execução dos testes devem ser verificadas na versão/commit submetidos.

## 12. Documentação complementar

- [Diagrama de classes — Checkpoint 3](docs/diagrama_de_classes.md)
- [Decisões de projeto](docs/decisoes.md)
- [Evolução e verificação](docs/evolucao.md)
- [Uso de inteligência artificial](docs/IA_USAGE.md)
- [Diagrama histórico — Checkpoint 1](docs/diagrama_de_classes_checkpoint1.svg)
- [Diagrama histórico — Checkpoint 2](docs/diagrama_de_classes_checkpoint2.md)

## 13. Marcos acadêmicos

| Marco | Data de referência do enunciado | Foco |
| --- | --- | --- |
| Checkpoint 1 | 06/10/2026 | Modelagem, invariantes, testes iniciais e build. |
| Checkpoint 2 | 27/10/2026 | Evolução do núcleo, polimorfismo, genéricos e consultas. |
| Checkpoint 3 | 10/11/2026 | Persistência, padrão/refatoração, limites e regressão. |
| Entrega e demonstração final | 24/11/2026 | Integração, documentação, testes e demonstração individual. |

### Referências de entregas registradas pela equipe

- **Checkpoint 1:** [commit `968b873`](https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras/commit/968b873d5bb9386d69ec47963f888fc4af754479) — branch `master` (data registrada no README anterior: 16/10/2026).
- **Checkpoint 2:** [commit `bcef819`](https://github.com/victorrezende7/Projeto-incremental-Reserva-de-quadras/commit/bcef819c9c4542da963e77a75b96b12702f84daa) — branch `master` (data registrada no README anterior: 31/10/2026).
- **Checkpoint 3:** registrar o **hash do commit efetivamente submetido** e a branch após a entrega.

As datas da tabela são as datas de referência dos **enunciados**, enquanto as datas dos dois commits acima foram transcritas do README incluído no ZIP.

## 14. Limitações e próximos passos

- Implementar um **relatório consolidado** útil para quadras esportivas (por exemplo, quantidade de reservas e faturamento previsto por quadra ou modalidade).
- Avaliar a persistência independente de participantes e quadras que ainda não possuam reservas.
- Validar a execução integral dos testes e o cenário de demonstração com JDK 25 antes da entrega.
- Conferir se as decisões de arquitetura, os testes de regressão e o diagrama documentam a versão final submetida.

---

**Projeto acadêmico — Programação Orientada a Objetos | Fagammon | 2026/2.**
