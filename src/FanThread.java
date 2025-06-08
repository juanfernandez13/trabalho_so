public class FanThread extends Thread {
    public final int id;
    private final int tempoLanche;
    private final ExibicaoFilmeGUI gui;
    private final Sistema sistema;

    public Estado estado = Estado.AGUARDANDO;

    public int x = 100;
    public int y = 150;

    public FanThread(int id, int tempoLanche, ExibicaoFilmeGUI gui, Sistema sistema) {
        this.id = id;
        this.tempoLanche = tempoLanche;
        this.gui = gui;
        this.sistema = sistema;
    }

    public void moverPara(int destinoX, int destinoY) throws InterruptedException {
        while (x != destinoX || y != destinoY) {
            if (x < destinoX) x++;
            else if (x > destinoX) x--;

            if (y < destinoY) y++;
            else if (y > destinoY) y--;

            gui.atualizarEstadoFan(id, estado); // Força repaint da GUI
            Thread.sleep(5);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Entrar na fila (posição inicial)
                estado = Estado.AGUARDANDO;
                gui.atualizarEstadoFan(id, estado);
                moverPara(280 - (id % 5) * 25 , 150 + (id / 5) * 25);  // Espaçamento vertical por fã
                sistema.entrarNaFila(this);

                // Esperar o início da exibição - mover para auditório
                sistema.esperarExibicao();
                moverPara(430 + (id % 5) * 25, 150 + (id / 5) * 25);  // Posicionar no auditório
                estado = Estado.ASSISTINDO;
                gui.atualizarEstadoFan(id, estado);

                // Esperar o filme acabar
                sistema.esperarFimDoFilme();
                estado = Estado.AGUARDANDO;
                gui.atualizarEstadoFan(id, estado);

                // Ir para o lanche
                moverPara(770 + (id % 5) * 25, 150 + (id / 5) * 25);
                estado = Estado.LANCHANDO;
                gui.atualizarEstadoFan(id, estado);
                gui.log("Fã " + id + " está lanchando por " + tempoLanche + "s");
                Thread.sleep(tempoLanche * 1000);
                estado = Estado.AGUARDANDO;
                moverPara(770 + (id % 5) * 25, 400);
                moverPara(80, 350);
                moverPara(80, 150);
                // Voltar para a fila para novo ciclo
                gui.atualizarEstadoFan(id, estado);
                gui.log("Fã " + id + " voltou do lanche");
                moverPara(280 - (id % 5) * 25 , 150 + (id / 5) * 25);
            }
        } catch (InterruptedException e) {
            gui.log("Fã " + id + " interrompido.");
        }
    }
}
