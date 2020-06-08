package p2p;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.security.DigestInputStream;
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;

public class UDPClient {
		  
	   private static String checksum(String filepath, MessageDigest md) throws IOException {

        // file hashing with DigestInputStream
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            while (dis.read() != -1) ; //empty loop to clear the data
            md = dis.getMessageDigest();
        }

        // bytes to hex
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
    	File dir = new File(path);
    	File[] files=dir.listFiles();
    	String[][] sumy = new String[files.length][2];
    	int i=0;
    	for (File file : dir.listFiles()) {
    		MessageDigest md = MessageDigest.getInstance("SHA-512");
            sumy[i][1] = checksum(file.getPath(), md);
            sumy[i][0] = file.getName();
            i++;
        }
    	
 
        String message = "Lista";
        InetAddress serverAddress = InetAddress.getByName("localhost");
        System.out.println(serverAddress);

        DatagramSocket socket = new DatagramSocket(); //Otwarcie gniazda
        byte[] stringContents = message.getBytes("utf8"); //Pobranie strumienia bajtów z wiadomosci

        DatagramPacket sentPacket = new DatagramPacket(stringContents, stringContents.length);
        sentPacket.setAddress(serverAddress);
        sentPacket.setPort(Config.PORT);
        socket.send(sentPacket);

        DatagramPacket recievePacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
        socket.setSoTimeout(1010);

        try{
            socket.receive(recievePacket);
            System.out.println("Serwer otrzymał wiadomość");
            //int length = recievePacket.getLength();
            //String message = new String(recievePacket.getData(), 0, length, "utf8");
        }catch (SocketTimeoutException ste){
            System.out.println("Serwer nie odpowiedział, więc albo dostał wiadomość albo nie...");
        }
    }
}