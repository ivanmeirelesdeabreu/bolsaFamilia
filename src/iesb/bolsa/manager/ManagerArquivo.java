package iesb.bolsa.manager;

import iesb.bolsa.dto.DadoDTO;
import iesb.bolsa.exceptions.MeuErroException;
import iesb.bolsa.util.ConversorUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

public class ManagerArquivo {

    private Connection c;
    private BufferedReader br;
    private PreparedStatement pstm;
    private static final int TAMANHO_BATCH = 5000;


    public void gravarLinha(DadoDTO dto) throws MeuErroException, SQLException {
        pstm.setString(1, dto.uf());
        pstm.setString(2, dto.nis());
        pstm.setDouble(3, dto.valorPag());
        pstm.addBatch();
    }

    public int importarArquivo(String arquivoSelecionado) throws MeuErroException {
        ManagerConexao conexao = new ManagerConexao();
        Connection c = null;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivoSelecionado), "ISO-8859-1"))) {

            conexao.abrirConexao(false);
            c = conexao.getConexao();

            //Statement stm = c.createStatement();
            pstm = c.prepareStatement(
                    "INSERT INTO dados (uf, nis, valor) VALUES (?, ?, ?)"
            );

            String lin;
            int totalRegistros = 0;
            final int TAMANHO_BATCH = 1000;

            // Ignora cabeçalho
            br.readLine();

            while ((lin = br.readLine()) != null) {
                StringTokenizer tok = new StringTokenizer(lin, ";");

                tok.nextToken(); // 1
                tok.nextToken(); // 2
                String c1 = tok.nextToken().replaceAll("\"", ""); // UF
                tok.nextToken(); // 4
                tok.nextToken(); // 5
                tok.nextToken(); // 6
                String c2 = tok.nextToken().replaceAll("\"", ""); // NIS
                tok.nextToken(); // 8
                String c3 = tok.nextToken(); // VALOR

                //DadoDTO dado = new DadoDTO(c1, c2, ConversorUtil.conversaoValor(c3));
                gravarLinha(new DadoDTO(c1, c2, ConversorUtil.conversaoValor(c3)));

                totalRegistros++;
                if (totalRegistros % TAMANHO_BATCH == 0) {
                    pstm.executeBatch();
                    c.commit();
                }
            }

            pstm.executeBatch();
            c.commit();
            return totalRegistros;

        } catch (IOException | SQLException e) {
            throw new MeuErroException("Erro ao importar arquivo: " + e.getMessage());

        } finally {
            conexao.fecharConexao();
        }
    }


}
