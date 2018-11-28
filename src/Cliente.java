import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente{
	
	//Atributos.
	private String ip; //IP para se conectar em um servidor.
	private int porta; //Porta para enviar as informações.
	
	private Socket socket; //Socket para a conexão de cliente.
	private ObjectInputStream receber; //Leitura de dados de outros filósofos.
	private ObjectOutputStream enviar; //Envio de dados para outros filósofos.
	
	//------------------------------------------------------------------------------------------------------------------------
	
	//Métodos da classe.
	
	//Construtor da classe, inicializa a conexão do cliente com o servidor.
	public Cliente (){
		this.receber = null;
		this.enviar = null;
		this.ip = null;
	}	
	//Se conecta a um servidor, ou seja, se conecta a um filosofo.
	public Dado conectar(String ip, int porta,Dado dado){
		
		this.ip = ip;
		this.porta = porta;
		Dado r;
		try{
			this.socket = new Socket (this.ip,this.porta);
			enviar = new ObjectOutputStream(socket.getOutputStream());
			enviar.writeObject(dado);
			enviar.flush();
			receber = new ObjectInputStream(socket.getInputStream());
			r = (Dado) receber.readObject();
			return r;
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
	}
	//Obtém o IP do cliente.
	public String getIP(){
		String s=null;
		
		try{
			s = InetAddress.getLocalHost().getHostAddress();
		}catch(Exception e){
			e.printStackTrace();
		}
		return s;
	}

	public void fechar(){
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
