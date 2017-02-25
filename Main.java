package srcfiles;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main extends Application {

    private final String hostName = "127.0.0.1";
    private final int portNumber = 5555;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));


        //Create clients and connecting them to the server (server must be running)
        Client client1 = new Client("Sigrid", "Sigridpassord");
        Client client2 = new Client("Test", "Testpassord");


        createMessageWindow(primaryStage, client1);





    }
    private void createMessageWindow(Stage primaryStage, Client client) {

        GridPane root = new GridPane();
        Scene scene = new Scene(root,400,400);


        primaryStage.setScene(scene);
        primaryStage.show();

        TextArea textAreaMessages = new TextArea();
        textAreaMessages.setPrefSize(scene.getWidth(), (scene.getWidth() - 50));
        textAreaMessages.setEditable(false);

        TextField textFieldMessage = new TextField();
        Button btnSend = new Button("Send message");

        root.add(textAreaMessages, 0, 0, 2, 1);
        root.add(textFieldMessage, 0, 1, 2, 1);
        root.add(btnSend, 3, 1, 1, 1);

        btnSend.setText("Send message");
        btnSend.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                /*
                String test = textFieldMessage.getText();
                System.out.println(test);
                textAreaMessages.appendText(test + "\n");
                textFieldMessage.setText("");
                */

                try
                        (
                                // create TCP socket for the given hostName, remote port PortNumber
                                Socket clientSocket = new Socket(hostName, portNumber);

                                // Stream writer to the socket
                                PrintWriter out =
                                        new PrintWriter(clientSocket.getOutputStream(), true);
                                // Stream reader from the socket
                                BufferedReader in =
                                        new BufferedReader(
                                                new InputStreamReader(clientSocket.getInputStream()))


                                //THIS NEEDS TO BE CHANGED TO JAVAFX CONTROLS (not System.in)
                                // Keyboard input reader
                                /*BufferedReader stdIn =
                                        new BufferedReader(
                                                new InputStreamReader(System.in))*/
                        )
                {

                    String userInput;
                    // Loop until null input string
                    System.out.println("I (Client) [" + InetAddress.getLocalHost()  + ":" + clientSocket.getLocalPort() + "] > ");

                    while ((userInput = textFieldMessage.getText()) != null && !userInput.isEmpty()) {

                        System.out.println("From textField: " + userInput);
                        out.println(userInput);

                        String receivedText = in.readLine();

                        textAreaMessages.appendText(receivedText + "\n");

                        textFieldMessage.setText("");
                    }

                } catch (UnknownHostException e)
                {
                    System.err.println("Unknown host " + hostName);

                } catch (IOException e)
                {
                    System.err.println("Couldn't get I/O for the connection to " + hostName);

                }

            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
