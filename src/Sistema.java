import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Sistema {
    private final int capacidade;
    public final Semaphore semFila;
    public final Semaphore aguadandoLotacao;
    public final Semaphore semExibicaoInicia;
    public final Semaphore semFilmeTermina;

    public Sistema(int capacidade) {
        this.capacidade = capacidade;
        this.semFila = new Semaphore(capacidade);
        this.semExibicaoInicia = new Semaphore(0);
        this.semFilmeTermina = new Semaphore(0);
        this.aguadandoLotacao = new Semaphore(0);
    }

    public int sleepWork(int durationMs) {
        if (durationMs <= 0) {
            return 0;
        }

        long startTime = System.currentTimeMillis();
        long endTime = startTime + durationMs;

        Random random = new Random();
        int lastGeneratedNumber = 0;

        while (System.currentTimeMillis() < endTime) {
            lastGeneratedNumber = random.nextInt(1000);
        }

        return lastGeneratedNumber;
    }

    public boolean temVaga() {
        return semFila.availablePermits() > 0;
    }

    public void entrarNaFila() throws InterruptedException {
        semFila.acquire();
        if(!temVaga()) {
            aguadandoLotacao.release();
        }
    }

    public void aguardarCapacidadeTotal() throws InterruptedException {
        aguadandoLotacao.acquire();
        sleepWork(1200);
    }

    public void iniciarExibicao() {
        semExibicaoInicia.release(capacidade);
    }

    public void encerrarFilme() {
        semFilmeTermina.release(capacidade);

        semFila.release(capacidade);
    }

    public void esperarExibicao() throws InterruptedException {
        semExibicaoInicia.acquire();
    }

    public void esperarFimDoFilme() throws InterruptedException {
        semFilmeTermina.acquire();
    }
}
