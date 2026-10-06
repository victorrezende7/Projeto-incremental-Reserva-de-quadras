package br.edu.fagammon.poo.dominio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipanteTest {

    @Test
    void deveCriarParticipanteValido() {
        Participante participante = new Participante("01", "Victor");

        assertEquals("01", participante.getId());
        assertEquals("Victor", participante.getNome());
    }

    @Test
    void deveRecusarIdNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participante(null, "Victor");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Participante("  ", "Victor");
        });
    }

    @Test
    void deveRecusarNomeNuloOuVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Participante("01", null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Participante("01", "");
        });
    }

    @Test
    void participantesComMesmoIdDevemSerIguais() {
        Participante p1 = new Participante("01", "Victor");
        Participante p2 = new Participante("01", "Victor Henrique");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void participantesComIdDiferenteNaoDevemSerIguais() {
        Participante p1 = new Participante("01", "Victor");
        Participante p2 = new Participante("02", "Victor");

        assertNotEquals(p1, p2);
    }
}