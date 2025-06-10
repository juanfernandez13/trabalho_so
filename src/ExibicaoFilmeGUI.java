import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;


public class ExibicaoFilmeGUI extends JFrame {
    int N;
    int Te;
    DrawingPanel panel;

    Sistema sistema;
    DemonstradorThread demonstrador;
    ArrayList<FanThread> fans = new ArrayList<>();

    JButton addFanButton;
    JTextField tlField;
    JLabel demonstradorStatus;
    JTextArea logArea;
    private Timer repaintTimer;

    public ExibicaoFilmeGUI(int capacidade, int tempoExibicao) {
        this.N = capacidade;
        this.Te = tempoExibicao;
        this.sistema = new Sistema(capacidade);

        setTitle("Exibição do Filme - Simulação");
        setSize(1000, 700);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new DrawingPanel();
        panel.setBounds(0, 0, 1000, 500);
        add(panel);

        addFanButton = new JButton("Adicionar Fã");
        addFanButton.setBounds(20, 510, 150, 30);
        add(addFanButton);

        JLabel tlLabel = new JLabel("Tempo Lanche (s):");
        tlLabel.setBounds(180, 510, 150, 30);
        add(tlLabel);

        tlField = new JTextField("3");
        tlField.setBounds(320, 510, 50, 30);
        add(tlField);

        demonstradorStatus = new JLabel("Demonstrador: DORMINDO");
        demonstradorStatus.setBounds(20, 550, 300, 30);
        add(demonstradorStatus);

        logArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBounds(600, 510, 370, 140);
        add(scrollPane);

        demonstrador = new DemonstradorThread(capacidade, tempoExibicao, this, sistema);
        demonstrador.start();

        addFanButton.addActionListener(e -> {
            int tl = Integer.parseInt(tlField.getText());
            int id = fans.size();
            FanThread fan = new FanThread(id, tl, this, sistema, Te);
            fans.add(fan);
            fan.start();
            System.out.println("Fan " + id + " adicionado com sucesso!");
        });

        repaintTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.repaint(); // Força o redesenho do painel
            }
        });
        repaintTimer.start();

        setVisible(true);
    }

    class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.BLUE);
            g.fillRect(100, 100, 200, 200);
            g.setColor(Color.WHITE);
            g.fillRect(400, 100, 200, 200);
            g.setColor(Color.RED);
            g.fillRect(700, 100, 200, 200);

            g.setColor(Color.BLACK);
            g.drawString("Fila Laion", 170, 90);
            g.drawString("Cine Laion", 470, 90);
            g.drawString("Lanche Laion", 770, 90);



            if(demonstrador.estado == Estado.EXIBINDO) {
                long currentTime = System.currentTimeMillis();
                if ((currentTime / 200) % 2 == 0) { // Alterna a cada 200ms
                    g.setColor(Color.BLUE);
                    g.fillOval(470, 300, 30, 30);
                    g.setColor(Color.WHITE);
                    g.drawString("D", 480, 320);
                } else {
                    g.setColor(Color.RED);
                    g.fillOval(470, 300, 30, 30);
                    g.setColor(Color.BLACK);
                    g.drawString("D", 480, 320);
                }
            } else {
                g.setColor(Color.RED);
                g.fillOval(470, 300, 30, 30);
                g.setColor(Color.BLACK);
                g.drawString("D", 480, 320);
            }

            for (FanThread f : fans) {
                switch (f.estado) {
                    case AGUARDANDO:
                        g.setColor(Color.white);
                        break;
                    case AGUARDANDONOCINEMA:
                        g.setColor(Color.red);
                        break;
                    case ASSISTINDO:
                        // NOVO: Cor oscilante para o estado ASSISTINDO
                        long currentTime = System.currentTimeMillis();
                        if ((currentTime / 200) % 2 == 0) { // Alterna a cada 200ms
                            g.setColor(Color.cyan);
                        } else {
                            g.setColor(Color.red); // Ou outra cor, como Color.LIGHT_GRAY
                        }
                        break;
                    case LANCHANDO:
                        long currentTime2 = System.currentTimeMillis();
                        if ((currentTime2 / 200) % 2 == 0) { // Alterna a cada 200ms
                            g.setColor(Color.cyan);
                        } else {
                            g.setColor(Color.white); // Ou outra cor, como Color.LIGHT_GRAY
                        }
                        break;
                    case BLOQUEADO: // Caso haja um estado de bloqueio explícito
                        g.setColor(Color.DARK_GRAY);
                        break;
                    default:
                        g.setColor(Color.RED); // Cor padrão se o estado não for reconhecido
                        break;
                }
                g.fillOval(f.x, f.y, 20, 20);
                g.setColor(Color.BLACK);
                g.drawString("" + f.id, f.x + 5, f.y + 15);
            }
        }
    }

    public void atualizarEstadoFan(int id, Estado estado) {
        panel.repaint();
    }

    public void atualizarEstadoDemonstrador(Estado estado) {
        demonstradorStatus.setText("Demonstrador: " + estado);
        panel.repaint();
    }

    public void log(String texto) {
        SwingUtilities.invokeLater(() -> logArea.append(texto + "\n"));
    }
}