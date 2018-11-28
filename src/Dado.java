import java.io.Serializable;

public class Dado implements Serializable{
	
	private String IP;
	private int pedido;
	private int sair;
	private int resposta;
	private int comeu;
	
	public Dado(){
		this.IP = null;
		this.comeu = 0;
		this.sair = 0;
	}
	
	public int getSair() {
		return sair;
	}
	
	public void setSair(int sair) {
		this.sair = sair;
	}
	
	public void setIP(String ip){
		this.IP = ip;
	}
	
	public String getIP(){
		return this.IP;
	}
	
	public void setPedido(int p){
		this.pedido = p;
	}
	
	public int getPedido(){
		return this.pedido;
	}
	
	public void setResposta(int resposta){
		this.resposta = resposta;
	}
	
	public int getResposta(){
		return this.resposta;
	}
	
	public void setComeu(int c){
		this.comeu = c;
	}
	
	public int getComeu(){
		return this.comeu;
	}
	
	public static Dado condicaoEntrar(String IP){	
		
		Dado dado = new Dado();
		dado.setIP(IP);
		dado.setPedido(1);
		dado.setResposta(0);
		return dado;
	}

	public static Dado condicaoSair(String IP){
		Dado dado = new Dado();
		dado.setIP(IP);
		dado.setSair(1);
		dado.setResposta(0);
		return dado;
	}
	
}
