import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//A classe filósofo representa os processos que são executados em um computador.
public class Filosofo extends Thread{
	
	private final static int PORTA = 5015;
	private final static int QUANTIDADE = 10;//Constante para quantidade de vezes que o filósofo come.
	private int comeu; //Quantidade de vezes que um filósofo comeu.
	private int estado; //Estado atual de um filósofo.
	
	List<Dado> lista; //Lista de requisições.
	private boolean garfo;
	boolean flag = false;
	
	//private int id_server; // id do server no map de endereços
	List<String> listaDeIPs; // indice = ID , conteudo = IP	
	private Servidor servidor;
	
//____________________________________________________________________________________________________________	
	//Construtor
	public Filosofo(){
		lista      = new ArrayList<>(); // Lista de requisição dos filósofos.
		listaDeIPs = new ArrayList<>(); // Lista de endereços e IDs
		
		listaDeIPs.add("200.239.139.125");
		listaDeIPs.add("200.239.139.118");

		servidor = new Servidor();
		servidor.start();
		
		this.estado = 1; //Começa pensando.
		this.comeu = 0; //Não comeu nada inicialmente.
		this.garfo = false;
	}	
	
	//Alguns getters e setters.
	public void setComeu(int i){
		this.comeu = i;
	}
	
	public int getComeu(){
		return this.comeu;
	}
	
	public void setEstado(int e){
		this.estado = e;
	}
	
	public int getEstado(){
		return this.estado;
	}
	
	public void setSituacaoGarfo(boolean g){ // atualiza a situacao do garfo ( true = disponivel )
		this.garfo = g;
	}
	
	public boolean getSituacaoGarfo(){ // retorna a situacao do garfo ( true = disponivel )
		return garfo;
	}
	

//____________________________________________________________________________________________________________		
	//Métodos dos filósofos.
	//Filsófo está coomendo (entra na seção crítica). **
	public boolean comer(){
		this.estado = 2; //Comendo.
		this.comeu++; //Comeu mais uma vez.
		
		try{
			Thread.sleep(new Random().nextInt(4995) + 5); //Espera de 5 a 5000 ms.
			System.out.println("Comi.");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	//Filósofo está pensando, aguarde um tempo. **
	public void pensar(){
		this.estado = 1; //Pensa.
		try{
			Thread.sleep(new Random().nextInt(4995) + 5); //Espera de 5 a 5000 ms.
			System.out.println("Pensando.");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Filósofo está com fome, gera intenção de comer. **
	public void comFome(){
		this.estado = 0;
		System.out.println("Com fome.");
	
	}
	
	//Pega um garfo para comer
	  public boolean pegarGarfo(){
			
			Cliente cliente = new Cliente();
			
			Dado pedido_1 = Dado.condicaoEntrar(listaDeIPs.get(0));
			Dado pedido_2 = Dado.condicaoEntrar(listaDeIPs.get(1));	
		
			Dado a = adicionarLista(pedido_1);
			Dado r = cliente.conectar(listaDeIPs.get(1),PORTA , pedido_2);
			
			if(r==null){
				return false;
			}
			
			if(a.getResposta() != 1 || r.getResposta() != 1){
				if(a.getResposta() == 1){
					r = cliente.conectar(listaDeIPs.get(1),PORTA , pedido_2);
					if(r.getResposta() == 1)
						return true;
					else
						removerLista(a);
				}else{
					a = adicionarLista(pedido_1);		
					if(a.getResposta() == 1)
						return true;
					else{
						a = Dado.condicaoSair(a.getIP());
						cliente.conectar(listaDeIPs.get(1), PORTA, a);
					}
				}
			}
			
			return true;
	
	}
	
	//Filósofo solta o garfo, saindo da seção crítica.
	public boolean soltarGarfo(){
		
		Cliente cliente = new Cliente();
		
		Dado sair = Dado.condicaoSair(listaDeIPs.get(0));
		
		Dado a = removerLista(sair);
		Dado r = cliente.conectar(listaDeIPs.get(1), PORTA, sair);
		
		if (r == null){
			return false;
		}
		
		if(a.getResposta() == 1 && r.getResposta() == 1){
			return true;
		}
		return true;
		
	}
	
	private synchronized Dado adicionarLista(Dado dado){
		
		if(lista.isEmpty()){
			lista.add(dado);
		}
		
		if(lista.get(0).getIP() == dado.getIP()){
			dado.setResposta(1);
			setSituacaoGarfo(true);
			return dado;
		}else{
			if(dado.getComeu() < lista.get(0).getComeu()){
				Dado aux = lista.get(0);
				lista.add(0,dado);
				lista.add(1,aux);
				dado.setResposta(1);
				return dado;
			}else{
				lista.add(1,dado);
				dado.setResposta(2);
				return dado;
			}
		}
	}
	
	private synchronized Dado removerLista(Dado dado){
		if(!(lista.isEmpty())){
			lista.remove(0);
			setSituacaoGarfo(false);
			dado.setResposta(1);
			return dado;
		}
		return dado;
	}
	
	
//________________________________________________________________________________________________________________	
	
	
	@Override
	public void run(){
		System.out.println("Inicializando Filosofo.");
		//Filósofo começa pensando por um determinado período de tempo.
		pensar();
		//Inicializo o servidor para escutar outros filósofos.
	
		while (comeu < QUANTIDADE){
			comFome();
			if(pegarGarfo()){
				comer();
				if(!soltarGarfo()){
					break;
				}
			}
			System.out.println(getComeu());
			pensar();
		}
		//this.servidor.fecharServidor();
		this.servidor.stop();
		System.out.println("Eu comi: " + getComeu());
	}
	
	
	public class Servidor extends Thread{
		public final static int PORTA=5014; //Abre o servidor na porta 4000.
		private ServerSocket servidor; //Socket para abertura de um servidor.
		private Socket cliente; //Socket para aceitar conexão com cliente.
		
		public boolean abrirServidor(){
			try {
				servidor = new ServerSocket(PORTA);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		public void fecharServidor (){
			try {
				this.cliente.close();
				this.servidor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run(){
			if(abrirServidor()){
				System.out.println("SERVIDOR: Servidor aberto");
			}else{
				System.out.println("SERVIDOR: Falha ao abrir o servidor.");	
			}
			while(true){
				try {
					cliente = servidor.accept();
					Thread thread = new ThreadCliente(cliente);
					thread.start();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}


	public class ThreadCliente extends Thread{
		private Socket cliente; //Socket para atender ao cliente.
		private ObjectInputStream receber; //Leitura de dados de outros filósofos.
		private ObjectOutputStream enviar; //Envio de dados para outros filósofos.
		private Dado dado;
		
		public ThreadCliente(Socket socket){
			this.cliente = socket;
		}
				
		public void run(){
				try {
					receber = new ObjectInputStream(cliente.getInputStream());
					dado = (Dado) receber.readObject();
					if(dado.getPedido() == 1){
						//Quer entrar na região crítica.
						Dado r = adicionarLista(dado);
						enviar = new ObjectOutputStream(cliente.getOutputStream());
						enviar.writeObject(r); //Envia a resposta para o cliente.
						enviar.flush();
						enviar.close();
					}else if (dado.getSair() == 1){
						//Já comeu e vai sair da região crítica.
						Dado r = removerLista(dado);
						enviar = new ObjectOutputStream(cliente.getOutputStream());
						enviar.writeObject(r);
						enviar.flush();
						enviar.close();
						setSituacaoGarfo(false);
					}
					receber.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
	}
}
