import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{

    public static Socket sockcli = null; /** socket pour communiquer avec un client */
    public static DataInputStream in; /** réception des informations en provenance du serveur */
    public static DataOutputStream out; /** envoi d'informations vers le serveur */

    public static void main (String args[]) throws Exception
    {
        String message;
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;
        while (continuer) {
            try {
                sockcli = new Socket ("127.0.0.1", 1234); //connexion au serveur
                in = new DataInputStream (sockcli.getInputStream()); // flux entrée
                out = new DataOutputStream (sockcli.getOutputStream()); // sortie
                System.out.println("Connected");
                System.out.println("Entrez votre message : ");
                message = scanner.nextLine();
                String[] messageSplit = message.split(" ");
                if (messageSplit[0].equals("QUIT")) {
                    out.writeUTF(message); // ecriture socket
                    sockcli.close();
                    System.out.println("Disconnected");
                    continuer = false;
                    break;
                } if (messageSplit[0].equals("PUT")) {
                    out.writeUTF(message);
                    sendFile(messageSplit[1]);
                } else if (messageSplit[0].equals("GET")) {
                    out.writeUTF(message);
                    message = in.readUTF();
                    System.out.println(message);
                } else {
                    out.writeUTF(message); // ecriture socket
                    message = in.readUTF(); // lecture socket
                    System.out.println(message);
                }
                sockcli.close();
            }catch (IOException ex){
                System.out.println(ex);
            }
        }

    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);

        // send file size
        out.writeLong(file.length());
        // break file into chunks
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            out.write(buffer,0,bytes);
            out.flush();
        }
        fileInputStream.close();
    }
}
