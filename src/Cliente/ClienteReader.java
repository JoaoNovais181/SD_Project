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

	private String center(int width, String padStr, String str)
	{
		int n = width - str.length();
		if (n==0) return str;
		return String.format("%0" + n/2 + "d" + "%s%0" + (n/2 + n%2) + "d", 0, str, 0).replace("0", padStr);
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
                } else if (args[0].equals("LISTARRECOMPENSAS")){
					System.out.println("HELLOOOOO");
					int nr = in.readInt();
					int n = 56;
					System.out.println("┌" + center(n,"─","") + "┐");
					String titulo = "Lista de Recompensas";
					System.out.println("│" + center(n, " ", titulo) + "│" + "\n" + "├" + center(n, "─", "") + "┤");
					for (int i=0 ; i<nr ; i++)
					{
						System.out.println("│" + center(n, " " , in.readUTF()) + "│");
					}
					System.out.println("└" + center(n,"─","") + "┘");
					this.status.setWaitingOFF();
				}else if (this.status.getWaiting()) { // está à espera de resposta
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
