package br.edu.fagammon.poo.repositorio;

import br.edu.fagammon.poo.dominio.*;
import br.edu.fagammon.poo.excecao.EntidadeDuplicadaException;
import br.edu.fagammon.poo.excecao.EntidadeNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioEmMemoriaTest {
    private final Repositorio<Participante> participantes = new RepositorioEmMemoria<>();

    @Test
    void deveCadastrarEBuscarPreservandoOrdemDeInclusao() {
        Participante primeiro = new Participante("02", "Ana");
        Participante segundo = new Participante("01", "Bia");
        participantes.adicionar(primeiro);
        participantes.adicionar(segundo);
        assertSame(primeiro, participantes.obterPorId("02"));
        assertEquals(List.of(primeiro, segundo), participantes.listar());
    }

    @Test
    void deveRecusarIdDuplicadoSemSubstituirCadastroOriginal() {
        Participante original = new Participante("01", "Ana");
        participantes.adicionar(original);
        assertThrows(EntidadeDuplicadaException.class,
                () -> participantes.adicionar(new Participante("01", "Outro nome")));
        assertThrows(EntidadeDuplicadaException.class, () -> participantes.adicionar(original));
        assertSame(original, participantes.obterPorId("01"));
        assertEquals(1, participantes.listar().size());
    }

    @Test
    void deveDiferenciarConsultaOpcionalDeBuscaObrigatoria() {
        assertTrue(participantes.buscarPorId("ausente").isEmpty());
        EntidadeNaoEncontradaException erro = assertThrows(EntidadeNaoEncontradaException.class,
                () -> participantes.obterPorId("ausente"));
        assertTrue(erro.getMessage().contains("ausente"));
    }

    @Test
    void listaDeveSerImutavelEIndependenteDeNovosCadastros() {
        participantes.adicionar(new Participante("01", "Ana"));
        List<Participante> copia = participantes.listar();
        assertThrows(UnsupportedOperationException.class, copia::clear);
        participantes.adicionar(new Participante("02", "Bia"));
        assertEquals(1, copia.size());
        assertEquals(2, participantes.listar().size());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void deveRecusarIdentificadoresInvalidos(String id) {
        assertThrows(IllegalArgumentException.class, () -> participantes.buscarPorId(id));
        assertThrows(IllegalArgumentException.class, () -> participantes.obterPorId(id));
        Repositorio<Identificavel> entidades = new RepositorioEmMemoria<>();
        assertThrows(IllegalArgumentException.class, () -> entidades.adicionar(() -> id));
        assertTrue(entidades.listar().isEmpty());
    }

    @Test
    void deveRecusarEntidadeNula() {
        assertThrows(IllegalArgumentException.class, () -> participantes.adicionar(null));
        assertTrue(participantes.listar().isEmpty());
    }

    @Test
    void deveReutilizarRepositorioParaQuadrasComIdentidadesIndependentes() {
        Repositorio<Quadra> quadras = new RepositorioEmMemoria<>();
        Quadra quadra = new Quadra("01", "Tênis", EnumSet.of(Modalidade.TENIS));
        quadras.adicionar(quadra);
        participantes.adicionar(new Participante("01", "Ana"));
        quadras.obterPorId("01").desativar();
        assertFalse(quadra.isAtiva());
        assertEquals("Ana", participantes.obterPorId("01").getNome());
    }
}
