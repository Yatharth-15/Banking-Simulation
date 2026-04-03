package logging;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class TransactionLogger {

    public void log(int accNo, String message) {

        saveToFile(accNo + " | " + message + " | [" + LocalDateTime.now() + "]");
    }

    
    public void log(String message) {
        saveToFile("[SYSTEM] [" + LocalDateTime.now() + "] MESSAGE: " + message);
    }

    public void log(model.Transaction t) {
        saveToFile("[" + LocalDateTime.now() + "] TRANSACTION: " + t.toString());
    }

    private void saveToFile(String fullLine) {
        try (PrintWriter out = new PrintWriter(new FileWriter("transactions.log", true))) {
            out.println(fullLine);
            System.out.println(fullLine); 
        } catch (Exception e) {
            System.err.println("Logging failed: " + e.getMessage());
        }
    }
}