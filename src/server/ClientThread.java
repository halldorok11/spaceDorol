import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClientThread extends Thread {
        
        private Socket socket;
        private boolean alive;
        private String nick;
        
        // Streams to read and write to the socket.
        PrintWriter out = null;
        BufferedReader in = null;
        
        
        
        
        public ClientThread(Socket clientSocket) {
                this.socket = clientSocket;
                
                
                try {
                        this.out = new PrintWriter(this.socket.getOutputStream());
                        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                        
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                
                        
                this.alive = true;
        }
        
        
        public String getNick(){
                return this.nick;
        }
        
        
        public void sendMessage(String message){
        
                try {
                        PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                        out.write(message+"\n");
                        out.flush();
                } 
                catch (IOException e) {
                        // If we are unable to get output stream from the socket we
                        // close the connection to that client!
                        this.alive = false;
                }
        }
        
        
        public void run(){
                
                // The first thing that the server does is to provide a unique id
                // and send it to the client. This will be the "client" nick name.
                // Here we select to give the player an MD5 sum as a nick name
                // from the current time.
                String md5name = MD5Generator.generate(
                                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
                
                Logging.Log(String.format("Creating nick for client: %s", md5name));
                this.nick = md5name;
                
                // We add ourself to the client thread collection.
                ClientThreads.instance().add(this);
                
                // Send the client information for all the players that are currently connected.
                List<String> connectedPlayers = ClientThreads.instance().getListOfConnectedPlayers();
                
                // Remove my name from the list.
                connectedPlayers.remove(this.nick);
                
                for(String p : connectedPlayers) {
                        this.sendMessage(String.format("online;%s", p));
                }
                
                
                while(this.alive) {
                        
                        try {
                                // We expect messages on csv format.
                                String message = this.in.readLine();
                                
                                if(message == null)
                                {
                                        this.alive = false;
                                        break;
                                }
                                
                                Logging.Log(String.format("[%s] %s", this.nick, message));
                                
                                String[] tokens = message.split(";");
                                String action = tokens[0];
                                
                                // What is the format of the move action!
                                if(action.equals("move")) {
                                        String x = tokens[1];
                                        String y = tokens[2];
                                        String z = tokens[3];
                                        
                                        String p = String.format("move;%s;%s;%s;%s", this.nick, x, y, z);
                                        ClientThreads.instance().broadcast(this, p);
                                }

                                if (action.equals("shot")){
                                    String p = String.format("shot;%s;%s;%s;%s;%s;%s;%s", this.nick,tokens[1],tokens[2],tokens[3],tokens[4],tokens[5],tokens[6]);
                                    ClientThreads.instance().broadcast(this, p);
                                }
                        } 
                        catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                this.alive = false;
                                break;
                        }

                        // Notify the client about other players currently playing        
                        // notify everyone that this client has joined the game.
                }
                
                // If this thread dies for some reason we must notify other players that he has been disconnected.
                ClientThreads.instance().remove(this);
                
        }
        
}
