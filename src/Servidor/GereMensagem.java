package Servidor;

//import Exceptions.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GereMensagem implements Runnable{
    private Socket cs;
    private DataOutputStream out;
    private DataInputStream in;
    private String active_user;
    private Map<String, Utilizador> users;
    private Mapa mapa;
	private GestorReservas gestorReservas;
	private ListaRecompensas listaRecompensas;
	private TrabalhadorNotificacoes tn;

    public GereMensagem(Socket cs, Mapa mapa, GestorReservas gestorReservas, ListaRecompensas listaRecompensas) {
        this.cs = cs;
        this.active_user = null;
        this.users = ListaUtilizadores.getInstance();
		this.mapa = mapa;
		this.gestorReservas = gestorReservas;
		this.listaRecompensas = listaRecompensas;
		this.tn = null;
    }

    /**
     * Método para ser executado pela thread.
     */
    public void run() {
        String msg;

        try {
            out = new DataOutputStream(new BufferedOutputStream(cs.getOutputStream()));
            in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
            while (!(msg = in.readUTF()).equals("SAIR")) {
                System.out.println(msg);
                command(msg);
            }

            out.writeUTF("SAIR");
            out.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("sair");

		if (this.tn != null)
			this.tn.shutdown();

    }

    /**
     * Método que recebe uma mensagem do cliente e reencaminha para o método correto.
     *
     * @param msg Pedido recebido.
     * @throws IOException
     */
    private void command(String msg) throws IOException{
        String[] args = msg.split(";");

        switch (args[0]) {
            case "LOGIN":
                login(msg);
                break;
            case "REGISTAR":
                sign(msg);
                break;
            case "LOGOUT":
                logout();
                break;
            case "RESERVAR":
                reserva(msg);
                break;
            case "LISTARTROTINETES":
                listarTrot(msg);
                break;
            case "LISTARRECOMPENSAS":
				this.listarRecompensas(msg);
                break;
            case "ESTACIONAR":
				this.estacionar(msg);
                break;
            case "NOTIFICAR":
				this.notificar(msg);
                break;
            default: {
                this.out.writeUTF("Erro");
                out.flush();
                break;
            }
        }

    }


    /**
     * Método que inicia o processo de término de uma conexão.
     */
    private void logout() throws IOException {
        if (this.active_user != null) {
            this.active_user = null;
        }
        out.writeUTF("LOGOUT");
        out.flush();
    }

     /**
     * Método reponsável por fazer login de um utilizador.
     *
     * @param msg Pedido ao servidor.
     * @throws IOException
     */
    private void login(String msg) throws IOException{
        String[] args = msg.split(";");
        String password = (args.length > 2) ? args[2] : ""; // permitir passes vazias
        String user = args[1];

		if (!this.users.containsKey(user))
			out.writeUTF("Acesso Negado. Utilizador não existe!");
		else if (!this.users.get(user).verificaPass(password))
			out.writeUTF("Acesso Negado. Password Errada!");
		else
		{
            this.active_user = args[1];

            out.writeUTF("GRANTED");

			this.tn = new TrabalhadorNotificacoes(out, listaRecompensas, this.users.get(this.active_user));
			Thread t = new Thread(tn, "Notificador de " + this.active_user);
			t.start();
        }
		out.flush();
    }

            /**
            * Método reponsável por registar um utilizador.
            *
            * @param msg Pedido ao servidor.
            */
    private void sign(String msg) throws IOException {

        String[] args = msg.split(";");
        System.out.println(args[1] + args[2]);
        {
            if (registarUtilizador(args[1], args[2])) {
                out.writeUTF("Utilizador <" + args[1] + "> registado com sucesso!");
            } else {
                out.writeUTF("Utilizador <" + args[1] + "> já existe!");
            }
            out.flush();
        } 
    }

    public boolean registarUtilizador(String user, String password) {
		if (!this.users.containsKey(user)) {
			this.users.put(user, new Utilizador(user, password));
			return true;
		}
		else return false;
    }


    private void reserva(String msg) throws IOException {
        String[] args = msg.split(";");
        Coord c = new Coord(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        int codigoReserva = this.gestorReservas.reservar(c);

        if (codigoReserva != -1) {
            out.writeUTF("Reserva feita com sucesso!\n\tCódigo de reserva: " + codigoReserva);
            out.flush();
        }
        else {
            out.writeUTF("Não foi possível realizar essa reserva!");
            out.flush();
        }
    }


    private void listarTrot(String msg) throws IOException{
        String[] args = msg.split(";");
        Coord c = new Coord(Integer.parseInt(args[1]),Integer.parseInt(args[2]));

        int nTrots = this.mapa.trotinetesNaVizinhanca(c);
        if( nTrots != 0) {
            List<Coord> coordTrot = this.mapa.coordTrotinetesVizinhanca(c);
            StringBuilder s = new StringBuilder();
            for (Coord ct : coordTrot) {
                s.append(" ").append(ct.toString()).append(" ");
            }
            out.writeUTF(" Existem " + nTrots + " trotinetes livres na tua zona!\n Coordenadas das trotinetes: " + s + '\n');
            out.flush();
        } else {
            out.writeUTF(" Não existem trotinetes na tua zona!\n");
            out.flush();
        }

    }

	private void listarRecompensas(String msg) throws IOException
	{
		String[] args = msg.split(";");
		Coord coord = new Coord(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		List<Recompensa> lr = new ArrayList<>();
		for (Recompensa r : this.listaRecompensas.getListaRecompensas())
			if (r.getDestino().DistanceTo(coord) <= this.mapa.getD())
				lr.add(r);
		out.writeUTF("LISTARRECOMPENSAS");
		out.writeInt(lr.size());
		for (Recompensa r : lr)
		{
			out.writeUTF(r.toString());
		}
		out.flush();
	}

	private void estacionar(String msg) throws IOException
	{
		String[] args = msg.split(";");
		Coord c = new Coord(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		int codigoReserva = Integer.parseInt(args[1]);
		float valorAPagar = this.gestorReservas.estacionar(codigoReserva, c);
	
		if (valorAPagar >= 0)
		{
			out.writeUTF("Trotinete Estacionada!.\n\tValor a pagar: " + valorAPagar + "€");
			out.flush();
		}
		else
		{
			out.writeUTF("Reserva com código: " + codigoReserva + " não existe!");
			out.flush();
		}
	}

	private void notificar(String msg) throws IOException
	{
		String[] args = msg.split(";");
		Coord coord = new Coord(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		if (this.users.get(this.active_user).addNotificar(coord))
			out.writeUTF("Pedido de Notificacao Adicionado");
		else
		{
			this.users.get(this.active_user).removeNotificar(coord);
			out.writeUTF("Pedido de Notificacao Removido");
		}
		out.flush();
	}

}
