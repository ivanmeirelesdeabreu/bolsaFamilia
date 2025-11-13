package iesb.bolsa.manager;

import iesb.bolsa.exceptions.MeuErroException;

import java.sql.*;

public class ManagerGrafico {

    public String[] ufs = new String[5];
    public double[] valores = new double[5];

    public void carregarTop5Valores() throws MeuErroException {
        ManagerConexao manager = new ManagerConexao();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            manager.abrirConexao(true);
            conn = manager.getConexao();

            String sql = "SELECT uf, valTotal FROM sintetico ORDER BY valTotal DESC LIMIT 5";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            int i = 0;
            while (rs.next() && i < 5) {
                ufs[i] = rs.getString("uf");
                valores[i] = rs.getDouble("valTotal");
                i++;
            }

        } catch (SQLException e) {
            throw new MeuErroException("Erro ao buscar dados do gráfico: " + e.getMessage());
        } finally {
            manager.fecharConexao();
        }
    }
}
