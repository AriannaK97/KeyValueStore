package Server;

import java.net.*;

public class Server {

    private static int Port;
    private static InetAddress ip;
    private static final Trie trie = new Trie();
    private static final Command commander = new Command(trie);

    public static void main(String[] args)throws Exception{

        if(args.length != 4){
            System.err.println("Wrong Arguments");
            throw new Exception();
        }

        ip = InetAddress.getByName(args[1]);
        Port = Integer.parseInt(args[3]);

        ServerSocket ss = new ServerSocket(Port, 0, ip);
        Socket s = ss.accept();

        commander.readFromStream(s);

        s.close();
        ss.close();
    }
}
