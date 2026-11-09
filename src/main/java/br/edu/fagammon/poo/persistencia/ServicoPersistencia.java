
package br.edu.fagammon.poo.persistencia;

import br.edu.fagammon.poo.dominio.Reserva;
import br.edu.fagammon.poo.servico.GerenciadorReservas;

import java.nio.file.Path;
import java.util.List;

public class ServicoPersistencia {

    private final PersistenciaJson persistencia;
    private final ConversorDadosSistema conversor;

    public ServicoPersistencia() {
        this.persistencia = new PersistenciaJson();
        this.conversor = new ConversorDadosSistema();
    }

    public void salvar(
            GerenciadorReservas gerenciador,
            Path caminho
    ) {
        if (gerenciador == null) {
            throw new IllegalArgumentException(
                    "Gerenciador não pode ser nulo"
            );
        }

        DadosSistema dados = conversor.paraDados(
                gerenciador.getReservas()
        );

        persistencia.salvar(dados, caminho);
    }

    public GerenciadorReservas carregar(Path caminho) {

        DadosSistema dados = persistencia.carregar(caminho);

        List<Reserva> reservas = conversor.paraReservas(dados);

        return GerenciadorReservas.restaurarReservas(reservas);
    }
}
