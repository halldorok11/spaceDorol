import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class NetworkThread extends Thread {

    private Socket socket = null;

    PrintWriter out = null;
    BufferedReader in = null;
    private  boolean alive;


    public NetworkThread() {
        try {
            this.socket = new Socket("localhost", 7575);
            this.out = new PrintWriter(this.socket.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));


        }
        catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        //System.out.println("Sending message: " + message);
        this.out.write(message+"\n");
        this.out.flush();
    }


    public void run() {

        // Read my nickname


        while(true) {
            System.out.println("waiting for server message!");
            try {
                String message = this.in.readLine();
                System.out.println("Received message from server: " + message);

                // We assume that the network messages are on csv format.
                String[] tokens = message.split(";");
                String name = tokens[1];

                if(tokens[0].equals("online")) {
                    int team = Integer.parseInt(tokens[2]);
                    NetworkGameState.instance().addPlayer(name);
                }

                if(tokens[0].equals("join")) {
                    int team = Integer.parseInt(tokens[2]);
                    NetworkGameState.instance().addPlayer(name);
                }

                if(tokens[0].equals("exit")) {
                    NetworkGameState.instance().removePlayer(name);
                }

                if(tokens[0].equals("move")) {
                    float x = Float.parseFloat(tokens[2]);
                    float y = Float.parseFloat(tokens[3]);
                    float z = Float.parseFloat(tokens[4]);
                    NetworkGameState.instance().updatePlayer(name, x, y, z);
                }

                if(tokens[0].equals("shot")){
                    int team = Integer.parseInt(tokens[2]);
                    Point3D p = new Point3D(Float.parseFloat(tokens[3]), Float.parseFloat(tokens[4]), Float.parseFloat(tokens[5]));
                    Vector3D speed = new Vector3D(Float.parseFloat(tokens[6]), Float.parseFloat(tokens[7]), Float.parseFloat(tokens[8]));
                    NetworkGameState.instance().addProjectile(p,speed,team);
                }

                if (tokens[0].equals("team")){
                    int team = Integer.parseInt(tokens[2]);
                    NetworkGameState.instance().setTeam(name,team);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
