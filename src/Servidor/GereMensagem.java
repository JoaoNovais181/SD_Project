package Servidor;

//import Exceptions.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

public class GereMensagem implements Runnable{
    private Socket cs;
    private DataOutputStream out;
    private DataInputStream in;
    private String active_user;
    private Map<String, Utilizador> users;
    private final ReadWriteLock l = new ReentrantReadWriteLock();
    private final Lock wl = l.writeLock();
    private final Lock rl = l.readLock();

    public GereMensagem(Socket cs) {
        this.cs = cs;
        this.active_user = null;
        this.users = new HashMap<>();
    }

    /**
     * Método para ser executado pela thread.
     */
    public void run() {
        String msg;

        try {
            out = new DataOutputStream(new BufferedOutputStream(cs.getOutputStream()));
            in = new DataInputStream(new BufferedInputStream(cs.getInputStream()));
            while (!(msg = in.readUTF()).equals("EXIT")) {
                System.out.println(msg);
                command(msg);
            }

            out.writeUTF("EXIT");
            out.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("sair");


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
                commandLogin(msg);
                break;
        
            case "REGISTER":
                commandSign(msg);
                break;
            
            case "LOGOUT":
                commandLogout();
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
    private void commandLogout() throws IOException {
        if (this.active_user != null) {
            this.active_user = null;
        }
        out.writeUTF("LOGGED OUT");
        out.flush();
    }

    /**
     * Método reponsável por fazer login de um utilizador.
     *
     * @param msg Pedido ao servidor.
     * @throws IOException
     */
    private void commandLogin(String msg) throws IOException{
        String[] args = msg.split(";");
        String password = (args.length > 2) ? args[2] : ""; // permitir passes vazias
        String user = args[1];

        if (this.users.containsKey(user) && this.users.get(user).verificaPass(password)) {
            this.active_user = args[1];

            out.writeUTF("GRANTED");
            out.flush();
        } else {
            out.writeUTF("DENIED");
            out.flush();
        }
    }

            /**
            * Método reponsável por registar um utilizador.
            *
            * @param msg Pedido ao servidor.
            */
    private void commandSign(String msg) throws IOException {

        String[] args = msg.split(";");
        System.out.println(args[1] + args[2]);
        {
            if (registarUtilizador(args[1], args[2])) {
                out.writeUTF("USER REGISTERED");
            } else {
                out.writeUTF("USER ALREADY REGISTERED");
            }
            out.flush();
        } 
    }

    public boolean registarUtilizador(String user, String password) {
        wl.lock();
        try {
            if (!this.users.containsKey(user)) {
                this.users.put(user, new Utilizador(user, password));
                return true;
            }else return false;
        } finally {
            wl.unlock();
        }
    }



}
