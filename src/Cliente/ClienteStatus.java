package Cliente;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe responsável por guardar informação sobre o estado.
 */
public class ClienteStatus {
    private boolean login;
    private boolean waitingForResponse;
    private boolean exited;
    private final Lock l = new ReentrantLock();
    private final Condition condLog = l.newCondition();

    public ClienteStatus() {
        this.login = false;
        this.waitingForResponse = false;
        this.exited = false;
    }

    /**
     * Método para adicionar informação de login.
     */
    public void login() {
        l.lock();
        try {
            this.login = true;
        } finally {
            l.unlock();
        }
    }

    /**
     * Método para adicionar informação de login.
     */
    public void logout() {
        l.lock();
        try {
            this.login = false;
        } finally {
            l.unlock();
        }
    }

    /**
     * Método para verificar se utilizador se encontra logado.
     *
     * @return boolean
     */
    public boolean getLogin() {
        l.lock();
        try {
            return this.login;
        } finally {
            l.unlock();
        }

    }

    /**
     * Método para verificar se utilizador se encontra à espera de resposta.
     *
     * @return boolean
     */
    public boolean getWaiting() {
        try {
            l.lock();
            return this.waitingForResponse;
        } finally {
            l.unlock();
        }
    }

    /**
     * Método para retirar utilizador de espera.
     */
    public void setWaitingOFF() {
        l.lock();
        try {
            this.waitingForResponse = false;
            condLog.signalAll();
        } finally {
            l.unlock();
        }
    }

    /**
     * Método para utilizador esperar por resposta do Servidor.
     */
    public void waitForResponse() throws InterruptedException {
        l.lock();
        try {
            this.waitingForResponse = true;
            while (this.waitingForResponse) {
                condLog.await();
            }
        } finally {
            l.unlock();
        }
    }

    /**
     * Método para adicionar informação que utilizador saiu.
     */
    public void exited() {
        l.lock();
        try {
            this.exited = true;
        } finally {
            l.unlock();
        }
    }

    /**
     * Método para verificar se utilizador pretende sair.
     *
     * @return boolean
     */
    public boolean isExited() {
        l.lock();
        try {
            return this.exited;
        } finally {
            l.unlock();
        }
    }

}
