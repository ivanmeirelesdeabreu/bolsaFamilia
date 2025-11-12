package iesb.bolsa;

import iesb.bolsa.service.impl.*;
import iesb.bolsa.util.TempoExecucao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class TelaPrincipal extends JFrame {

    private String arquivoSelecionado;
    private JLabel lblMensagem;      // área de mensagens
    private JPanel painelCentral;    // onde os gráficos serão exibidos

    public TelaPrincipal() {
        this.setTitle("Dados do Bolsa Família");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.criarMenu();
        criarPainelCentral();
        criarRodapeMensagens();
        this.setVisible(true);
    }

    private void criarMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu mn1 = new JMenu("Opções");
        JMenu mn2 = new JMenu("Gráficos");
        JMenu mn3 = new JMenu("Auxílio");

        //mn1
        JMenu mnCriar = new JMenu("Criaçao das Tabelas");

        JMenuItem selArquivo = new JMenuItem("Selecionar Arquivo");
        selArquivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selecionarArquivo();
            }
        });

        JMenuItem itCriarDados = new JMenuItem("Tabela Dados");
        itCriarDados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chamarCriarDados();
            }
        });

        JMenuItem itCriarSintetico = new JMenuItem("Tabela Sintetico");
        itCriarSintetico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chamarCriarSintetico();
            }
        });

        JMenu mnCarregar = new JMenu("Carregar Tabelas");

        JMenuItem itCarrDados = new JMenuItem("Tabela Dados");
        itCarrDados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chamarImportarDadosService();
            }
        });

        JMenuItem itCarrSintetico = new JMenuItem("Tabela Sintético");
        itCarrSintetico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chamarImportarSinteticoService();
            }
        });

        //mn2
        JMenuItem itGrfTop1 = new JMenuItem("Gráfico 1");
        itGrfTop1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itGrfTop1ActionPerformed();
            }
        });





        JMenuItem itGrfTop2 = new JMenuItem("Gráfico 2");
        itGrfTop2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itGrfTop2ActionPerformed();
            }
        });

        JMenuItem itGrfTop3 = new JMenuItem("Gráfico 3");
        itGrfTop3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itGrfTop3ActionPerformed();
            }
        });

        //mn3
        JMenuItem itSobre = new JMenuItem("Sobre o Sistema");

        mnCriar.add(itCriarDados);
        mnCriar.add(itCriarSintetico);

        mnCarregar.add(itCarrDados);
        mnCarregar.add(itCarrSintetico);

        mn1.add(selArquivo);
        mn1.add(mnCriar);
        mn1.add(mnCarregar);

        mn2.add(itGrfTop1);
        mn2.add(itGrfTop2);
        mn2.add(itGrfTop3);

        mn3.add(itSobre);

        bar.add(mn1);
        bar.add(mn2);
        bar.add(mn3);

        this.setJMenuBar(bar);
    }
    private void criarPainelCentral() {
        painelCentral = new JPanel();
        painelCentral.setLayout(new BorderLayout());
        painelCentral.setBackground(Color.WHITE);

        JLabel lblPlaceholder = new JLabel("Área de gráficos", SwingConstants.CENTER);
        lblPlaceholder.setFont(new Font("Arial", Font.BOLD, 24));
        painelCentral.add(lblPlaceholder, BorderLayout.CENTER);

        this.add(painelCentral, BorderLayout.CENTER);
    }
    private void criarRodapeMensagens() {
        lblMensagem = new JLabel("Favor selecionar o Arquivo para importação dos dados");
        lblMensagem.setOpaque(true);
        lblMensagem.setBackground(new Color(230, 230, 230));
        lblMensagem.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.add(lblMensagem, BorderLayout.SOUTH);
    }
    private void selecionarArquivo() {
        FileDialog dig = new FileDialog(this, "Abrir Arquivo do Bolsa",
                FileDialog.LOAD);
        dig.setDirectory("C:\\programas\\bolsa\\bolsa\\dados");
        dig.setFile("*.csv");
        dig.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.contains(".csv");
            }
        });
        dig.setVisible(true);
        this.arquivoSelecionado = dig.getDirectory() + dig.getFile();

        if (this.arquivoSelecionado != null && !this.arquivoSelecionado.endsWith("null")) {
            mostrarMensagem("Arquivo selecionado: " + this.arquivoSelecionado);
        } else {
            mostrarMensagem("Nenhum arquivo selecionado.");
        }

        //System.out.println("Seu Arquivo eh: " + this.nomeArq);
    }

    private void chamarCriarDados() {
        CriarDadosService service = new CriarDadosService();
        if (service.execute()) {
            mostrarMensagem("Tabela de dados criada com sucesso!");
        } else {
            mostrarErro(service.getErr().getMessage());
        }
    }

    private void chamarCriarSintetico() {
        //new CriarSinteticoService().execute();
        CriarSinteticoService service = new CriarSinteticoService();
        if (service.execute()) {
            mostrarMensagem("Tabela de dados Sintetico criada com sucesso!");
        } else {
            mostrarErro(service.getErr().getMessage());
        }
    }

    private void chamarImportarSinteticoService() {
        ImportarSinteticoService service = new ImportarSinteticoService();
        try {
            // Marca o início da execução
            TempoExecucao.iniciar();

            if (service.execute()) {
                //service.execute();
                String totalRegistrosFmt = String.format(
                        new Locale("pt", "BR"), "%,d registros", service.getTotalRegistros()
                );

                mostrarMensagem(String.format(
                        "Tabela de dados sintetico carregada com sucesso! " +
                                "Registros carregados: %s " +
                                "Tempo total: %s",
                        totalRegistrosFmt,
                        TempoExecucao.getTempoFormatado()
                ));
            } else {
                mostrarErro(service.getErr().getMessage());
            }

        } catch (Exception e) {
            // Captura qualquer exceção inesperada
            mostrarErro("Erro durante a importação de dados sintético: " + e.getMessage());

        } finally {
            // Marca o fim da execução, independentemente do resultado
            TempoExecucao.finalizar();
        }
    }

    private void chamarImportarDadosService() {
        if (arquivoSelecionado == null) {
            mostrarErro("Nenhum arquivo foi selecionado para importação.");
            return;
        }
        ImportarDadosService service = new ImportarDadosService(arquivoSelecionado);
        try {
            // Marca o início da execução
            TempoExecucao.iniciar();

            if (service.execute()) {
                String totalRegistrosFmt = String.format(
                    new Locale("pt", "BR"), "%,d registros", service.getTotalRegistros()
                );

                mostrarMensagem(String.format(
                        "Tabela de dados carregada com sucesso! " +
                        "Registros carregados: %s " +
                        "Tempo total: %s",
                        totalRegistrosFmt,
                        TempoExecucao.getTempoFormatado()
                ));
            } else {
                mostrarErro(service.getErr().getMessage());
            }

        } catch (Exception e) {
            // Captura qualquer exceção inesperada
            mostrarErro("Erro durante a importação de dados do arquivo: " + e.getMessage());

        } finally {
            // Marca o fim da execução, independentemente do resultado
            TempoExecucao.finalizar();
        }
    }
    //
    //
    private void itGrfTop1ActionPerformed() {
        CriarGraficoService service = new CriarGraficoService(this, 1);
        if (service.execute()) {
            mostrarMensagem("Gráfico de Pizza criado com sucesso!");
        } else {
            mostrarErro(service.getErr().getMessage());
        }
    }

    private void itGrfTop2ActionPerformed() {
        CriarGraficoService service = new CriarGraficoService(this, 2);
        if (service.execute()) {
            mostrarMensagem("Gráfico de Barras criado com sucesso!");
        } else {
            mostrarErro(service.getErr().getMessage());
        }
    }

    private void itGrfTop3ActionPerformed() {
        CriarGraficoService service = new CriarGraficoService(this, 3);
        if (service.execute()) {
            mostrarMensagem("Gráfico de Linhas criado com sucesso!");
        } else {
            mostrarErro(service.getErr().getMessage());
        }
    }

    public JPanel getPainelCentral() {
        return painelCentral;
    }
    //
    //


    private void mostrarMensagem(String msg) {
        lblMensagem.setForeground(Color.BLACK);
        lblMensagem.setText(msg);
    }

    private void mostrarErro(String msg) {
        lblMensagem.setForeground(Color.RED);
        lblMensagem.setText(msg);
    }


    public static void main(String [] args) {
        new TelaPrincipal();
    }


}
