package Servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Utilizador {
    private final String username;
    private final String password;
	private boolean loggedIn;

	private Lock lock;
	private List<Coord> notificar;

    /*
        Construtor da class Utilizador

        @param user     Username do Utilizador
        @param pass     Password do Utilizador
     */
    public Utilizador(String user, String pass){
        this.username = user;
        this.password = pass;
		this.notificar = new ArrayList<>();
		this.lock = new ReentrantLock();
		this.loggedIn = false;
	}

	public boolean addNotificar(Coord coord)
	{
		this.lock.lock();
		try
		{
			if (!this.notificar.contains(coord))
				this.notificar.add(coord);
			else
				return false;
			return true;
		} 
		finally { this.lock.unlock(); }
	}

	public void removeNotificar(Coord coord)
	{
		this.lock.lock();
		try
		{
			if (this.notificar.contains(coord))
				this.notificar.remove(coord);
		}
		finally { this.lock.unlock(); }
	}

	public List<Coord> getNotificar()
	{
		this.lock.lock();
		try
		{
			return new ArrayList<>(this.notificar);
		}
		finally { this.lock.unlock(); }
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

	public boolean login()
	{
		if (this.loggedIn)
			return false;
		this.loggedIn = true;
		return true;
	}

	public void logout()
	{
		this.loggedIn = false;
	}
}
