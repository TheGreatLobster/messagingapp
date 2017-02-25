package srcfiles;


import java.io.BufferedReader;
import java.io.PrintWriter;

public class Client {

    private String userName;
    private String password;
    private static String clientAddress;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String userName) {
        this.userName = userName;
    }


    public Client(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public static void setClientAddress(String clientAddress) {
        Client.clientAddress = clientAddress;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }
}
