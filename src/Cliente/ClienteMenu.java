package Cliente;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe responsável por ler a informação proveniente do cliente e enviar para
 * o Servidor.
 */
public class ClienteMenu implements Runnable {
    private int menu_status;
    private Socket cs;
    private ClienteStatus status;
    private DataOutputStream out;
    private DataInputStream in;

    public ClienteMenu(Socket cs, ClienteStatus status) {
        menu_status = 0;
        this.status = status;
        this.cs = cs;
    }

	public static String pad(int width, String padStr, String str)
	{
		int n = width - str.length();
		if (n==0) return str;
		return String.format("%0" + n + "d", 0).replace("0", padStr) + str;
	}

	public static String center(int width, String padStr, String str)
	{
		int n = width - str.length();
		if (n==0) return str;
		return String.format("%0" + n/2 + "d" + "%s%0" + (n/2 + n%2) + "d", 0, str, 0).replace("0", padStr);
	}


	private void printOpcoes (String titulo, String[] opcoes)
	{
		int n = 50;
		int len = (""+opcoes.length).length() + 3;
		System.out.println("┌" + center(n, "─", "") + "┐");
        System.out.println("│" + center(n, " ", titulo) + "│");
		System.out.println("├" + pad(len, "─", "┬") + center(n-len, "─", "") + "┤");

		for (int i=1 ;  i<opcoes.length ; i++)
			System.out.println("│" + String.format(" %" + (len-3) + "d │", i) + center(n-len," ", opcoes[i-1]) + "│");

		System.out.println("│"+ String.format(" %" + (len-3) + "d │", 0) + center(n-len," ", opcoes[opcoes.length-1]) + "│");
        System.out.println("└" + pad(len, "─", "┴") + center(n-len, "─", "") + "┘");
        System.out.print(" Opção: ");
	}

    /**
     * Método usado para desenhar os menus.
     */
    public void printmenu() {
        switch (this.menu_status) {
            case 0: // escolher registo/login
                this.printOpcoes("Menu de Inicio de Sessão", new String[] {"Log In", "Registar", "Sair"});
                break;

            case 1: // tem sessão iniciada
                this.printOpcoes("Menu de Funcionalidades", new String[] {	"Fazer Reserva",
																			"Listar Trotinetes Livres",
																			"Listar Recompensas",
																			"Estacionar Trotinete",
																			"Notificar Recompensas",
																			"Logout"});
                break;

        }
    }

    /**
     * Método para ler a decisão do cliente em cada menu.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void readmenu() throws IOException, InterruptedException {
        switch (this.menu_status) {
            case 0:
                menu_one();
                break;
            case 1:
                menu_two();
                break;
            default:
                break;
        }
    }

    /**
     * Método para interpretar a decisão do cliente no menu 1.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void menu_one() throws IOException, InterruptedException {
        int option = this.readOpt();
		
        switch (option) {
            case 0:
                server_request("SAIR");
                break;
            case 1:
                login();
                break;
            case 2:
                signup();
                break;
            default: {
                System.out.print(" Opção inválida!\n Escolha uma nova opção: ");
                menu_one();
                break;
            }
        }
    }

    /**
     * Método para interpretar a decisão do cliente no menu 2.
     */
    public void menu_two() throws IOException { // LOGGED IN
        int option = this.readOpt();
        switch (option) {
            case 0:
                logout();
                break;
            case 1:
                reservar();
                break;
            case 2:
                listartrotinetes();
                break;
            case 3:
                listarrecompensas();
                break;
            case 4:
                estacionar();
                break;
            case 5:
                notificar();
                break;
            default: {
                System.out.print("Opção inválida. Escolha uma nova opção: ");
                menu_two();
            }
        }
    }

    /**
     * Método para fazer login.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void login() throws IOException, InterruptedException {
        String username, password;
        Scanner is = new Scanner(System.in);

        System.out.print(" Username: ");
        username = is.nextLine();
        if (username.isEmpty())
            return;

        System.out.print(" Password: ");
        password = is.nextLine();

        String result = String.join(";", "LOGIN", username, password);
        this.server_request(result);

        if (this.status.getLogin()) { //caso o login seja bem sucedido
            this.menu_status++;
        }

    }

    /**
     * Método para registar.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void signup() throws IOException, InterruptedException {
        String username, password;
        Scanner is = new Scanner(System.in);

        System.out.print(" Username: ");
        username = is.nextLine();
        if (username.isEmpty())
            return;

        System.out.print(" Password: ");
        password = is.nextLine();

        String result = String.join(";", "REGISTAR", username, password);
        this.server_request(result);

    }

    /**
     * Método para dar logout.
     */
    public void logout() {
        try {
            server_request("LOGOUT");
            this.menu_status = 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para fazer reserva de uma trotinete.
     */
    public void reservar() {
        String x,y;
        Scanner is = new Scanner(System.in);

        System.out.print(" X: ");
        x = is.nextLine();
        if (x.isEmpty())
            return;

        System.out.print(" Y: ");
        y = is.nextLine();
        if (y.isEmpty())
            return;
        try {
            String result = String.join(";", "RESERVAR", x, y);
            this.server_request(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para apresentar todas as trotinetes livres.
     */
    public void listartrotinetes() {
        String x,y;
        Scanner is = new Scanner(System.in);

        System.out.print(" X: ");
        x = is.nextLine();
        if (x.isEmpty())
            return;

        System.out.print(" Y: ");
        y = is.nextLine();

        if (y.isEmpty())
            return;

        try {
            String result = String.join(";", "LISTARTROTINETES",x,y);
            this.server_request(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para listar as recompensas disponiveis.
     */
    public void listarrecompensas() {
        String x,y;
        Scanner is = new Scanner(System.in);

        System.out.print(" X: ");
        x = is.nextLine();
        if (x.isEmpty())
            return;


        System.out.print(" Y: ");
        y = is.nextLine();
        if (y.isEmpty())
            return;

        try {
            String result = String.join(";", "LISTARRECOMPENSAS",x,y);
            server_request(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para estacionar uma trotinete.
     */
    public void estacionar() {
        String cod, x, y;
        Scanner is = new Scanner(System.in);

        System.out.print("Código Reserva: ");
        cod = is.nextLine();

        if (cod.isEmpty())
            return;


        System.out.print("X: ");
        x = is.nextLine();
        if (x.isEmpty())
            return;

        System.out.print("Y: ");
        y = is.nextLine();
        if (y.isEmpty())
            return;

        try {
            String result = String.join(";", "ESTACIONAR", cod, x, y);
            this.server_request(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para notificar sobre recompensas.
     */
    public void notificar() {
        String x, y;
        Scanner is = new Scanner(System.in);

        System.out.print("X: ");
        x = is.nextLine();
        if (x.isEmpty())
            return;

        System.out.print("Y: ");
        y = is.nextLine();
        if (y.isEmpty())
            return;

        try {
            String result = String.join(";", "NOTIFICAR", x, y);
            this.server_request(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para enviar pedidos ao servidor.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void server_request(String msg) throws IOException, InterruptedException {
        this.out.writeUTF(msg);
        this.out.flush();
        this.status.waitForResponse();
    }

    /**
     * Método para ler um inteiro digitado pelo utilizador.
     *
     * @return Valor inteiro introduzido pelo utilizador.
     */
    public int readOpt() {
        int option = -1;
        boolean valid = false;
        String msg;
        Scanner is = new Scanner(System.in);

        while (!valid) {
            try {
                msg = is.nextLine();
                option = Integer.parseInt(msg);
                valid = true;
            } catch (NumberFormatException e) {
                System.out.print(" Input inválido! Insira um dígito.\n Opção: ");
            }
        }

        return option;
    }

    /**
     * Método que é executado pela thread.
     */
    public void run() {
        try {
            this.out = new DataOutputStream(new BufferedOutputStream(cs.getOutputStream()));

            while (!this.status.isExited()) {
                if (this.status.getLogin()){
                    this.menu_status=1;
                } else if (!this.status.getLogin()) this.menu_status = 0;
                printmenu();
                readmenu();
            }
            System.out.println(" Saindo...");
        } catch (IOException | InterruptedException e) {
            System.out.print("error ");
            e.printStackTrace();

        }
    }

}
