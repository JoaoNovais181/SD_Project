package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    /*
        Método que conecta o Servidor aos Clientes e inicializa os sockets para a comunicação

        @param   args          Argumentos passados aquando da inicialização
        @throws  IOException   Exceção lançada caso algo inesperado aconteça
     */
    public static void main(String[] args) throws IOException{
		boolean debug = false;
		for (String arg : args)
			if (arg.equals("-d"))
				debug = true;
        ServerSocket ss = new ServerSocket(12345);
		Mapa mapa = new Mapa(20,2);
		ListaRecompensas recompensas = new ListaRecompensas();
		TrabalhadorRecompensas tr = new TrabalhadorRecompensas(recompensas, mapa);
		GestorReservas gr = new GestorReservas(mapa, recompensas);

		Thread trabalhadorRecompensas = new Thread(tr);
		trabalhadorRecompensas.start();

        while(true){
            Socket cs = ss.accept();
            if (debug)
				System.out.println("Conexão estabelecida");
            Thread worker = new Thread(new GereMensagem(cs, mapa, gr, recompensas, debug));
            worker.start();
        }
    }
}
