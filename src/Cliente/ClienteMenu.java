package Cliente;

import java.io.BufferedOutputStream;
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

    public ClienteMenu(Socket cs, ClienteStatus status) {
        menu_status = 0;
        this.status = status;
        this.cs = cs;
    }

    /**
     * Método usado para desenhar os menus.
     */
    public void menu_draw() {
        switch (this.menu_status) {
            case 0:
                System.out.println("1 - Log In\n2 - Registar\n0 - Sair");
                break;

            case 1: // está logged
                System.out.println(
                        "1 - Fazer reserva\n2 - Listar trotinetes livres\n3 - Listar recompensas\n4 - Estacionar\n5 - Notificar\n0 - Logout");
                break;

        }
    }

    /**
     * Método para ler a decisão do cliente em cada menu.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void read_menu_output() throws IOException, InterruptedException {
        switch (this.menu_status) {
            case 0:
                menu_one_output();
                break;
            case 1:
                menu_two_output();
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
    public void menu_one_output() throws IOException, InterruptedException {
        int option = this.readOpt();

        switch (option) {
            case 0:
                server_request("EXIT");
                break;
            case 1:
                menu_one_login();
                break;
            case 2:
                menu_one_signup();
                break;
            default: {
                System.out.println("Por favor insira um número das opções dadas");
                menu_one_output();
                break;
            }
        }
    }

    /**
     * Método para interpretar a decisão do cliente no menu 2.
     */
    public void menu_two_output() { // LOGGED IN
        int option = this.readOpt();
        switch (option) {
            case 0:
                menu_two_logout();
                break;
            case 1:
                menu_two_reservar();
                break;
            case 2:
                menu_two_listartrotinetes();
                break;
            case 3:
                menu_two_listarrecompensas();
                break;
            case 4:
                menu_two_estacionar();
                break;
            case 5:
                menu_two_notificar();
                break;
            default: {
                System.out.println("Por favor insira um número das opções dadas");
                menu_two_output();
            }
        }
    }

    /**
     * Método para fazer login.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void menu_one_login() throws IOException, InterruptedException {
        String username, password;
        Scanner is = new Scanner(System.in);

        System.out.print("Username: ");
        username = is.nextLine();
        if (username.isEmpty())
            return;

        System.out.print("Password: ");
        password = is.nextLine();

        String result = String.join(";", "LOGIN", username, password);
        this.server_request(result);

        if (this.status.getLogin()) {
            // checkUserMsg(username); // para fazer os avisos
            this.menu_status++;
        }

    }

    /**
     * Método para registar.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void menu_one_signup() throws IOException, InterruptedException {
        String username, password;
        Scanner is = new Scanner(System.in);

        System.out.print("Username: ");
        username = is.nextLine();
        if (username.isEmpty())
            return;

        System.out.print("Password: ");
        password = is.nextLine();

        String result = String.join(";", "REGISTER", username, password);
        this.server_request(result);

    }

    /**
     * Método para dar logout.
     */
    public void menu_two_logout() {
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
    public void menu_two_reservar() {
        /*
        try {

        } //catch (IOException | InterruptedException e) {
            //e.printStackTrace();
        }*/
    }

    /**
     * Método para apresentar todas as trotinetes livres.
     */
    public void menu_two_listartrotinetes() {
        /*try {

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Método para listar as recompensas disponiveis.
     */
    public void menu_two_listarrecompensas() {
        /*try {

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Método para estacionar uma trotinete.
     */
    public void menu_two_estacionar() {
        /*try {

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Método para notificar sobre recompensas.
     */
    public void menu_two_notificar() {
        /*try {

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
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
                System.out.println("Input inválido. Insira um dígito.\n");
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
                this.menu_status = 0;
                menu_draw();
                read_menu_output();
            }
            System.out.println("Exiting ...");
        } catch (IOException | InterruptedException e) {
            System.out.println("error");
            e.printStackTrace();

        }
    }

}
