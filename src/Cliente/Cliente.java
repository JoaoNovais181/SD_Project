package Cliente;

import java.io.IOException;
import java.net.Socket;

/**
 * Classe responsável por ligar um cliente ao servidor.
 */
public class Cliente {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        ClienteStatus cStatus = new ClienteStatus();
        Thread t1 = new Thread(new ClienteMenu(socket, cStatus));
        Thread t2 = new Thread(new ClienteReader(socket, cStatus));
        t1.start();
        t2.start();
    }
}
