public class DemonstradorThread extends Thread {
    private final int capacidade;
    private final int tempoExibicao;
    private final ExibicaoFilmeGUI gui;
    private final Sistema sistema;

    public Estado estado = Estado.BLOQUEADO;

    public DemonstradorThread(int capacidade, int tempoExibicao, ExibicaoFilmeGUI gui, Sistema sistema) {
        this.capacidade = capacidade;
        this.tempoExibicao = tempoExibicao;
        this.gui = gui;
        this.sistema = sistema;
    }

    @Override
    public void run() {
        while (true) {
            try {
                estado = Estado.DORMINDO;
                gui.atualizarEstadoDemonstrador(estado);

                sistema.aguardarCapacidadeTotal();
                sistema.iniciarExibicao();
                estado = Estado.EXIBINDO;
                gui.atualizarEstadoDemonstrador(estado);
                gui.log("Filme começou a ser exibido");


                //Thread.sleep(tempoExibicao * 1000);
                sistema.sleepWork(tempoExibicao * 1000);
                gui.log("Filme terminou");
                sistema.encerrarFilme();

            } catch (InterruptedException e) {
                gui.log("Demonstrador interrompido");
                break;
            }
        }
    }
}
