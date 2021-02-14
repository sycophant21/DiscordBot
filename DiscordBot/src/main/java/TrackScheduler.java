import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer audioPlayer;
    private final Queue<AudioTrack> queue;
    private boolean pause;

    public TrackScheduler(AudioPlayer audioPlayer, Queue<AudioTrack> queue) {
        this.audioPlayer = audioPlayer;
        this.queue = queue;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        pause = player.isPaused();
        player.setPaused(!pause);
        pause = !pause;
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        player.setPaused(false);
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        player.startTrack(track, true);
        // A track started playing
    }

    public void playNextTrack() {
        audioPlayer.stopTrack();
        if (!queue.isEmpty()) {
            audioPlayer.playTrack(queue.poll());
        }
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (!queue.isEmpty()) {
                player.playTrack(queue.poll());
            }
            else {
                track.setPosition(0);
                player.playTrack(track);
            }
            // Start next track
        }
        else {
            player.stopTrack();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        player.playTrack(track);
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }

    public void queue(AudioTrack audioTrack) {
        if (!audioPlayer.startTrack(audioTrack,true)) {
            queue.add(audioTrack);
        }
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }
}