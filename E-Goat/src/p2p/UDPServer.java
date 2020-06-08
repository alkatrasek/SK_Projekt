package p2p;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

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
            	//Wczytywanie listy plików
            	byteResponse = "OK".getBytes("utf8");
            }
            
            else
            {
            	//Sporządzenie listy klientów posiadających dany plik i wysłanie jej
            	byteResponse = "Lista użytkowników posiadających plik".getBytes("utf8");
            }
            
            DatagramPacket response
                    = new DatagramPacket(
                        byteResponse, byteResponse.length, address, port);

            datagramSocket.send(response);
        }
    }
}