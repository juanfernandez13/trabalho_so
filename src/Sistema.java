import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Sistema {
    private final int capacidade;
    public final Semaphore semFila;
    public final Semaphore semExibicaoInicia;
    public final Semaphore semFilmeTermina;
    public final AtomicInteger filmeIniciado = new AtomicInteger(0);
    private final List<FanThread> filaLote = new ArrayList<>();
    private final Object mutexFila = new Object();

    public Sistema(int capacidade) {
        this.capacidade = capacidade;
        this.semFila = new Semaphore(capacidade);
        this.semExibicaoInicia = new Semaphore(0);
        this.semFilmeTermina = new Semaphore(0);
    }

    public void entrarNaFila(FanThread fan) throws InterruptedException {
        semFila.acquire();

        synchronized (mutexFila) {
            filaLote.add(fan);
        }
    }

    public void aguardarCapacidadeTotal() throws InterruptedException {
        while (true) {
            synchronized (mutexFila) {
                if (filaLote.size() >= capacidade) {
                    return;
                }
            }
            Thread.sleep(100);
        }
    }

    public void iniciarExibicao() {
        semExibicaoInicia.release(capacidade);
    }

    public void encerrarFilme() {
        semFilmeTermina.release(capacidade);

        semFila.release(capacidade);

        synchronized (mutexFila) {
            filaLote.clear();
        }
    }

    public void esperarExibicao() throws InterruptedException {
        semExibicaoInicia.acquire();
    }

    public void esperarFimDoFilme() throws InterruptedException {
        semFilmeTermina.acquire();
    }
}
