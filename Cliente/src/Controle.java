import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Controle extends Thread {

	
	private static final int PORTA = 5010;
	private ServerSocket server;
	private Socket cliente;
	private List<String> total;
	private String ip;
	
	public Controle(){
		this.server = null;
		this.cliente = null;
		total = new ArrayList<>();
	}
	
	public void run(){
		try {
			this.server = new ServerSocket(PORTA);
			System.out.println("Server aberto.");
			System.out.println("Aguardando clientes.");
			while(true){
				cliente = server.accept();
				ip = cliente.getInetAddress().toString();
				String IP = (String) ip.subSequence(1, ip.length());
				VerificaLista(IP);
				ThreadCliente t = new ThreadCliente(cliente);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Total de computadores conectados: " + total.size());
	}

	
	public void VerificaLista(String ip){
		if(!(total.contains(ip))){
			total.add(ip);
		}
	}
	
	private class ThreadCliente extends Thread{
		
		private Socket cliente;
		private ObjectOutputStream enviar; //Envio de dados para outros filósofos.
		public ThreadCliente(Socket cliente){
			this.cliente = cliente;
		}
		
		

		public void run(){
			try {
				System.out.println("Cliente aceito");
				enviar =  new ObjectOutputStream(cliente.getOutputStream());
				enviar.writeObject(total.size());			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
						
		}
	}
}
