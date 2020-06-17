# E-Goat
*Stanisław Małkowski, Franciszek Mirecki*

Projekt powinien realizować progamy z polecenia E-Goat, to znaczy program serwera i klienta umożliwiające komunikację oraz przesyłanie plików bezpośrednio miedzy klientami (peer-to-peer).

Całość została napisana w javie i komunikuje się przy pomocy protokołu `UDP`.\
Uruchamiamy `UDPServer` oraz `UDPClient`.\
Postępujemy zgodnie z instrukcjami w programie `UDPClient`, to znaczy:
1. Podajemy ścieżkę do folderu którego zawartośc mamy zamiar udostępnić, powinien on zawierać same pliki.
```java
Scanner scan = new Scanner(System.in);
System.out.println("Podaj ściezke folderu, który chcesz udostępnić:");
String path = scan.nextLine();
System.out.println("Czekaj...");
File dir = new File(path);
String[][] sums = loadNamesAndChecksums(dir);
```

2. Program wczytuje nazwy plików i oblicza ich sumy kontrolne, a następnie wysyła te wartości do serwera, który zapisuje je w liście obiektów stworzonej klasy `File` (plik `File` z deklaracją klasy, jej metodami i konstruktorami).
```java
private static String [][] loadNamesAndChecksums(File dir) throws NoSuchAlgorithmException, IOException {
    String[][] sums = new String[Objects.requireNonNull(dir.listFiles()).length][2];
    int i = 0;
    //Wczytywanie nazw plików i sum kontrolnych do tablicy
    for (File file : Objects.requireNonNull(dir.listFiles())) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        sums[i][1] = checksum(file.getPath(), md);
        sums[i][0] = file.getName();
        i++;
    }
    return sums;
}
```

3. Następnie program `UDPClient` rząda od użytkownika sumy kontronej pliku, który chcielibyśmy pobrać, a po wczytaniu wysyła ją do serwera.
```java
System.out.println("Podaj sume kontrolna pliku, który chcialbys pobrac:");
String sum = scan.nextLine();
stringContents = sum.getBytes(StandardCharsets.UTF_8);
sentPacket = new DatagramPacket(stringContents, stringContents.length);
sentPacket.setAddress(serverAddress);
sentPacket.setPort(Config.PORT);
socket.send(sentPacket);
```

4. `UPDServer` przeszukuje listę plików i gdy znajdzie taki o podanej sumie kontrolnej, to wysyła listę adresów wraz z portami klientów, którzy zadeklarowali, że posiadają taki plik.
```java
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
```

5. `UDPClient` wyświetla daną listę użytkownikowi i rząda przekopiowania lub przepisania adresu wraz z portem wybranych z podanej listy.
```java
System.out.println(message + "Wybierz jeden adres i przekopiuj go wraz z portem.");
```

6. Program wczytyje wybrany przez klienta adres z portem.
```java 
String clientAdress = scan.nextLine();
```

Na tym kończy się działanie programów.\
Brakuje komunikacji między klientami w celu przesłania pliku, przesłania go oraz sprawdzenia sumy kontrolnej.\
Należaloby również dodać kontrolę błędów w niektórych miejscach gdzie jej brakuje, a potencjalnie takowe mogą wystąpić.