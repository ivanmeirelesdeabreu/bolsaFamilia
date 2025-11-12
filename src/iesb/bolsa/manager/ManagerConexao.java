package iesb.bolsa.manager;

import iesb.bolsa.exceptions.MeuErroException;

import java.sql.*;
import java.util.Date;

public class ManagerConexao {

    private Connection c;

    public Connection getConexao() {
        return c;
    }

    public void abrirConexao(boolean auto) throws MeuErroException {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:C:\\programas\\bolsa\\bolsa\\bd\\BolsaSQLi.db");
            c.setAutoCommit(auto);
        } catch (ClassNotFoundException e1) {
            throw new MeuErroException(e1.getMessage());
        } catch (SQLException e2) {
            throw new MeuErroException(e2.getMessage());
        }
    }

    public void criarTabDeDados() throws MeuErroException {
        try {
            Statement stm = c.createStatement();
            stm.executeUpdate("""
                        CREATE TABLE dados (
                            id INTEGER primary KEY AUTOINCREMENT,
                            uf CHAR(2) NOT NULL,
                            nis VARCHAR(20),
                            valor NUMERIC(5,2))
                    """);

        } catch (SQLException e) {
            throw new MeuErroException("Erro ao criar tabela dado: " + e.getMessage());
        }
    }

    public void criarTabSintetico() throws MeuErroException {
        try {
            Statement stm = c.createStatement();
            stm.executeUpdate("""
                CREATE TABLE sintetico (
                    uf char(2) primary key not null,
                    valTotal numeric,
                    totNis integer,
                    totNisIndep integer
                );
            """);
        } catch (SQLException e) {
            throw new MeuErroException("Erro ao criar tabela sintetico': " + e.getMessage());
        }
    }

    public void carrTabDados() {

    }

    public int  carrTabSintetico() throws MeuErroException {
        System.out.println("Início: " + new Date());

        ManagerConexao conexao = new ManagerConexao();
        //Connection c = null;
        Statement stm = null;
        ResultSet r = null;
        PreparedStatement pstm = null;

        try {
            // Abre conexão com commit manual
            conexao.abrirConexao(false); // false para batch commit
            c = conexao.getConexao();

            // Prepara o PreparedStatement para inserir na tabela sintética
            String insertSql = "INSERT INTO sintetico (uf, valTotal, totNis, totNisIndep) VALUES (?, ?, ?, ?)";
            pstm = c.prepareStatement(insertSql);

            // Consulta agregada
            String sql = """
                SELECT uf, SUM(valor) AS total_valor, COUNT(nis) AS total_nis, COUNT(DISTINCT nis) AS total_dist_nis
                FROM dados
                GROUP BY uf
            """;

            stm = c.createStatement();
            r = stm.executeQuery(sql);

            int totalRegistros = 0;
            final int TAMANHO_BATCH = 5000;

            while (r.next()) {
                String uf = r.getString("uf");
                double valor = r.getDouble("total_valor");
                int totNis = r.getInt("total_nis");
                int totNisIndep = r.getInt("total_dist_nis");

                // Preenche parâmetros do PreparedStatement
                pstm.setString(1, uf);
                pstm.setDouble(2, valor);
                pstm.setInt(3, totNis);
                pstm.setInt(4, totNisIndep);
                pstm.addBatch();

                totalRegistros++;

                if (totalRegistros % TAMANHO_BATCH == 0) {
                    pstm.executeBatch();
                    c.commit();
                }

                // Log do progresso
                System.out.printf("%s - Média: %.2f - Valor/DistNIS: %.2f - TotNIS: %d - TotDistNIS: %d%n",
                        uf, valor / totNis, valor / totNisIndep, totNis, totNisIndep);
            }

            // Executa o restante do batch
            pstm.executeBatch();
            c.commit();
            System.out.println("Final: " + new Date());
            return totalRegistros;

        } catch (SQLException e) {
            try { if (c != null) c.rollback(); } catch (SQLException ex) { /* log */ }
            throw new MeuErroException("Erro ao carregar tabela sintética: " + e.getMessage());

        } finally {
            // Fecha recursos
            try { if (r != null) r.close(); } catch (SQLException e) { /* log */ }
            try { if (stm != null) stm.close(); } catch (SQLException e) { /* log */ }
            try { if (pstm != null) pstm.close(); } catch (SQLException e) { /* log */ }
            conexao.fecharConexao();
        }
    }

    public void fecharConexao() throws MeuErroException {
        try {
            c.close();
        } catch (SQLException e) {
            throw new MeuErroException(e.getMessage());
        }
    }

}