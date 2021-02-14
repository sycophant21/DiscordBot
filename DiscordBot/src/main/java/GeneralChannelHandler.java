import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class GeneralChannelHandler {


    public void handleTextCommands(GuildMessageReceivedEvent event, String message) {
        //* Parse the id of discord channel here.*//
        String id = "";
        //Returns the member object of the user who sent the message.
        Member member = event.getGuild().getMemberById(Long.parseLong(id));
        assert member != null;
        //Says hi to the user.
        if (event.getAuthor() != member.getUser()) {
            if (message.equalsIgnoreCase("!hi")) {
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage("hi " + event.getAuthor().getName()).completeAfter(2, TimeUnit.SECONDS);
            }
            if (message.contains("!say hi to")) {
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage("hi " + message.substring("!say hi to ".length())).completeAfter(2, TimeUnit.SECONDS);
            }
        }
    }
}
