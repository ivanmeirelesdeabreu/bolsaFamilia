package iesb.bolsa.service.impl;

import iesb.bolsa.exceptions.MeuErroException;
import iesb.bolsa.manager.ManagerGrafico;
import iesb.bolsa.service.Service;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class CriarGraficoService implements Service {

    private MeuErroException err;
    private int tipoGrafico; // 1=pizza, 2=barras, 3=linhas
    private JPanel painel;

    public CriarGraficoService(int tipoGrafico, JPanel painel) {
        this.tipoGrafico = tipoGrafico;
        this.painel = painel;
    }

    @Override
    public boolean execute() {
        try {
            ManagerGrafico manager = new ManagerGrafico();
            manager.carregarTop5Valores();

            JFreeChart chart = null;
            if (tipoGrafico == 1) {
                chart = criarPizza(manager.ufs, manager.valores);
            } else if (tipoGrafico == 2) {
                chart = criarBarras(manager.ufs, manager.valores);
            } else {
                chart = criarLinhas(manager.ufs, manager.valores);
            }

            exibirGrafico(chart);
            return true;

        } catch (MeuErroException e) {
            err = e;
            return false;
        } catch (Exception e) {
            err = new MeuErroException("Erro ao criar gráfico: " + e.getMessage());
            return false;
        }
    }

    private JFreeChart criarPizza(String[] ufs, double[] valores) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < ufs.length && ufs[i] != null; i++) {
            dataset.setValue(ufs[i], valores[i]);
        }
        return ChartFactory.createPieChart(
                "Top 5 Valores Bolsa Família (Pizza)",
                dataset,
                true, true, false
        );
    }

    private JFreeChart criarBarras(String[] ufs, double[] valores) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ufs.length && ufs[i] != null; i++) {
            dataset.addValue(valores[i], "Valor Total", ufs[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 Valores Bolsa Família (Barras)",
                "UF", "Valor Total (R$)",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );

        var plot = chart.getCategoryPlot();
        var eixoY = (NumberAxis) plot.getRangeAxis();
        eixoY.setNumberFormatOverride(new DecimalFormat("R$ #,##0.00"));

        return chart;
    }

    private JFreeChart criarLinhas(String[] ufs, double[] valores) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ufs.length && ufs[i] != null; i++) {
            dataset.addValue(valores[i], "Valor Total", ufs[i]);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Top 5 Valores Bolsa Família (Linhas)",
                "UF", "Valor Total (R$)",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );

        var plot = chart.getCategoryPlot();
        var eixoY = (NumberAxis) plot.getRangeAxis();
        eixoY.setNumberFormatOverride(new DecimalFormat("R$ #,##0.00"));

        return chart;
    }

    private void exibirGrafico(JFreeChart chart) {
        painel.removeAll();
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        painel.add(chartPanel, BorderLayout.CENTER);
        painel.revalidate();
        painel.repaint();
    }

    public MeuErroException getErr() {
        return err;
    }
}
