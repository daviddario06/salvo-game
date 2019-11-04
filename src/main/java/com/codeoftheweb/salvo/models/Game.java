package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores;


    public Set<Score> getScores() {
        return scores;
    }



    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }



    public Game() {
    }

    public Game(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }


    public long getId() {
        return id;
    }

    public Map<String, Object> makeGameDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", this.getId());
        dto.put("created",this.getCreationDate());
        dto.put("gamePlayers",this.getGamePlayers().stream().map(gamePlayer -> gamePlayer.makeGamePlayerDTO()).collect(Collectors.toList()));
        dto.put("scores", this.getScores().stream().map(score -> score.makeScoreDTO()).collect(Collectors.toList()));

        return dto;
    }

}
