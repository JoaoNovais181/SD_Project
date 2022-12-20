package Servidor;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Utilizador {
    private final String username;
    private final String password;
    private ReentrantLock lock = new ReentrantLock();


    /*
        Construtor da class Utilizador

        @param user     Username do Utilizador
        @param pass     Password do Utilizador
        @param coordX   Coordenada x do Utilizador
        @param coordY   Coordenada y do Utilizador
     */
    public Utilizador(String user, String pass){
        this.username = user;
        this.password = pass;
        this.lock     = new ReentrantLock();

    }

    public String getUsername(){
        return this.username;
    }

    /*
        Método para verificar se a password coincide com a do Utilizador

        @param  pass   Password introduzida
        @return True   caso as passwords sejam iguais
                False  caso contrário
     */
    public boolean verificaPass (String pass){
        return this.password.equals(pass);
    }





}
