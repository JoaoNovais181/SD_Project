package Servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe utilziada para armazenar a informação relativa a um utilizador
 *
 * @author João Carlos Fernande Novais
 * @author Beatriz Ribeiro Monteiro
 * @author João Pedro Machado Ribeiro
 * @author Telmo José Pereira Maciel
 * */
public class Utilizador {
	/**
	 * Username do utilizador
	 * */
	private final String username;
	/**
	 * Password do utilizador
	 * */
    private final String password;
	/**
	 * Boolean que indica se o utilizador está ligado
	 * */
	private boolean loggedIn;

	/**
	 * {@link Lock} utilziado para garantir exclusão mútua
	 * */
	private Lock lock;
	/**
	 * Lista de coordenadas a notificar
	 * */
	private List<Coord> notificar;

    /**
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

	/**
	 * Método utilizado para adicionar uma coordanada a notificar
	 *
	 * @param coord coordenadas a notificar
	 *
	 * @return se foi possível adicionar ou não
	 * */
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

	/**
	 * Método utilizado para remover uma coordenada a notificar
	 * */
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

	/**
	 * Método utilizado para obter a lista de coordenadas a notificar
	 *
	 * @return Lista de coordenadas a notificar
	 * */
	public List<Coord> getNotificar()
	{
		this.lock.lock();
		try
		{
			return new ArrayList<>(this.notificar);
		}
		finally { this.lock.unlock(); }
	}

	/**
	 * Getter para o username
	 *
	 * @return username do utilizador
	 * */
    public String getUsername(){
        return this.username;
    }

    /**
        Método para verificar se a password coincide com a do Utilizador

        @param  pass   Password introduzida
        @return True   caso as passwords sejam iguais
                False  caso contrário
     */
    public boolean verificaPass (String pass){
        return this.password.equals(pass);
    }

	/**
	 * Método uitilziado para ligar o utilizador ao sistema
	 *
	 * @return se foi possível ligar o utilizador
	 * */
	public boolean login()
	{
		if (this.loggedIn)
			return false;
		this.loggedIn = true;
		return true;
	}

	/**
	 * Método utilizador para desligar o utilizador do sistema
	 * */
	public void logout()
	{
		this.loggedIn = false;
	}
}
