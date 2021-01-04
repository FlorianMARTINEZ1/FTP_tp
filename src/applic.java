import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

class applic
{
    public static ServerSocket sockserv=null; // ?
    public static DataInputStream in ; // ?
    public static DataOutputStream out; // ?

    public static void main (String args[]) throws Exception
    {
        //byte mess[];
        String message;
        sockserv = new ServerSocket (1234);
        Scanner scanner = new Scanner(System.in);
        boolean continuer = true;
        try {
            while (continuer)
            {
                try
                {
                    System.out.println("Waiting for client . . .");
                    Socket sockcli = sockserv.accept(); // ?
                    System.out.println("Client connected");

                    in = new DataInputStream (sockcli.getInputStream()); // ?
                    out = new DataOutputStream (sockcli.getOutputStream()); // ?
                    message = in.readUTF(); // ?
                    String[] messageSplit = message.split(" ");
                    if (messageSplit[0].equals("QUIT")) {
                        continuer = false;
                        System.out.println("Arrêt du serveur . . .");
                    } else if (messageSplit[0].equals("GET")) {
                        String Filecontent = readFile(messageSplit[1]);
                        out.writeUTF(Filecontent);
                    } else if (messageSplit[0].equals("PUT")) {
                        System.out.println("Transfert du fichier . . .");
                        String file = messageSplit[2];
                        receiveFile(file);
                        System.out.println("Transfert terminé !");
                    } else {
                        System.out.print("Client : ");
                        System.out.println(message);
                        System.out.println("Entrez votre message : ");
                        message = scanner.nextLine();
                        out.writeUTF(message);
                    }
                    sockcli.close(); // ?
                } catch (IOException ex) { }
            }
        } finally {
            try {
                sockserv.close(); // ?
            } catch (IOException ex) { }
        }

    }
    
    private static void receiveFile(String fileName) throws Exception{
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        long size = in.readLong();     // read file size
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;      // read upto file size
        }
        fileOutputStream.close();
    }

    private static String readFile(String path) throws Exception {
        String content = readFile(path, StandardCharsets.US_ASCII);
        return content;
    }

    private static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}