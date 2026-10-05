# Plataforma de Reservas

## Equipe

- Integrante 1: Victor Henrique Rezende Andrade
- Integrante 2: 

## Variante

- Variante: Quadras Esportivas
- Regra específica: Compatibilidade de modalidade - cada quadra possui uma lista de modalidades permitidas; o sistema recusa reservas com modalidade incompatível.

## Requisitos atendidos

Atualize esta seção em cada marco do projeto.

## Requisitos atendidos (checkpoint 1)

- RF1 (parcial): cadastro e validação de Participante.
- RF2 (parcial): cadastro, ativação/desativação de Quadra.
- RF3: Período como objeto de valor imutável, com validação de início/fim.
- RF4 (parcial): Reserva recusa quadra inativa e modalidade incompatível;
  detecção de sobreposição entre reservas fica para o checkpoint 2.
- Regra própria da variante: compatibilidade de modalidade implementada e testada.

## Como executar

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

## Estrutura

As classes de domínio serão criadas pela equipe depois da escolha da variante.
Não adicione camadas, interfaces ou padrões antes de existir um requisito que
justifique a decisão.
