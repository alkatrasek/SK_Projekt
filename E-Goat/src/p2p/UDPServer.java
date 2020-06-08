package p2p;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
	
	static List<Plik> pliki = new ArrayList<Plik>();

    public static void main(String[] args) throws Exception{

        //Otwarcie gniazda z okreslonym portem
        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

        while (true){

            DatagramPacket receivedPacket
                    = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);

            datagramSocket.receive(receivedPacket);

            int length = receivedPacket.getLength();
            String message = new String(receivedPacket.getData(), 0, length, "utf8");

            // Port i host który wysłał nam zapytanie
            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            
            byte[] byteResponse = "".getBytes("utf8");

            if (message.equals("Lista"))
            {
            	datagramSocket.receive(receivedPacket);
                length = receivedPacket.getLength();
                message = new String(receivedPacket.getData(), 0, length, "utf8");
                String[] linijki = message.split("\n");
                for (String linijka : linijki)
                {
                	int p = 0;
                	String[] L = linijka.split("\t");
                	for (Plik plik : pliki)
                	{
                		if (L[1].equals(plik.getSuma()))
                		{
                			plik.dodajAdres(address.toString()+"\t"+port);
                			p=1;
                		}
                	}
                	if (p==0)
                	{
                		pliki.add(new Plik(L[0], L[1], address.toString()+"\t"+port));
                	}
                }
                byteResponse = "OK".getBytes("utf8");
            }
            
            else
            {
            	List<String> a = new ArrayList<String>();
            	for (Plik plik : pliki)
            	{
            		if (message.equals(plik.getSuma())) a=plik.getAdresy();
            	}
            	String odpowiedz = new String("Lista adresów i portów klientów, którzy posiadaja ten plik:\n");
            	for (String s : a)
            	{
            		odpowiedz=odpowiedz+s+"\n";
            	}
            	if (a.isEmpty()) odpowiedz="Nie ma takiego pliku";
            	byteResponse = odpowiedz.getBytes("utf8");
            }
            
            DatagramPacket response
                    = new DatagramPacket(
                        byteResponse, byteResponse.length, address, port);

            datagramSocket.send(response);
        }
    }
}