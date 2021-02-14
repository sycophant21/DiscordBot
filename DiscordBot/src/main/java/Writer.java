import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Queue;

public class Writer implements Runnable{
    private final EventHandler eventHandler;
    private TextChannel currentChannel;
    private final Queue<String> messagesString;

    public Writer(EventHandler eventHandler, Queue<String> messagesString) {
        this.eventHandler = eventHandler;
        this.messagesString = messagesString;
    }

    @Override
    public void run() {
        //Helps owner reply from the console itself.
        //Replies as the bot and not tbe owner.
        //Writes the message in the discord channel.
        while (true) {
            if (currentChannel != null && !messagesString.isEmpty()) {
                    currentChannel.sendMessage(messagesString.poll()).queue();
            }
            currentChannel = eventHandler.getCurrentChannel();
        }
    }
}
