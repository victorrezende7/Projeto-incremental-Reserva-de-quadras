
package br.edu.fagammon.poo.persistencia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PersistenciaJson {

    private final ObjectMapper mapper;

    public PersistenciaJson() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void salvar(DadosSistema dados, Path caminho) {
        if (dados == null || caminho == null) {
            throw new IllegalArgumentException(
                    "Dados e caminho não podem ser nulos"
            );
        }

        try {
            Path pasta = caminho.toAbsolutePath().getParent();

            if (pasta != null) {
                Files.createDirectories(pasta);
            }

            mapper.writeValue(caminho.toFile(), dados);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao salvar os dados", e
            );
        }
    }

    public DadosSistema carregar(Path caminho) {
        if (caminho == null) {
            throw new IllegalArgumentException(
                    "Caminho não pode ser nulo"
            );
        }

        try {
            return mapper.readValue(
                    caminho.toFile(),
                    DadosSistema.class
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar os dados", e
            );
        }
    }
}
