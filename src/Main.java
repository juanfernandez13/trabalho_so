import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int n = Integer.parseInt(JOptionPane.showInputDialog("Capacidade do auditório (N):"));
        int te = Integer.parseInt(JOptionPane.showInputDialog("Tempo de exibição (Te) em segundos:"));
        new ExibicaoFilmeGUI(n, te);
    }
}
