package iesb.bolsa.service.impl;

import iesb.bolsa.TelaPrincipal;
import iesb.bolsa.exceptions.MeuErroException;
import iesb.bolsa.manager.ManagerConexao;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class CriarGraficoService implements iesb.bolsa.service.Service {

    private MeuErroException err;
    private TelaPrincipal telaPrincipal;
    private int tipoGrafico; // 1=pizza, 2=barras, 3=linhas

    public CriarGraficoService(TelaPrincipal telaPrincipal, int tipoGrafico) {
        this.telaPrincipal = telaPrincipal;
        this.tipoGrafico = tipoGrafico;
    }

    @Override
    public boolean execute() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ManagerConexao manager = new ManagerConexao();
            manager.abrirConexao(true);
            conn = manager.getConexao();
            //conn = manager.abrirConexao(true);

            String sql = "SELECT uf, valTotal FROM sintetico ORDER BY valTotal DESC LIMIT 5";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            Map<String, Double> dados = new LinkedHashMap<>();
            while (rs.next()) {
                dados.put(rs.getString("uf"), rs.getDouble("valTotal"));
            }

            if (dados.isEmpty()) {
                throw new MeuErroException("Nenhum dado encontrado na tabela sintetico.");
            }

            JFreeChart chart = null;
            switch (tipoGrafico) {
                case 1:
                    chart = criarGraficoPizza(dados);
                    break;
                case 2:
                    chart = criarGraficoBarras(dados);
                    break;
                case 3:
                    chart = criarGraficoLinhas(dados);
                    break;
                default:
                    throw new MeuErroException("Tipo de gráfico inválido.");
            }

            exibirGraficoNaTela(chart);
            salvarGraficoComoImagem(chart, tipoGrafico);

            return true;

        } catch (Exception e) {
            err = new MeuErroException("Erro ao criar gráfico: " + e.getMessage());
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (conn != null) conn.close(); } catch (Exception ignored) {}
        }
    }

    private JFreeChart criarGraficoPizza(Map<String, Double> dados) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dados.forEach(dataset::setValue);
        return ChartFactory.createPieChart(
                "Top 5 Valores Bolsa Família (Pizza)",
                dataset,
                true, true, false
        );
    }
    /*
    private JFreeChart criarGraficoBarras(Map<String, Double> dados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dados.forEach((uf, valor) -> dataset.addValue(valor, "Valor Total", uf));
        return ChartFactory.createBarChart(
                "Top 5 Valores Bolsa Família (Barras)",
                "UF", "Valor Total (R$)",
                dataset
        );
    }
    */
    private JFreeChart criarGraficoBarras(Map<String, Double> dados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dados.forEach((uf, valor) -> dataset.addValue(valor, "Valor Total", uf));

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Valores Bolsa Família (Barras)",
                "UF", "Valor Total (R$)",
                dataset
        );

        // >>> Adiciona formatação de números no eixo Y <<<
        var plot = chart.getCategoryPlot();
        var eixoY = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        eixoY.setNumberFormatOverride(new java.text.DecimalFormat("#,##0.00"));

        return chart;
    }


    /*
    private JFreeChart criarGraficoLinhas(Map<String, Double> dados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dados.forEach((uf, valor) -> dataset.addValue(valor, "Valor Total", uf));
        return ChartFactory.createLineChart(
                "Top 5 Valores Bolsa Família (Linhas)",
                "UF", "Valor Total (R$)",
                dataset
        );
    }
    */
    private JFreeChart criarGraficoLinhas(Map<String, Double> dados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dados.forEach((uf, valor) -> dataset.addValue(valor, "Valor Total", uf));

        JFreeChart chart = ChartFactory.createLineChart(
                "Top 5 Valores Bolsa Família (Linhas)",
                "UF", "Valor Total (R$)",
                dataset
        );

        // >>> Adiciona formatação de números no eixo Y <<<
        var plot = chart.getCategoryPlot();
        var eixoY = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        eixoY.setNumberFormatOverride(new java.text.DecimalFormat("#,##0.00"));

        return chart;
    }


    private void exibirGraficoNaTela(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel painelCentral = telaPrincipal.getPainelCentral();
        painelCentral.removeAll();
        painelCentral.add(chartPanel, BorderLayout.CENTER);
        painelCentral.revalidate();
        painelCentral.repaint();
    }

    private void salvarGraficoComoImagem(JFreeChart chart, int tipoGrafico) {
        try {
            String nome = switch (tipoGrafico) {
                case 1 -> "grafico_pizza.png";
                case 2 -> "grafico_barras.png";
                case 3 -> "grafico_linhas.png";
                default -> "grafico.png";
            };
            File arquivo = new File(nome);
            ChartUtils.saveChartAsPNG(arquivo, chart, 800, 600);
        } catch (Exception e) {
            System.err.println("Não foi possível salvar o gráfico: " + e.getMessage());
        }
    }

    public MeuErroException getErr() {
        return err;
    }
}
