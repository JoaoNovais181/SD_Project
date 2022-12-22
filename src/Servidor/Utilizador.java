package Servidor;

public class Utilizador {
    private final String username;
    private final String password;

    /*
        Construtor da class Utilizador

        @param user     Username do Utilizador
        @param pass     Password do Utilizador
     */
    public Utilizador(String user, String pass){
        this.username = user;
        this.password = pass;
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
