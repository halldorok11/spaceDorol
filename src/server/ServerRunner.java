import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRunner {
                
        public static void main(String[] args) {
                // Varible for the socket port.
                int serverPort = 7575;
                boolean alive = true;
                
                try {
                        
                        Logging.Log(String.format("Starting server listening on port %d", serverPort));
                        
                        ServerSocket serverSocket = new ServerSocket(serverPort);
                        
                        while(alive) {
                                Logging.Log("Waiting for connections");
                                
                                Socket s = serverSocket.accept();
                                Logging.Log("Client connected!");
                                
                                ClientThread ct = new ClientThread(s);
                                //ClientThreads.instance().add(ct);
                                ct.start();
                                
                                
                        }
                        
                        serverSocket.close();
                        serverSocket = null;
                        
                } 
                catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
}
