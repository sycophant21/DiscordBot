import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class MusicChannelHandler {
    private final AudioPlayerSendHandler audioPlayerSendHandler;
    private final AudioPlayerManager playerManager;
    private final AudioLoadResultManager audioLoadResultManager;
    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;
    private AudioTrack audioTrack;
    private long position;
    private VoiceChannel musicChannel;
    private Guild guild;


    public MusicChannelHandler(AudioPlayerSendHandler audioPlayerSendHandler, AudioPlayerManager playerManager, AudioLoadResultManager audioLoadResultManager, AudioPlayer audioPlayer, TrackScheduler trackScheduler) {
        this.audioPlayerSendHandler = audioPlayerSendHandler;
        this.playerManager = playerManager;
        this.audioLoadResultManager = audioLoadResultManager;
        this.audioPlayer = audioPlayer;
        this.trackScheduler = trackScheduler;
    }

    public void handleMusicEvents(GuildMessageReceivedEvent event, String message) {
        if (message.equalsIgnoreCase("!join")) {
            Member member = event.getMember();
            if (member != null) {
                GuildVoiceState guildVoiceState = member.getVoiceState();
                if (guildVoiceState != null) {
                    VoiceChannel voiceChannel = guildVoiceState.getChannel();
                    if (voiceChannel != null) {
                        event.getGuild().getAudioManager().openAudioConnection(guildVoiceState.getChannel());
                    }
                    else {
                        Objects.requireNonNull(guild).getAudioManager().openAudioConnection(musicChannel);
                    }
                }
            }
        } else if (message.equalsIgnoreCase("!leave")) {
            event.getGuild().getAudioManager().closeAudioConnection();
        } else if (message.equalsIgnoreCase("!playlist")) {
            List<AudioTrack> audioTrackList = fillQueue(trackScheduler.getQueue());
            String result = "";
            for (int i = 0; i < audioTrackList.size(); i++) {
                result = result.concat((i + 1) + ") " + audioTrackList.get(i).getInfo().title + "\n");
            }
            event.getChannel().sendMessage(result).queue();
        } else if (message.contains("!play")) {
            event.getGuild().getAudioManager().setSendingHandler(audioPlayerSendHandler);
            if (message.contains("https:")) {
                playerManager.loadItem(message.substring(("!play ").length()), audioLoadResultManager);
            } else {
                String search = "";
                if (message.contains("playlist")) {
                    audioLoadResultManager.more();
                    search = search.concat(message.substring("!play ".length(), message.lastIndexOf(" playlist")));
                } else {
                    audioLoadResultManager.one();
                    search = search.concat(message.substring("!play ".length()));
                    //Sound Cloud Search: scsearch:
                }
                playerManager.loadItem("ytsearch:" + search, audioLoadResultManager);
            }
        } else if (message.equalsIgnoreCase("!stop")) {
            audioTrack = audioPlayer.getPlayingTrack();
            position = audioTrack.getPosition();
            audioPlayer.stopTrack();
        } else if (message.equalsIgnoreCase("!resume")) {
            AudioTrack track = audioTrack.makeClone();
            track.setPosition(position);
            audioPlayer.startTrack(track, true);
        } else if (message.equalsIgnoreCase("!skip playlist")) {
            audioLoadResultManager.skipPlaylist();
        } else if (message.equalsIgnoreCase("!skip song")) {
            audioLoadResultManager.playNext();
        } else if (message.equalsIgnoreCase("!Decrease volume")) {
            audioPlayer.setVolume(audioPlayer.getVolume() - 10);
        } else if (message.equalsIgnoreCase("!Increase volume")) {
            audioPlayer.setVolume(audioPlayer.getVolume() + 10);
        } else if (message.contains("!forward") && message.length() >= "!forward 0".length()) {
            long seconds = Long.parseLong(message.substring("!forward ".length()));
            AudioTrack currentTrack = audioPlayer.getPlayingTrack();
            currentTrack.setPosition(currentTrack.getPosition() + (seconds * 1000));
        } else if (message.contains("!backward") && message.length() >= "!backward 0".length()) {
            long seconds = Long.parseLong(message.substring("!backward ".length()));
            AudioTrack currentTrack = audioPlayer.getPlayingTrack();
            currentTrack.setPosition(currentTrack.getPosition() - (seconds * 1000));
        }
    }

    private List<AudioTrack> fillQueue(Queue<AudioTrack> queue) {
        List<AudioTrack> audioTrackList = new ArrayList<>();
        if (queue.size() > 0) {
            audioTrackList.add(audioPlayer.getPlayingTrack());
            for (int i = 0; i < queue.size(); i++) {
                AudioTrack audioTrack = queue.poll();
                audioTrackList.add(audioTrack);
                queue.offer(audioTrack);
            }
        } else {
            audioTrackList.add(audioPlayer.getPlayingTrack());
        }
        return audioTrackList;
    }

    public void setMusicChannel(VoiceChannel musicChannel) {
        this.musicChannel = musicChannel;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
