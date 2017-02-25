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


    public static void main(String[] args) throws IOException
    {

        clients = new ArrayList<>();
        Client admin = new Client("admin", "admin");
        addClientToList(admin);

        Client client1 = new Client("Per", "PerPassord");
        addClientToList(admin);

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
        if(findClientInList(client) == null) {
            clients.add(client);
        }
    }

    public static Client findClientInList(Client client) {
        for (Client c : clients) {
            if(c.getUserName().equalsIgnoreCase(client.getUserName())) {
                return client;
            }
        }
        return null;
    }

    public static void removeClientFromList(Client client) {
        if(findClientInList(client) != null) {
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

        public ClientServer(Socket connectSocket)
        {
            this.connectSocket = connectSocket;
            clientAddr = connectSocket.getInetAddress();
            clientPort = connectSocket.getPort();
            serverPort = connectSocket.getLocalPort();
        }

        public void run()
        {
            System.out.println("Ny tråd for client [" + clientAddr.getHostAddress() +  ":" + clientPort +"] ");
            try (
                    // Create server socket with the given port number
                    PrintWriter out =
                            new PrintWriter(connectSocket.getOutputStream(), true);
                    // Stream reader from the connection socket
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connectSocket.getInputStream()));
            )
            {

                String receivedText;



                // read from the connection socket
                while (((receivedText = in.readLine()) != null))
                {
                    System.out.println("Client [" + clientAddr.getHostAddress() +  ":" + clientPort +"] > " + receivedText);

                    // Write the converted uppercase string to the connection socket
                    String outText = receivedText.toUpperCase();

                    out.println(outText);
                    System.out.println("I (Server) [" + connectSocket.getLocalAddress().getHostAddress() + ":" + serverPort +"] > " + outText);
                }

                System.out.println("Ferdig med å lese fra textfeltet");
                // close the connection socket
                //connectSocket.close();

            } catch (IOException e)
            {
                System.out.println("Exception occurred when trying to communicate with the client " + clientAddr.getHostAddress());
                System.out.println(e.getMessage());
            }
        }
    }
}
