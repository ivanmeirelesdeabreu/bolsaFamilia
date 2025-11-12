package iesb.bolsa.service.impl;

import iesb.bolsa.exceptions.MeuErroException;
import iesb.bolsa.manager.ManagerConexao;
import iesb.bolsa.service.Service;

import java.sql.SQLException;

public class ImportarSinteticoService implements Service {

    private MeuErroException err;
    private int totalRegistros = 0; // opcional, se quiser contar

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public Exception getErr() {
        return err;
    }

    @Override
    public boolean execute() {
    //public boolean execute() throws MeuErroException {
    // Seria melhor void na interface Service execute()..
    // deixar assim por enquanto.... não podemos mudar a interface...
    //public void execute() throws MeuErroException {
        err = null;
        ManagerConexao manager = new ManagerConexao(); // ou ManagerSintetico, se tiver

        try {
            // Chama o método que gera a tabela sintética
            totalRegistros = manager.carrTabSintetico();
            return true;

        } catch (MeuErroException e) {
            //throw new MeuErroException(e.getMessage());
            err = e;
            return false;

        } catch (Exception e) {
            //throw new MeuErroException("Erro inesperado durante a importação" + e.getMessage());
            err = new MeuErroException(e.getMessage());
            return false;
        }
    }
}

