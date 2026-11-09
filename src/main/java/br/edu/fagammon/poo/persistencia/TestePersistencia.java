package br.edu.fagammon.poo.persistencia;

import br.edu.fagammon.poo.persistencia.DadosSistema;
import br.edu.fagammon.poo.persistencia.PersistenciaJson;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class TestePersistencia {

    public static void main(String[] args) {

        PersistenciaJson persistencia = new PersistenciaJson();

        DadosSistema dados = new DadosSistema(
                List.of(
                        new DadosSistema.DadosParticipante(
                                "01", "Joao"
                        )
                ),
                List.of(
                        new DadosSistema.DadosQuadra(
                                "01",
                                "Quadra de Futsal",
                                Set.of("FUTSAL"),
                                true
                        )
                ),
                List.of()
        );

        Path caminho = Path.of("dados", "reservas.json");

        persistencia.salvar(dados, caminho);
        System.out.println("Dados salvos!");

        DadosSistema recuperados =
                persistencia.carregar(caminho);

        System.out.println(
                recuperados.participantes()
        );
    }
}
