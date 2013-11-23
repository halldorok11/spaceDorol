import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Generator {
        public static String generate(String s){
                try{
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.reset();
                m.update(s.getBytes());
                byte[] digest = m.digest();
                BigInteger bigInt = new BigInteger(1,digest);
                String hashtext = bigInt.toString(16);
                while(hashtext.length() < 32 )
                  hashtext = "0"+hashtext;
                return hashtext;
                }
                catch(Exception ex){
                        Logging.Log("Unable to create MD5 sum");
                        return null;
                }
        }
}
