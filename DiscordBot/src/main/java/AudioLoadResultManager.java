import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AudioLoadResultManager implements AudioLoadResultHandler {
    private final TrackScheduler trackScheduler;
    private boolean oneOrMore = false;
    private final Queue<Integer> playlistSizeQueue;

    public AudioLoadResultManager(TrackScheduler trackScheduler, Queue<Integer> playlistSizeQueue) {
        this.trackScheduler = trackScheduler;
        this.playlistSizeQueue = playlistSizeQueue;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        trackScheduler.queue(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        if (oneOrMore) {
            playlistSizeQueue.add(playlist.getTracks().size());
            for (AudioTrack track : playlist.getTracks()) {
                trackScheduler.queue(track);
            }
        } else {
            playlistSizeQueue.add(1);
            trackScheduler.queue(playlist.getTracks().get(0));
        }
    }

    public void skipPlaylist() {
        if (!playlistSizeQueue.isEmpty()) {
            int size = playlistSizeQueue.poll();
            for (int i = 0 ; i < size ; i++) {
                trackScheduler.playNextTrack();
            }
        }
        else {
            trackScheduler.playNextTrack();
        }
    }
    public void playNext() {
        if (!playlistSizeQueue.isEmpty()) {
            int size = playlistSizeQueue.poll();
            if (size > 1) {
                size -= 1;
                abc(size);
            }
        }
        trackScheduler.playNextTrack();
    }

    @Override
    public void noMatches() {
        // Notify the user that we've got nothing
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
        // Notify the user that everything exploded
    }

    public void more() {
        oneOrMore = true;
    }

    public void one() {
        oneOrMore = false;
    }

    private void abc(Integer integer) {
        Queue<Integer> integerQueue = new LinkedBlockingQueue<>();
        while (!playlistSizeQueue.isEmpty()) {
            integerQueue.offer(playlistSizeQueue.poll());
        }
        playlistSizeQueue.offer(integer);
        while (!integerQueue.isEmpty()) {
            playlistSizeQueue.offer(integerQueue.poll());
        }
    }
}
