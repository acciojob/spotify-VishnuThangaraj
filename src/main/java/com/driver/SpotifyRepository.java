package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User newUser = new User(name,mobile);
        users.add(newUser);
        //hash
        userPlaylistMap.put(newUser, new ArrayList<>());
        return newUser;
    }

    public Artist createArtist(String name) {
        Artist newArtist = new Artist(name);
        artists.add(newArtist);
        //hash
        artistAlbumMap.put(newArtist, new ArrayList<>());
        return newArtist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist currentArtist = null;
        //get artist
        for(Artist person : artists){
            if(person.getName().equals(artistName)){
                currentArtist = person;
                break;
            }
        }

        if(currentArtist == null){
            currentArtist = createArtist(artistName);
        }

        // create album
        Album newAlbum = new Album(title);
        albums.add(newAlbum);
        //hash
        artistAlbumMap.get(currentArtist).add(newAlbum);
        albumSongMap.put(newAlbum, new ArrayList<>());

        return newAlbum;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Album oldAlbum = null;
        for(Album albumsz : albums){
            if(albumsz.getTitle().equals(albumName)){
                oldAlbum = albumsz;
                break;
            }
        }

        if(oldAlbum == null){
            throw  new Exception("Album does not exist");
        }

        Song newSong = new Song(title, length);
        newSong.setLikes(0);
        songs.add(newSong);

        // Hash
        albumSongMap.get(albumName).add(newSong);
        songLikeMap.put(newSong, new ArrayList<>());

        return newSong;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User creator = null;
        for(User person : users){
            if(person.getMobile().equals(mobile)){
                creator = person;
                break;
            }
        }

        if(creator == null){
            throw  new Exception("User does not exist");
        }

        Playlist newPlaylist = new Playlist(title);
        playlists.add(newPlaylist);

        //hash
        playlistListenerMap.put(newPlaylist, new ArrayList<>());
        playlistSongMap.put(newPlaylist, new ArrayList<>());

        //add song
        for(Song currentSong : songs){
            if(currentSong.getLength() == length){
                playlistSongMap.get(newPlaylist).add(currentSong);
            }
        }

        playlistListenerMap.get(newPlaylist).add(creator);
        creatorPlaylistMap.put(creator, newPlaylist);
        userPlaylistMap.get(creator).add(newPlaylist);

        return newPlaylist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User creator = null;
        for(User person : users){
            if(person.getMobile().equals(mobile)){
                creator = person;
                break;
            }
        }

        if(creator == null){
            throw  new Exception("User does not exist");
        }

        Playlist newPlaylist = new Playlist(title);
        playlists.add(newPlaylist);

        //hash
        playlistListenerMap.put(newPlaylist, new ArrayList<>());
        playlistSongMap.put(newPlaylist, new ArrayList<>());

        //add song
        for(Song currentSong : songs){
            if(currentSong.getTitle().equals(songTitles)){
                playlistSongMap.get(newPlaylist).add(currentSong);
            }
        }

        playlistListenerMap.get(newPlaylist).add(creator);
        creatorPlaylistMap.put(creator,newPlaylist);
        userPlaylistMap.get(creator).add(newPlaylist);

        return newPlaylist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User creator = null;
        for(User person : users){
            if(person.getMobile().equals(mobile)){
                creator = person;
                break;
            }
        }

        if(creator == null){
            throw  new Exception("User does not exist");
        }

        // find playlist
        Playlist oldPlaylist = null;
        for(Playlist playL : playlists){
            if(playL.getTitle().equals(playlistTitle)){
                oldPlaylist = playL;
                break;
            }
        }

        if(oldPlaylist == null){
            throw new Exception("Playlist does not exist");
        }

        // check if creator
        if(creatorPlaylistMap.containsKey(creator) && creatorPlaylistMap.get(creator) == oldPlaylist  || playlistListenerMap.get(oldPlaylist).contains(creator)){
            return oldPlaylist;
        }
        playlistListenerMap.get(oldPlaylist).add(creator);

        if(!userPlaylistMap.get(creator).contains(oldPlaylist)){
            userPlaylistMap.get(creator).add(oldPlaylist);
        }

        return oldPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user == null)
            throw new Exception("User does not exist");

        Song song=null;
        for(Song song1:songs){
            if(song1.getTitle().equals(songTitle)){
                song=song1;
                break;
            }
        }
        if(song == null)
            throw new Exception("Song does not exist");

        if(songLikeMap.get(song).contains(user)){
            return song;
        }
        song.setLikes(song.getLikes()+1);
        songLikeMap.get(song).add(user);

        for(Album album:albumSongMap.keySet()){
            if(albumSongMap.get(album).contains(song)){
                for(Artist artist:artistAlbumMap.keySet()){
                    if(artistAlbumMap.get(artist).contains(album)){
                        artist.setLikes(artist.getLikes()+1);
                        break;
                    }
                }
                break;
            }
        }
        return song;
    }

    public String mostPopularArtist() {
        int countLikes=Integer.MIN_VALUE;
        String popularArtist="";
        for(Artist artist:artists){
            if(artist.getLikes() > countLikes){
                popularArtist=artist.getName();
                countLikes=artist.getLikes();
            }
        }
        return popularArtist;
    }

    public String mostPopularSong() {
        int countLikes=Integer.MIN_VALUE;
        String popularSong="";
        for(Song song:songs){
            if(song.getLikes() > countLikes){
                popularSong=song.getTitle();
                countLikes=song.getLikes();
            }
        }
        return popularSong;
    }
}
