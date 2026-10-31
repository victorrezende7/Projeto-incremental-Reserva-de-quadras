package br.edu.fagammon.poo.repositorio;

import br.edu.fagammon.poo.dominio.Identificavel;
import br.edu.fagammon.poo.excecao.EntidadeDuplicadaException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioEmMemoria<T extends Identificavel> implements Repositorio<T> {
    private final Map<String, T> entidades = new LinkedHashMap<>();

    @Override
    public void adicionar(T entidade) {
        if (entidade == null) {
            throw new IllegalArgumentException("Entidade não pode ser nula");
        }
        String id = entidade.getId();
        validarId(id);
        if (entidades.containsKey(id)) {
            throw new EntidadeDuplicadaException(id);
        }
        entidades.put(id, entidade);
    }

    @Override
    public Optional<T> buscarPorId(String id) {
        validarId(id);
        return Optional.ofNullable(entidades.get(id));
    }

    @Override
    public List<T> listar() {
        return List.copyOf(entidades.values());
    }

    private static void validarId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Identificador não pode ser nulo ou vazio");
        }
    }
}
