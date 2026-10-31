# Evolução da plataforma de reservas

Data: 31/10/2026.

## Base da análise

Foram examinadas todas as classes de produção e de teste, `pom.xml`, a configuração
do Maven Wrapper, README, decisões, registro de IA e os diagramas existentes.
Os cinco tópicos informados orientam esta entrega. O enunciado completo mencionado
como RF1–RF10 no histórico não acompanha o projeto; portanto, os identificadores
E01–E10 abaixo são locais a este documento e não substituem a numeração do professor.

## O que existia e o que faltava

| Área | Situação encontrada | Evolução implementada |
| --- | --- | --- |
| Modelo | Entidades por id, período imutável e modalidade compatível | Contrato `Identificavel`, validação de elementos nulos e de política obrigatória |
| Agenda | Lista interna, conflitos apenas com confirmadas | Repositório genérico interno, conflito específico e ocupação histórica das concluídas |
| Ciclo de vida | Transições protegidas com exceção genérica | Exceção específica e operações por id, sem remover histórico |
| Composição/polimorfismo | Duas estratégias de cancelamento existentes | Contrato explícito, validação uniforme e relógio injetável |
| Genéricos | Apenas coleções padrão | Interface e implementação próprias, reutilizadas para três tipos de entidade |
| Consultas | Quadra, participante, status e ordenação | Período, disponibilidade, busca obrigatória/opcional por id e validação dos filtros |
| Testes | Casos básicos e testes de cancelamento dependentes do relógio real | Limites exatos, parametrização, falhas sem efeitos parciais e integração |
| Documentação | README ainda descrevia somente o checkpoint 1 | Escopo atual, matriz de requisitos, decisões e diagrama da evolução |

## Matriz de requisitos e testes

| ID local | Requisito/critério de aceitação | Implementação | Evidência automatizada |
| --- | --- | --- | --- |
| E01 | Cadastrar entidades com id obrigatório e único por repositório; não sobrescrever duplicadas | `Identificavel`, `Repositorio`, `RepositorioEmMemoria` | `RepositorioEmMemoriaTest`, `ParticipanteTest`, `QuadraTest` |
| E02 | Preservar modalidades e impedir novas reservas de quadra inativa/incompatível | `Quadra`, construtor de `Reserva`, `estaDisponivel` | `QuadraTest`, `ReservaTest`, `GerenciadorReservasTest` |
| E03 | Exigir início anterior ao fim; intervalos adjacentes não conflitam | `PeriodoReserva` | `PeriodoReservaTest`, testes de adjacência do gerenciador |
| E04 | Impedir sobreposição parcial, total ou idêntica na mesma quadra por id | `GerenciadorReservas.encontrarConflito` | Testes parametrizados de sobreposição e identidade da quadra |
| E05 | Cancelar libera horário; concluir preserva ocupação no histórico | `Reserva`, `GerenciadorReservas` | Testes de liberação e de reserva concluída |
| E06 | Permitir apenas cancelamento/conclusão de confirmadas e manter estado em falhas | `TransicaoReservaInvalidaException`, `Reserva` | `ReservaTest`, teste de política recusada no gerenciador |
| E07 | Aplicar estratégia composta sem decidir por tipo concreto; limite padrão de 24h inclusivo | `PoliticaCancelamento`, duas implementações, `Clock` | `CancelamentoLivreTest`, `CancelamentoComAntecedenciaTest`, estratégia lambda em `ReservaTest` |
| E08 | Consultar por quadra, participante, status, período e id; ordenar por início | Métodos de consulta do gerenciador | `GerenciadorReservasTest`, inclusive empates, ausência e filtros nulos |
| E09 | Expor listas sem permitir alteração estrutural; proteger cópias das modalidades | `List.copyOf`, `Stream.toList`, `EnumSet.copyOf` | Testes de cópia na entrada/saída e de consultas imutáveis |
| E10 | Demonstrar cadastro, falhas, transições e reuso do horário em um fluxo completo | `Aplicacao` e API pública | `EvolucaoReservasTest`, execução da aplicação no console |

## Contrato de falhas

| Condição | Resultado |
| --- | --- |
| Argumento obrigatório ausente, texto em branco, intervalo ou configuração inválida | `IllegalArgumentException` com mensagem |
| Id repetido no cadastro | `EntidadeDuplicadaException` (subtipo de `IllegalArgumentException`) |
| Busca obrigatória de id válido inexistente | `EntidadeNaoEncontradaException` (subtipo de `IllegalArgumentException`) |
| Consulta opcional de id válido inexistente | `Optional.empty()` |
| Reserva sobreposta | `ConflitoReservaException` com id da reserva conflitante (subtipo de `IllegalArgumentException`) |
| Cancelamento ou conclusão de estado terminal | `TransicaoReservaInvalidaException` (subtipo de `IllegalStateException`) |
| Antecedência insuficiente | `CancelamentoNaoPermitidoException` (subtipo de `IllegalStateException`) |
| Consulta com critério válido sem resultados | Lista vazia |
| Disponibilidade de quadra inativa, modalidade incompatível ou horário ocupado | `false` |

As subclasses mantêm compatibilidade com os tratamentos e testes antigos que
capturavam `IllegalArgumentException` ou `IllegalStateException`. Não se captura
uma exceção para ignorá-la: o domínio sinaliza a falha e a aplicação demonstra
o tratamento de um conflito esperado. Nenhuma criação recusada entra na agenda;
nenhuma transição recusada modifica o status.

## Escolhas de escopo

- Não foi introduzida uma regra de proibição de reservas no passado nem de
  conclusão apenas após o fim: isso alteraria contratos já utilizados nos testes.
- `CancelamentoLivre` mantém seu significado original: não restringe antecedência;
  os estados terminais continuam protegidos pela entidade `Reserva`.
- Os horários são `LocalDateTime`. O `Clock` define a hora local usada na política;
  não se adicionou suporte a reservas em múltiplos fusos.
- As listas são cópias estruturais, não cópias profundas. As entidades continuam
  mudando exclusivamente por operações de domínio, como `cancelar` e `desativar`.
- O gerenciador possui seu repositório de reservas e não o expõe para inserção
  externa. Criar uma entidade `Reserva` avulsa não a insere em uma agenda.
- Repositórios de participantes e quadras permitem cadastro explícito na aplicação.
  Não se passou a exigir cadastro prévio no gerenciador, preservando sua API.
- Não há persistência, controle concorrente, cobrança ou publicação nesta etapa.

## Reprodução da validação

Com `JAVA_HOME` apontando para um JDK 25:

```powershell
.\mvnw.cmd test
.\mvnw.cmd verify
& "$env:JAVA_HOME\bin\java.exe" -cp target/classes br.edu.fagammon.poo.dominio.Aplicacao
```

No ambiente desta implementação, o Java padrão era um JRE 8. Foi necessário
baixar um JDK 25 portátil da distribuição Eclipse Temurin, verificar o SHA256
informado pela API de distribuição e extraí-lo em `target/tools/`. O JDK e os
caches de Maven/dependências ficaram em `target/`, já ignorado pelo Git. Nenhuma
versão do `pom.xml` nem configuração global de Java ou Git foi alterada.

Os resultados são registrados nos relatórios `target/surefire-reports/` e no
registro de uso de IA após a execução. Como `target/` é descartável, o JDK local
precisa ser preparado novamente se essa pasta for apagada.

Para reutilizar as ferramentas já preparadas neste ambiente, sem alterar variáveis
permanentes do Windows:

```powershell
$env:JAVA_HOME = (Get-ChildItem target/tools -Directory -Filter 'jdk-*' | Select-Object -First 1).FullName
$env:MAVEN_USER_HOME = Join-Path (Get-Location) 'target/maven-user-home'
.\mvnw.cmd -B '-Dmaven.repo.local=target/m2' verify
```

## Resultado da verificação

| Classe de teste | Casos executados | Falhas/erros/ignorados |
| --- | ---: | --- |
| `ParticipanteTest` | 5 | 0 / 0 / 0 |
| `PeriodoReservaTest` | 12 | 0 / 0 / 0 |
| `QuadraTest` | 12 | 0 / 0 / 0 |
| `ReservaTest` | 20 | 0 / 0 / 0 |
| `CancelamentoComAntecedenciaTest` | 14 | 0 / 0 / 0 |
| `CancelamentoLivreTest` | 2 | 0 / 0 / 0 |
| `RepositorioEmMemoriaTest` | 11 | 0 / 0 / 0 |
| `GerenciadorReservasTest` | 31 | 0 / 0 / 0 |
| `EvolucaoReservasTest` | 1 | 0 / 0 / 0 |
| **Total** | **108** | **0 / 0 / 0** |

`verify` terminou com `BUILD SUCCESS` e gerou
`target/plataforma-reservas-1.0.0-SNAPSHOT.jar`. A demonstração executou com código 0,
e `git diff --check` não apontou erros de espaços em branco.

## Arquivos da entrega

Os caminhos Java abaixo partem de `src/main/java/br/edu/fagammon/poo/` ou de
`src/test/java/br/edu/fagammon/poo/`, conforme a coluna. São 13 arquivos novos e
15 arquivos modificados. Nenhum arquivo importante foi excluído.

| Criados | Local |
| --- | --- |
| `dominio/Identificavel.java` | Produção |
| `repositorio/Repositorio.java` | Produção |
| `repositorio/RepositorioEmMemoria.java` | Produção |
| `excecao/EntidadeDuplicadaException.java` | Produção |
| `excecao/EntidadeNaoEncontradaException.java` | Produção |
| `excecao/ConflitoReservaException.java` | Produção |
| `excecao/TransicaoReservaInvalidaException.java` | Produção |
| `excecao/CancelamentoNaoPermitidoException.java` | Produção |
| `politica/CancelamentoLivreTest.java` | Testes |
| `repositorio/RepositorioEmMemoriaTest.java` | Testes |
| `servico/EvolucaoReservasTest.java` | Testes |
| `docs/evolucao.md` | Documentação, a partir da raiz |
| `docs/diagrama_de_classes.md` | Documentação, a partir da raiz |

| Modificados | Local |
| --- | --- |
| `dominio/Participante.java` | Produção |
| `dominio/Quadra.java` | Produção |
| `dominio/Reserva.java` | Produção |
| `dominio/Aplicacao.java` | Produção |
| `politica/PoliticaCancelamento.java` | Produção |
| `politica/CancelamentoLivre.java` | Produção |
| `politica/CancelamentoComAntecedencia.java` | Produção |
| `servico/GerenciadorReservas.java` | Produção |
| `dominio/QuadraTest.java` | Testes |
| `dominio/ReservaTest.java` | Testes |
| `politica/CancelamentoComAntecedenciaTest.java` | Testes |
| `servico/GerenciadorReservasTest.java` | Testes |
| `README.md` | Documentação, a partir da raiz |
| `docs/decisoes.md` | Documentação, a partir da raiz |
| `docs/IA_USAGE.md` | Documentação, a partir da raiz |
