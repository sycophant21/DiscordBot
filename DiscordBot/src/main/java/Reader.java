import java.util.Queue;
import java.util.Scanner;

public class Reader implements Runnable{
    private final Queue<String> messages;

    public Reader(Queue<String> messages) {
        this.messages = messages;
    }

    @Override
    public void run() {
        //Helps the owner reply from the console itself.
        //Reads the messages to be sent.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            messages.offer(scanner.nextLine());
        }
    }
}
