package iesb.bolsa.service.impl;

import iesb.bolsa.dto.DadoDTO;
import iesb.bolsa.exceptions.MeuErroException;
import iesb.bolsa.manager.ManagerArquivo;
import iesb.bolsa.manager.ManagerConexao;
import iesb.bolsa.service.Service;
import iesb.bolsa.util.ConversorUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;

public class ImportarDadosService implements Service {

    //private Connection c;
    //private BufferedReader br;
    //rivate PreparedStatement pstm;
    //private static final int TAMANHO_BATCH = 5000;

    private int totalRegistros = 0;
    private final String arquivoSelecionado;
    private MeuErroException err;

    public ImportarDadosService(String nomArq) {
        this.arquivoSelecionado = nomArq;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }

    public Exception getErr() {
        return err;
    }

    @Override
    public boolean execute() {
        err = null;
        ManagerArquivo obj = new ManagerArquivo();

        try {
            // Importa o arquivo e grava no banco usando a conexão ativa
            totalRegistros = obj.importarArquivo(arquivoSelecionado);
            return true;

        } catch (MeuErroException e) {
            err = e;
            return false;

        } catch (Exception e) {
            err = new MeuErroException(e.getMessage());
            return false;
        }
    }

}
