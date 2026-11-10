# Diagrama da evolução

```mermaid
classDiagram
    class Identificavel {
        <<interface>>
        +getId() String
    }
    class Participante {
        -String idParticipante
        -String nome
    }
    class Quadra {
        -String id
        -Set~Modalidade~ modalidadesPermitidas
        -boolean ativa
        +ativar()
        +desativar()
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
        +cancelar()
        +concluir()
    }
    class PoliticaCancelamento {
        <<interface>>
        +validarCancelamento(Reserva)
    }
    class CancelamentoLivre
    class CancelamentoComAntecedencia {
        -Duration antecedenciaMinima
        -Clock relogio
    }
    class Repositorio~T~ {
        <<interface>>
        +adicionar(T)
        +buscarPorId(String) Optional~T~
        +obterPorId(String) T
        +listar() List~T~
    }
    class RepositorioEmMemoria~T~ {
        -Map~String,T~ entidades
    }
    class GerenciadorReservas {
        -Repositorio~Reserva~ reservas
        +criarReserva(Participante, Quadra, PeriodoReserva, Modalidade, PoliticaCancelamento) Reserva
        +buscarReservaPorId(String) Reserva
        +cancelarReserva(String)
        +concluirReserva(String)
        +buscaPorQuadra(Quadra) List~Reserva~
        +buscaPorParticipante(Participante) List~Reserva~
        +buscaPorStatus(StatusReserva) List~Reserva~
        +buscaPorPeriodo(PeriodoReserva) List~Reserva~
        +reservasOrdenadasPorData() List~Reserva~
        +estaDisponivel(Quadra, PeriodoReserva, Modalidade) boolean
    }
    Identificavel <|.. Participante
    Identificavel <|.. Quadra
    Identificavel <|.. Reserva
    Repositorio~T~ <|.. RepositorioEmMemoria~T~
    Repositorio~T~ ..> Identificavel : T extends Identificavel
    GerenciadorReservas *-- RepositorioEmMemoria : agenda interna
    Reserva --> "1" Participante
    Reserva --> "1" Quadra
    Reserva *-- "1" PeriodoReserva : valor imutavel
    Reserva --> "1" PoliticaCancelamento : estrategia composta
    PoliticaCancelamento <|.. CancelamentoLivre
    PoliticaCancelamento <|.. CancelamentoComAntecedencia
    CancelamentoComAntecedencia --> Clock
    Reserva --> StatusReserva
    Reserva --> Modalidade
    Quadra --> Modalidade
```

Participantes e quadras são entidades compartilhadas: não são destruídos quando
uma reserva muda de estado. A composição comportamental está na política de
cancelamento recebida por `Reserva`; a mesma interface admite novas estratégias.
O repositório genérico é usado com `Participante`, `Quadra` e `Reserva`.

```mermaid
stateDiagram-v2
    [*] --> CONFIRMADA : criacao valida sem conflito
    CONFIRMADA --> CANCELADA : politica permite cancelar
    CONFIRMADA --> CONCLUIDA : concluir
    CANCELADA --> [*]
    CONCLUIDA --> [*]
```

Não há retorno a `CONFIRMADA`. Tentativas de nova transição a partir dos estados
terminais lançam `TransicaoReservaInvalidaException`. Os registros terminais
permanecem no histórico; somente `CANCELADA` deixa de bloquear o intervalo.
