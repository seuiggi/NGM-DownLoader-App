package com.acceler8tion.ngmdownloader.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Objects;

public class NGRequest {

    private static final String OLD_URL = "https://www.newgrounds.com/audio/download/";
    private static final String CURRENT_URL = "https://audio-download.ngfiles.com/";
    private static final String INFO_URL = "https://www.newgrounds.com/audio/listen/";
    private final int songId;
    private final String downloadUrl;
    private final String name;

    private NGRequest(int songId, String downloadUrl, String name) {
        this.songId = songId;
        this.downloadUrl = Objects.requireNonNull(downloadUrl, "failed-to-parse-url");
        this.name = name;
    }

    public static NGRequest build(int songId) throws NGMNoSuchMusicException, NGMConnectFailedException {
        String songName;
        String songUrl;
        try {
            //TODO: Throwing an error
            Document con = Jsoup.connect(INFO_URL + songId)
                    .ignoreContentType(true)
                    .maxBodySize(0)
                    .get();
            songName = con.title();
            String title = songName;
            title = title.replace(" ", "-");
            title = title.replace("&", "amp");
            title = title.replace("\"", "quot");
            title = title.replace("<", "lt");
            title = title.replace(">", "gt");
            if (title.length() > 27) {
                title = title.substring(0, 27);
            }
            title = title.replace("[^a-zA-z0-9-]", "");

            if(songId <= 469776) {
                return new NGRequest(songId, OLD_URL + songId, songName);
            } else {
                songUrl = String.format(CURRENT_URL + "%s000/%s_%s.mp3", ("" + songId).substring(0, 3), songId, title);
                return new NGRequest(songId, songUrl, songName);
            }
        } catch (HttpStatusException e) {
            throw new NGMNoSuchMusicException();
        } catch (IOException e) {
            throw new NGMConnectFailedException();
        }
    }

    public static String[] parseInfo(int songId) {
        try {
            Document con = Jsoup.connect(INFO_URL + songId)
                    .ignoreContentType(true)
                    .get();
            String title = con.title();
            final String name = title;
            if(songId <= 469776) return new String[]{"", name};
            title = title.replace(" ", "-");
            title = title.replace("&", "amp");
            title = title.replace("\"", "quot");
            title = title.replace("<", "lt");
            title = title.replace(">", "gt");
            if (title.length() > 27) {
                title = title.substring(0, 27);
            }
            title = title.replace("[^a-zA-z0-9-]", "");
            return new String[]{String.format(CURRENT_URL + "%s000/%s_%s.mp3", ("" + songId).substring(0, 3), songId, title), name};
        } catch (IOException e) {
            return null;
        }
    }

    public int getSongId() {
        return songId;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof NGRequest)){
            return false;
        }
        NGRequest ngr = (NGRequest) obj;
        return ngr.songId == songId && downloadUrl.equalsIgnoreCase(ngr.downloadUrl);
    }

    @NonNull
    @Override
    public String toString() {
        return "NGRequest["+
                "songId="+songId+
                ", downloadUrl="+downloadUrl
                +"]";
    }
}
