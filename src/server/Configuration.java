import java.util.HashMap;

public class Configuration {
        // Singleton instance for the Configuration class.
        private static Configuration instance = null;
        
        // Dictionary that we use to store settings.
        private HashMap<String, String> kv;
        
        private Configuration() {
                this.kv = new HashMap<String,String>();
        }
        
        public Configuration instance() {
                if(instance == null)
                        instance = new Configuration();
                return instance;
        }
        
        public String get(String key) {
                return this.kv.get(key);
        } 
        
        public void set(String key, String value) {
                this.kv.put(key, key);
        }
        
}
