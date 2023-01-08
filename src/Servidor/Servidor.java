package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Servidor {
    /*
        Método que conecta o Servidor aos Clientes e inicializa os sockets para a comunicação

        @param   args          Argumentos passados aquando da inicialização
        @throws  IOException   Exceção lançada caso algo inesperado aconteça
     */
    public static void main(String[] args) throws IOException{
        ServerSocket ss = new ServerSocket(12345);
		Mapa mapa = new Mapa(20,2);
		ListaRecompensas recompensas = new ListaRecompensas();
		ReentrantLock lockReservas = new ReentrantLock();
		Condition esperaAcao = lockReservas.newCondition();
		TrabalhadorRecompensas tr = new TrabalhadorRecompensas(recompensas, mapa);
		GestorReservas gr = new GestorReservas(mapa, recompensas);

		Thread trabalhadorRecompensas = new Thread(tr);
		trabalhadorRecompensas.start();

        while(true){
            Socket cs = ss.accept();
            System.out.println("Conexão estabelecida");
            Thread worker = new Thread(new GereMensagem(cs, mapa, gr, recompensas));
            worker.start();
        }
    }
}
