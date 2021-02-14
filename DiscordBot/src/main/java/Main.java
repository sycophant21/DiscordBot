import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(args[0]).build();

        ConfessionChannelHandler confessionChannelHandler = new ConfessionChannelHandler(new LinkedBlockingQueue<>());
        String message = fillMessage();
        PrivateMessageHandler privateMessageHandler = new PrivateMessageHandler(message);
        EventHandler eventHandler = new EventHandler(new GeneralChannelHandler(), confessionChannelHandler, privateMessageHandler);
        jda.addEventListener(eventHandler);
        Queue<String> messagesQueue = new LinkedBlockingQueue<>();
        new Thread(new Reader(messagesQueue)).start();
        new Thread(new Writer(eventHandler, messagesQueue)).start();


    }

    private static String fillMessage() {
        //Return the user manual message for the bot here
        return "";
    }


}