package srcfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static int portNumber = 5555;
    public static ArrayList<Client> clients;
    public BufferedReader socketIn;
    public PrintWriter socketOut;


    public static void main(String[] args) throws IOException
    {

        clients = new ArrayList<>();
        //adminUser for later implementation if wanted/necessary
        Client admin = new Client("admin", "admin");
        addClientToList(admin);

        //for testing: later clients would register/login in the application and added to Arraylist "clients"
        //This way we can connect existing users to a chat room.
        Client client1 = new Client("Per", "Perpassord");
        Client client2 = new Client("Kari", "Karipassord");
        addClientToList(client1);
        addClientToList(client2);



        if (args.length > 0)
        {
            if (args.length == 1)
                portNumber = Integer.parseInt(args[0]);
            else
            {
                System.err.println("Usage: java TCP-server [<port number>]");
                System.exit(1);
            }
        }


        try (
                // Create server socket with the given port number
                ServerSocket serverSocket =
                        new ServerSocket(portNumber)

        )
        {

            System.out.println("TCP server is up and running with port: " + portNumber);
            // continuously listening for clients
            while (true)
            {
                // create and start a new ClientServer thread for each connected client
                ClientServer clientserver = new Server.ClientServer(serverSocket.accept());
                clientserver.start();

            }
        } catch (IOException e)
        {

            System.out.println("Exception occurred when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }

    public static void addClientToList(Client client) {
        if(findClientInList(client.getUserName()) == null) {
            clients.add(client);
        }
    }

    public static Client findClientInList(String userName) {
        for (Client c : clients) {
            if(c.getUserName().equalsIgnoreCase(userName)) {
                return c;
            }
        }
        return null;
    }

    public static void removeClientFromList(Client client) {
        if(findClientInList(client.getUserName()) != null) {
            clients.remove(client);
        }
    }


    /***
     * This class serves a client in a separate thread
     */
    static class ClientServer extends Thread
    {
        Socket connectSocket;
        InetAddress clientAddr;
        int serverPort, clientPort;
        BufferedReader socketIn;
        PrintWriter socketOut;

        public ClientServer(Socket connectSocket)
        {
            this.connectSocket = connectSocket;
            clientAddr = connectSocket.getInetAddress();
            clientPort = connectSocket.getPort();
            serverPort = connectSocket.getLocalPort();

        }

        public void run()
        {
            //Here we must implement a check to see if the client is already connected! We can do this since we never close the socket
            //unless user types "EXIT"


            System.out.println("Ny tråd for client [" + clientAddr.getHostAddress() +  ":" + clientPort +"] ");
            try (
                    // Create server socket with the given port number
                    PrintWriter out =
                            new PrintWriter(connectSocket.getOutputStream(), true);
                    // Stream reader from the connection socket
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connectSocket.getInputStream()))
            )
            {
                //These will be passed on to the correct client object, parts[0} (the splitted receivedText)
                socketIn = in;
                socketOut = out;


                String receivedText; //= in.readLine();


                // read from the connection socket
                while ((receivedText = in.readLine()) != null)
                {
                    System.out.println("Client [" + clientAddr.getHostAddress() +  ":" + clientPort +"] > " + receivedText);

                    // Splits the receivedText from socket into 3 parts, max. This way we can see who is the sender, and who is the receiver.
                    String[] parts = receivedText.split(":", 3);


                    Client sender = findClientInList(parts[0]);
                    Client  receiver = findClientInList(parts[1]);


                    if(sender != null && receiver  != null) {
                        System.out.println("Found both clients, sender and receiver!");
                        //As of now this sends text back to the sender, this WILL be changed to the receiving end ie. parts[1]
                        out.println(parts[2]);

                    }



                    if(parts[2].equals("EXIT")) {
                        System.out.println("Ferdig med å lese fra textfeltet");
                        out.println("Closing session with " + parts[1]);
                        // close the connection socket
                        connectSocket.close();
                        break;
                    }
                }



            } catch (IOException e)
            {
                System.out.println("Exception occurred when trying to communicate with the client " + clientAddr.getHostAddress());
                System.out.println(e.getMessage());
            }
        }
    }
}
