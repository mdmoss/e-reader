
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StdinListener implements Runnable {

  Reader r;

  public StdinListener(Reader r) {
    this.r = r;
  }
  
  
  BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  
  @Override
  public void run() {
    while (true) {
      try {
        String in = reader.readLine();
        if (in == null) System.exit(0);
        else if (in.equals("y") || in.equals("n")) {
          this.r.confirmations.put(in);
        } else {
          this.r.input.put(in);  
        }
        
      } catch (IOException ex) {
        Logger.getLogger(StdinListener.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InterruptedException ex) {
        Logger.getLogger(StdinListener.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  
}
