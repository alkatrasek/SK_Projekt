package p2p;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
	
	private static List<File> files = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        //Otwarcie gniazda z okreslonym portem
        DatagramSocket datagramSocket = new DatagramSocket(Config.PORT);

        while (true) {
            DatagramPacket receivedPacket = new DatagramPacket( new byte[Config.BUFFER_SIZE], Config.BUFFER_SIZE);
            datagramSocket.receive(receivedPacket);

            int length = receivedPacket.getLength();
            String message = new String(receivedPacket.getData(), 0, length, StandardCharsets.UTF_8);

            // Port i host który wysłał nam zapytanie
            InetAddress address = receivedPacket.getAddress();
            int port = receivedPacket.getPort();
            
            byte[] byteResponse;

            if (message.equals("Lista")) {
            	datagramSocket.receive(receivedPacket);
                length = receivedPacket.getLength();
                message = new String(receivedPacket.getData(), 0, length, StandardCharsets.UTF_8);
                String[] lines = message.split("\n");
                for (String line : lines) {
                	int p = 0;
                	String[] splitLines = line.split("\t");
                	for (File file : files) {
                		if (splitLines[1].equals(file.getSum())) {
							file.addAddress(address.toString() + "\t" + port);
                			p = 1;
                		}
                	}
                	if (p == 0) {
                		files.add(new File(splitLines[1], address.toString() + "\t" + port));
                	}
                }
                byteResponse = "OK".getBytes(StandardCharsets.UTF_8);
            }
            
            else {
            	List<String> a = new ArrayList<>();
            	for (File file : files) {
            		if (message.equals(file.getSum())) a = file.getAddresses();
            	}
            	StringBuilder responses = new StringBuilder("Lista adresów i portów klientów, którzy posiadaja ten plik:\n");
            	for (String s : a) {
					responses.append(s).append("\n");
            	}
            	if (a.isEmpty()) responses = new StringBuilder("Nie ma takiego pliku");
            	byteResponse = responses.toString().getBytes(StandardCharsets.UTF_8);
            }

            DatagramPacket response = new DatagramPacket(byteResponse, byteResponse.length, address, port);
            datagramSocket.send(response);
        }
    }
}