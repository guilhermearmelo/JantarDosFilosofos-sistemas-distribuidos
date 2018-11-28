import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Debug {

	public static void main(String[] args) {
		String IP = "200.239.139.123";
		ObjectInputStream in;
		Socket socket;
		int total=0;		
		
		try {
			while(total != 5){
				socket = new Socket(IP,5010);
				in = new ObjectInputStream(socket.getInputStream());
				total = (int) in.readObject();
			}
			if(total == 5){
				Filosofo filosofo = new Filosofo();
				filosofo.start();
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
