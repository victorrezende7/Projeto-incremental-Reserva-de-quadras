package br.edu.fagammon.poo.dominio;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class QuadraTest {

    @Test
    void deveCriarQuadraValida() {
        Quadra quadra = new Quadra("01", "Quadra Poliesportiva",
                EnumSet.of(Modalidade.FUTSAL, Modalidade.VOLEIBOL));

        assertEquals("01", quadra.getId());
        assertEquals("Quadra Poliesportiva", quadra.getNome());
        assertTrue(quadra.isAtiva());
    }

    @Test
    void deveRecusarIdNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Quadra(null, "Quadra 1", EnumSet.of(Modalidade.FUTSAL));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Quadra("", "Quadra 1", EnumSet.of(Modalidade.FUTSAL));
        });
    }

    @Test
    void deveRecusarNomeNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Quadra("01", null, EnumSet.of(Modalidade.FUTSAL));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Quadra("01", "  ", EnumSet.of(Modalidade.FUTSAL));
        });
    }

    @Test
    void deveRecusarQuadraSemModalidades() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Quadra("01", "Quadra sem esporte", EnumSet.noneOf(Modalidade.class));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Quadra("01", "Quadra sem esporte", null);
        });
    }

    @Test
    void deveAceitarModalidadePermitida() {
        Quadra quadra = new Quadra("01", "Quadra de Futsal", EnumSet.of(Modalidade.FUTSAL));

        assertTrue(quadra.aceitaModalidade(Modalidade.FUTSAL));
    }

    @Test
    void deveRecusarModalidadeNaoPermitida() {
        Quadra quadra = new Quadra("01", "Quadra de Tênis", EnumSet.of(Modalidade.TENIS));

        assertFalse(quadra.aceitaModalidade(Modalidade.FUTSAL));
    }

    @Test
    void deveAtivarEDesativarQuadra() {
        Quadra quadra = new Quadra("01", "Quadra 1", EnumSet.of(Modalidade.FUTSAL));

        quadra.desativar();
        assertFalse(quadra.isAtiva());

        quadra.ativar();
        assertTrue(quadra.isAtiva());
    }

    @Test
    void getModalidadesPermitidasDeveRetornarCopia() {
        Quadra quadra = new Quadra("01", "Quadra 1", EnumSet.of(Modalidade.FUTSAL));

        Set<Modalidade> modalidades = quadra.getModalidadesPermitidas();
        modalidades.add(Modalidade.TENIS);

        assertFalse(quadra.aceitaModalidade(Modalidade.TENIS));
    }

    @Test
    void quadrasComMesmoIdDevemSerIguais() {
        Quadra q1 = new Quadra("01", "Quadra Futsal", EnumSet.of(Modalidade.FUTSAL));
        Quadra q2 = new Quadra("01", "Quadra Futsal (nome diferente)", EnumSet.of(Modalidade.VOLEIBOL));

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void quadrasComIdDiferenteNaoDevemSerIguais() {
        Quadra q1 = new Quadra("01", "Quadra 1", EnumSet.of(Modalidade.FUTSAL));
        Quadra q2 = new Quadra("02", "Quadra 1", EnumSet.of(Modalidade.FUTSAL));

        assertNotEquals(q1, q2);
    }
}