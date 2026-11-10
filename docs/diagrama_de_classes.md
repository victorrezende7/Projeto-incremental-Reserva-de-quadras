# Diagrama de classes — Checkpoint 3

O diagrama representa o código do Checkpoint 3. As relações de dependência entre
camadas são mostradas separadamente das associações do domínio para facilitar a leitura.
Setas contínuas (`-->`, `*--`) indicam campos da classe; setas tracejadas (`..>`)
indicam dependências de uso (parâmetros, instanciação).

## Domínio, políticas, repositório e serviço

```mermaid
classDiagram
    direction LR

    class Identificavel {
        <<interface>>
        +getId() String
    }
    class Participante {
        -String idParticipante
        -String nome
        +getId() String
        +getNome() String
    }
    class Quadra {
        -String id
        -String nome
        -Set~Modalidade~ modalidadesPermitidas
        -boolean ativa
        +getId() String
        +ativar()
        +desativar()
        +isAtiva() boolean
        +aceitaModalidade(Modalidade) boolean
    }
    class PeriodoReserva {
        <<record>>
        +LocalDateTime inicio
        +LocalDateTime fim
        +sobrepoe(PeriodoReserva) boolean
    }
    class Reserva {
        -String idReserva
        -StatusReserva statusReserva
        -BigDecimal preco
        +getId() String
        +cancelar()
        +concluir()
        +restaurar(...) Reserva$
    }
    class Modalidade {
        <<enumeration>>
        FUTSAL
        VOLEIBOL
        BASQUETE
        TENIS
        HANDEBOL
        SOCIETY
    }
    class StatusReserva {
        <<enumeration>>
        CONFIRMADA
        CANCELADA
        CONCLUIDA
    }

    class PoliticaCancelamento {
        <<interface>>
        +validarCancelamento(Reserva)
    }
    class CancelamentoLivre
    class CancelamentoComAntecedencia {
        -Duration antecedenciaMinima
        -Clock relogio
        +getAntecedenciaMinima() Duration
    }
    class PoliticaPreco {
        <<interface>>
        +calcularPreco(PeriodoReserva, Modalidade) BigDecimal
    }
    class PrecoFixoPorHora {
        -BigDecimal valorPorHora
    }
    class PrecoPorModalidade {
        -BigDecimal precoPadrao
        -Map~Modalidade,BigDecimal~ precosEspeciais
    }

    class Repositorio~T~ {
        <<interface>>
        +adicionar(T)
        +buscarPorId(String) Optional~T~
        +listar() List~T~
        +obterPorId(String) T
    }
    class RepositorioEmMemoria~T~ {
        -Map~String,T~ entidades
    }
    class GerenciadorReservas {
        -Repositorio~Reserva~ reservas
        +criarReserva(Participante, Quadra, PeriodoReserva, Modalidade, PoliticaCancelamento, PoliticaPreco) Reserva
        +buscarReservaPorId(String) Reserva
        +cancelarReserva(String)
        +concluirReserva(String)
        +getReservas() List~Reserva~
        +buscaPorQuadra(Quadra) List~Reserva~
        +buscaPorParticipante(Participante) List~Reserva~
        +buscaPorStatus(StatusReserva) List~Reserva~
        +buscaPorPeriodo(PeriodoReserva) List~Reserva~
        +reservasOrdenadasPorData() List~Reserva~
        +estaDisponivel(Quadra, PeriodoReserva, Modalidade) boolean
        +restaurarReservas(List~Reserva~) GerenciadorReservas$
    }

    Identificavel <|.. Participante
    Identificavel <|.. Quadra
    Identificavel <|.. Reserva

    Reserva "*" --> "1" Participante : participante
    Reserva "*" --> "1" Quadra : quadra
    Reserva *-- "1" PeriodoReserva : periodoReserva
    Reserva --> "1" Modalidade : modalidade
    Reserva --> "1" StatusReserva : statusReserva
    Reserva --> "1" PoliticaCancelamento : politicaCancelamento
    Quadra --> "1..*" Modalidade : modalidadesPermitidas

    PoliticaCancelamento <|.. CancelamentoLivre
    PoliticaCancelamento <|.. CancelamentoComAntecedencia
    PoliticaCancelamento ..> Reserva : valida
    PoliticaPreco <|.. PrecoFixoPorHora
    PoliticaPreco <|.. PrecoPorModalidade

    Repositorio~T~ <|.. RepositorioEmMemoria~T~
    Repositorio~T~ ..> Identificavel : T extends
    GerenciadorReservas *-- "1" Repositorio~T~ : reservas
    GerenciadorReservas ..> RepositorioEmMemoria~T~ : instancia
    GerenciadorReservas ..> PoliticaPreco : parametro de criarReserva
    GerenciadorReservas ..> PoliticaCancelamento : parametro de criarReserva
```

> Observação: o preço final (`BigDecimal`) é armazenado em `Reserva`; a política
> de preço é utilizada ao criar a reserva, mas não fica associada a ela. Já a
> política de cancelamento é guardada na reserva, pois é consultada em `cancelar()`.

## Persistência e infraestrutura

```mermaid
classDiagram
    direction LR

    class ServicoPersistencia {
        -PersistenciaJson persistencia
        -ConversorDadosSistema conversor
        +salvar(GerenciadorReservas, Path)
        +carregar(Path) GerenciadorReservas
    }
    class PersistenciaJson {
        -ObjectMapper mapper
        +salvar(DadosSistema, Path)
        +carregar(Path) DadosSistema
    }
    class ConversorDadosSistema {
        +paraDados(List~Reserva~) DadosSistema
        +paraReservas(DadosSistema) List~Reserva~
    }
    class DadosSistema {
        <<record>>
        +List~DadosParticipante~ participantes
        +List~DadosQuadra~ quadras
        +List~DadosReserva~ reservas
    }
    class DadosParticipante {
        <<record>>
        +String id
        +String nome
    }
    class DadosQuadra {
        <<record>>
        +String id
        +String nome
        +Set~String~ modalidades
        +boolean ativa
    }
    class DadosReserva {
        <<record>>
        +String id
        +String participanteId
        +String quadraId
        +String inicio
        +String fim
        +String modalidade
        +String status
        +BigDecimal preco
        +String politicaCancelamento
        +Long antecedenciaMinutos
    }

    class GerenciadorReservas
    class Reserva
    class Participante
    class Quadra
    class PoliticaCancelamento {
        <<interface>>
    }
    class CancelamentoLivre
    class CancelamentoComAntecedencia
    class Aplicacao

    ServicoPersistencia *-- "1" PersistenciaJson
    ServicoPersistencia *-- "1" ConversorDadosSistema
    ServicoPersistencia ..> GerenciadorReservas : salva / restaura
    PersistenciaJson ..> DadosSistema : serializa / desserializa
    ConversorDadosSistema ..> DadosSistema : converte
    ConversorDadosSistema ..> Reserva : le / reconstroi
    ConversorDadosSistema ..> Participante : reconstroi
    ConversorDadosSistema ..> Quadra : reconstroi
    ConversorDadosSistema ..> CancelamentoLivre : tipo LIVRE
    ConversorDadosSistema ..> CancelamentoComAntecedencia : tipo ANTECEDENCIA
    PoliticaCancelamento <|.. CancelamentoLivre
    PoliticaCancelamento <|.. CancelamentoComAntecedencia
    DadosSistema *-- "*" DadosParticipante
    DadosSistema *-- "*" DadosQuadra
    DadosSistema *-- "*" DadosReserva
    Aplicacao ..> ServicoPersistencia : demonstra
    Aplicacao ..> GerenciadorReservas : usa
```

**Fronteira arquitetural:** as classes de persistência dependem de tipos do
domínio, e as entidades do domínio (`Participante`, `Quadra`, `Reserva`,
`PeriodoReserva`) não importam Jackson, `Files` ou `Path`. A exceção é
`Aplicacao`, o ponto de entrada de demonstração, que está no pacote `dominio` e
usa `ServicoPersistencia` e `Path`. A estratégia de cancelamento salva é
reconstituída pelo conversor por um identificador de tipo conhecido (`LIVRE` ou
`ANTECEDENCIA`); por isso o conversor depende das duas implementações.

## Estados da reserva

```mermaid
stateDiagram-v2
    [*] --> CONFIRMADA : criacao valida sem conflito
    CONFIRMADA --> CANCELADA : politica de cancelamento autoriza
    CONFIRMADA --> CONCLUIDA : concluir
    CANCELADA --> [*]
    CONCLUIDA --> [*]
```

Canceladas liberam o intervalo; confirmadas e concluídas impedem reservas
sobrepostas da mesma quadra. Estados terminais não retornam a `CONFIRMADA`:
tentativas de nova transição lançam `TransicaoReservaInvalidaException`.
