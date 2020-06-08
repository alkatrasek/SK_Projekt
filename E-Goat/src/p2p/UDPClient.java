package p2p;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.security.DigestInputStream;
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;

public class UDPClient {
		  
	   //Funkcja spawdzająca sumę kontrolną
	   private static String checksum(String filepath, MessageDigest md) throws IOException {

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            while (dis.read() != -1) ;
            md = dis.getMessageDigest();
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();

	   }
	   
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
    	Scanner scan = new Scanner(System.in);
    	System.out.println("Podaj ściezke folderu, który chcesz udostępnić:");
    	String path = scan.nextLine();
    	System.out.println("Czekaj...");
    	File dir = new File(path);
    	File[] files=dir.listFiles();
    	String[][] sumy = new String[files.length][2];
    	int i=0;
    	//Wczytywanie nazw plików i sum kontrolnych do tablicy
    	for (File file : dir.listFiles()) {
    		MessageDigest md = MessageDigest.getInstance("SHA-512");
            sumy[i][1] = checksum(file.getPath(), md);
            sumy[i][0] = file.getName();
            i++;
        }
    	
    	//Wysłanie komendy do serwera w celu podania mu listy plików
        String message = "Lista";
        InetAddress serverAddress = InetAddress.getByName("localhost");
        System.out.println(serverAddress);

        DatagramSocket socket = new DatagramSocket();
        byte[] stringContents = message.getBytes("utf8");

        DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
        sentPacket.setAddress(serverAddress);
        sentPacket.setPort(Config.PORT);
        socket.send(sentPacket);
        
        //Sporządzanie i wysyłanie listy plików
        String odpowiedz = new String();
    	for (int j=0; j<sumy.length; j++)
    	{
    		odpowiedz=odpowiedz+sumy[j][0]+"\t"+sumy[j][1]+"\n";
    	}
    	
    	stringContents = odpowiedz.getBytes("utf8");
        sentPacket = new DatagramPacket(stringContents, stringContents.length);
        sentPacket.setAddress(serverAddress);
        sentPacket.setPort(Config.PORT);
        socket.send(sentPacket);
        
        //Odbieranie potwierdzenia od serwera
        DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
        socket.setSoTimeout(1010);

        try{
            socket.receive(recievePacket);
            System.out.println("Serwer potwierdza otrzymanie danych");
        }catch (SocketTimeoutException ste){
            System.out.println("Serwer nie odpowiedział !!!");
        }
        
        while(true)
	        {
	        //Wysyłanie do serwera sumy kontrolnej pliku
	        System.out.println("Podaj sumę kontrolną pliku, który chciałbyś pobrać:");
	    	String sum = scan.nextLine();
	    	stringContents = sum.getBytes("utf8");
	        sentPacket = new DatagramPacket(stringContents, stringContents.length);
	        sentPacket.setAddress(serverAddress);
	        sentPacket.setPort(Config.PORT);
	        socket.send(sentPacket);
	        
	        //Wczytywanie wiadomości zwortnej z serwera
	        recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
	        socket.setSoTimeout(1010);
	
	        try{
	            socket.receive(recievePacket);
	            int length = recievePacket.getLength();
	            message = new String(recievePacket.getData(), 0, length, "utf8");
	            if (message.equals("Nie ma takiego pliku")) System.out.println(message);
	            else break;
	        }catch (SocketTimeoutException ste){
	            System.out.println("Serwer nie odpowiedział !!!");
	        }
        }
        System.out.println(message+"Wybierz jeden adres i przekopiuj go wraz z portem.");
        String adresKlienta = scan.nextLine();
    }
}