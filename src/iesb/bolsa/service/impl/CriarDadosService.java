package iesb.bolsa.service.impl;

import iesb.bolsa.exceptions.MeuErroException;
import iesb.bolsa.manager.ManagerConexao;
import iesb.bolsa.service.Service;

public class CriarDadosService implements Service {
    private MeuErroException err;

    public MeuErroException getErr() {
        return err;
    }

    @Override
    public boolean execute() {
        ManagerConexao obj = new ManagerConexao();
        try {
            obj.abrirConexao(true);
            obj.criarTabDeDados();
            obj.fecharConexao();
            return true;
        } catch (MeuErroException e) {
            //throw e;
            err = e;
            return false;
        } catch (Exception e) {
            err = new MeuErroException(e.getMessage());
            return false;
        }
    }
}