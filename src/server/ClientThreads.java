import java.util.LinkedList;
import java.util.List;

public class ClientThreads {
        
        private static ClientThreads instance;
        
        List<ClientThread> clientThreads;
        
        private ClientThreads(){
                this.clientThreads = new LinkedList<ClientThread>();
        }
        
        public static ClientThreads instance() {
                if(instance == null)
                        instance = new ClientThreads();
                return instance;
        }
        
        
        public List<String> getListOfConnectedPlayers() {
                List<String> names = new LinkedList<String>();
                
                for(ClientThread c : this.clientThreads)
                        names.add(c.getNick());
                
                return names;        
        }
        
        
        public void add(ClientThread t) {        
                // Notify other clients that we have a new player in the game.
                String message = String.format("join;%s", t.getNick());
                
                for(ClientThread c : this.clientThreads) {
                        c.sendMessage(message);
                }
                
                this.clientThreads.add(t);
        }
        
        
        public void broadcast(ClientThread from, String message) {
                for(ClientThread c : this.clientThreads){
                        if(c.getNick().equals(from.getNick()))
                                continue;
                        c.sendMessage(message);
                }
        }

        public void remove(ClientThread clientThread) {
                this.clientThreads.remove(clientThread);
                Logging.Log(String.format("Removing %s from client threads", clientThread.getNick()));
                
                // Notify all connected users that he has left.
                for(ClientThread c : this.clientThreads){
                        if(c.getNick().equals(clientThread.getNick()))
                                continue;
                        c.sendMessage(String.format("exit;%s", clientThread.getNick()));
                }
        }
}
