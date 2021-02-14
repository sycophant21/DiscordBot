import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class ConfessionChannelHandler implements Runnable {

    private boolean isConfession = false;
    private boolean start = true;
    private final Queue<String> confessionQueue;
    private TextChannel confessionTextChannel;

    public ConfessionChannelHandler(Queue<String> confessionQueue) {
        this.confessionQueue = confessionQueue;
    }

    public void handleConfessionCommands(PrivateMessageReceivedEvent event, String message) {
        if (message.equalsIgnoreCase("!confess") && !isConfession) {
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage("Go ahead I'm listening").completeAfter(2, TimeUnit.SECONDS);
            isConfession = true;
        } else if (isConfession) {
            if (!message.equalsIgnoreCase("Go ahead I'm listening")) {
                confessionQueue.offer(message);
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage("Gotcha!!").completeAfter(2,TimeUnit.SECONDS);
                System.out.println(event.getAuthor() + " \t :" +message);
                if (start) {
                    new Thread(this).start();
                    start = false;
                }
                isConfession = false;
            }
        }
    }

    public void setConfessionTextChannel(TextChannel confessionTextChannel) {
        this.confessionTextChannel = confessionTextChannel;
    }

    @Override
    public void run() {
        while (true) {
            String confession = confessionQueue.poll();
            if (confession != null && confessionTextChannel != null) {
                System.out.println(confession);
                confessionTextChannel.sendMessage("Somebody just confessed \" " + confession + " \"").queue();
            } else if (confession != null) {
                confessionQueue.offer(confession);
            }
        }
    }
}
