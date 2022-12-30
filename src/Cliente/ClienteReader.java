package Cliente;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Classe responsável por ler a informação proveniente do servidor.
 */
public class ClienteReader implements Runnable {
    private Socket cs;
    private DataInputStream in;
    private ClienteStatus status;

    public ClienteReader(Socket cs, ClienteStatus status) {
        this.cs = cs;
        this.status = status;
    }

    /**
     * Método para ser executado pela thread.
     */
    public void run() {
        String msg;
        String[] args;
        try {
            this.in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
            while (!(args = (msg = this.in.readUTF()).split(";"))[0].equals("SAIR")) {
                switch (args[0]) {
                    case "GRANTED" -> this.status.login();
                    case "LOGOUT" -> this.status.logout();
                }
                if (args[0].equals("GRANTED")) { // quando dá login
                    System.out.println("Sessão Iniciada com Sucesso!");
                    this.status.setWaitingOFF();
                } else if (this.status.getWaiting()) { // está à espera de resposta
                    System.out.println(msg);
                    this.status.setWaitingOFF();
                } else { // mensagens do servidor só
                    System.out.println("MESSAGE FROM SERVER: " + msg);
                }
            }
            this.status.exited();
            if (this.status.getWaiting()) {
                this.status.setWaitingOFF();
            }


        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
