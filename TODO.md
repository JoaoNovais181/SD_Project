# Coisas a fazer para o trabalho

## Funcionalidades

1. Fazer autenticação e registo de um utilizador (coisa mais facil do trabalho a meu ver)

2. Listagem de locais onde existem trotinetes livres até uma distancia fixa D (a nossa escolha, por ex D=2)

3. Listagem das recompensaas com origem até uma distância fixa D de um determinado local, dada por pares origem-destino

4. Reserva de uma trotinete livre, o mais perto possível de um determinado local, limitado a uma distancia fixa D 

	4.1. Servidor responde com local e código de reserva caso sucesso
	
	4.2. Servidor responde com codigo de insucesso caso não seja possível reservar (por ex. não há trotinetes nesse raio de localizações)

5. Estacionamento de uma trotinete dando o código de reserva e o local.

	5.1. O servidor informa o cliente do custo da viagem, em função do tempo passado desde a reserva e a distância percorrida
	
	5.2. Caso a viagem tenha uma recompensa associada é informado o valor da mesma (a aplicabilidade da mesma é avliada no estacionamento, de acordo com a lista de recompensas que exista nesse determinado momento)

6. Um cliente pode pedir para ser notificado quando apareçam recompensas com origem a menos de uma distância fixa D de um determinado local.

	6.1. As notificações poedm ser enviadas muito mais tarde, devendo ser possivel o cliente prosseguir com outras operações.
	
	6.2. O cliente pode cancelar os pedidos de notificação.

---

## Coisas a fazer mediante as Funcionalidades

1. Servidor que pode receber os varios tipos de pedido (login, logout, registo, listar trotinetes, listar recompensas, reservar trotinete, estacionar trotinete, pedidos de notificacao)

2. Cliente que comece por fazer o login/registo, apresente o menu com as opções ao Utilizador e envie os pedidos para o Servidor

3. ___Minha Ideia___ Fazer uma classe para o mapa de trotinetes, onde tratamos de toda a exclusão mútua necessária (podemos ter locks de leitura e escrita)

	3.1. Tive a ideia de para a representação do mapa utilizar uma lista de listas de trotinetes (List<List<Trotinete>>), assim em cada ponto teriamos a lista com todas as trotinetes livres
	
	3.2. Visto que as trotinetes não tem muita informação, podia ser a mesma ideia de cima mas com List<List<Integer>>, e cada posição teria o número de trotinetes livres (podia ser array estático também)

4. Necessário haver uma thread que fique seja notificada sempre que uma trotinete é estacionada e faz o cálculo das recompensas (retirar algumas e adicionar outras)

5. Talvez uma classe que represente uma reserva de uma trotinete (com data de reserva, a trotinete em questão (caso usemos a solução das trotinetes), local de onde saiu a trotinete, código de reserva) e guardar isso no servidor

6. Estacionar uma trotinete, ou seja, uma classe especifica (ou nao) para estacionar as trotinetes, que faz o calculo do custo da viagem e usa a thread das recompensas para verificar se tem recompensa associada

7. ___Minha Ideia___ Fazer uma classe de lista de recompensas, que trata dentro dela da exclusão mútua no que toca às recompensas, e pode ser partilhada entre várias threads

8. Quando um cliente pede para ser notificado podemos criar uma thread nova que verifique sempre que uma trotinete é estacionada se existem recompensas na área que o cliente pediu



RESERVAR;10;5\n

Reservar -> 10, 5 -> mapa.reserva(10,5) -> retornar Reserva(data.now(), 10 , 6, 109621) se deu || return Reserva(1)

LISTARRECOMPENSAS;10;5\n

LOGIN;joaoPichudo69;passeXD1\n

LOGOUT;joaoPichudo69\n

REGISTAR;joaoPichudo69,passeXD1\n

LISTARRECOMPENSAS;10;5\n

ESTACIONAR;109621;1;1\n

NOTIFICAR;10;5\n
