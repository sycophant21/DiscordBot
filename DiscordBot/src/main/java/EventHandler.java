import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.LinkedBlockingQueue;

public class EventHandler extends ListenerAdapter {

    private TextChannel generalTextChannel;
    private TextChannel musicTextChannel;
    private final GeneralChannelHandler generalChannelHandler;
    private final ConfessionChannelHandler confessionChannelHandler;
    private MusicChannelHandler musicChannelHandler;
    private final PrivateMessageHandler privateMessageHandler;
    private TextChannel currentChannel;

    public EventHandler(GeneralChannelHandler generalChannelHandler, ConfessionChannelHandler confessionChannelHandler, PrivateMessageHandler privateMessageHandler) {
        this.generalChannelHandler = generalChannelHandler;
        this.confessionChannelHandler = confessionChannelHandler;
        this.privateMessageHandler = privateMessageHandler;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!shutdown")) {
            try {
                currentChannel = null;
                event.getJDA().shutdown();
            }
            catch (Error e) {
                System.out.println(e.getMessage().length());
            }
        }
        if (!event.getAuthor().isBot()) {
            String message = event.getMessage().getContentRaw();
            if (textChannel == generalTextChannel) {
                currentChannel = generalTextChannel;
                generalChannelHandler.handleTextCommands(event,message);
            }
            else if (textChannel == musicTextChannel) {
                currentChannel = musicTextChannel;
                musicChannelHandler.handleMusicEvents(event, message);
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.equalsIgnoreCase("!help")) {
            privateMessageHandler.handlePrivateMessages(event);
        }
        else {
            confessionChannelHandler.handleConfessionCommands(event, message);
        }
    }
    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        Guild guild = event.getGuild();
        String textChannelId = "";
        String confessionChannelId = "";
        String musicTextChannelId = "";
        String musicVoiceChannelId = "";
        generalTextChannel = guild.getTextChannelById(Long.parseLong(textChannelId));
        confessionChannelHandler.setConfessionTextChannel(guild.getTextChannelById(Long.parseLong(confessionChannelId)));
        currentChannel = generalTextChannel;

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler(player, new LinkedBlockingQueue<>());
        player.addListener(trackScheduler);
        musicChannelHandler = new MusicChannelHandler(new AudioPlayerSendHandler(player), playerManager, new AudioLoadResultManager(trackScheduler, new LinkedBlockingQueue<>()), player, trackScheduler);
        musicChannelHandler.setMusicChannel(guild.getVoiceChannelById(Long.parseLong(musicVoiceChannelId)));
        musicChannelHandler.setGuild(event.getJDA().getGuilds().get(0));
        musicTextChannel = guild.getTextChannelById(Long.parseLong(musicTextChannelId));


    }

    public TextChannel getCurrentChannel() {
        //Return the current active channel.
        return currentChannel;
    }

}
