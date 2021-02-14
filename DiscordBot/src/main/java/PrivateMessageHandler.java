import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class PrivateMessageHandler {
    private final String message;

    public PrivateMessageHandler(String message) {
        this.message = message;
    }

    public void handlePrivateMessages(PrivateMessageReceivedEvent event) {
        //Handles private messages sent to the bot.
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(this.message).completeAfter(2, TimeUnit.SECONDS);
    }
}
