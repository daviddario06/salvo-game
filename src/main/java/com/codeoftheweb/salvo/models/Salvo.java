package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {

//---------ATRIBUTES--------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_ID")
    private GamePlayer gamePlayer;

    private Long turn;

    @ElementCollection
    @JoinColumn(name = "salvoLocation")
    private List<String> salvoLocations;

 //---------CONSTRUCTORS--------------
    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayer, Long turn, List<String> location) {
        this.gamePlayer = gamePlayer;
        this.turn = turn;
        this.salvoLocations = location;
    }
//---------GETTERS AND SETTERS--------------

    public Long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setTurn(Long turn) {
        this.turn = turn;
    }

    public Long getTurn() {
        return turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> makeSalvoDTO() {
    Map<String, Object> dto = new LinkedHashMap<String, Object>();

        dto.put("turn", this.getTurn());
        dto.put("player",getGamePlayer().getPlayer().getId());
        dto.put("locations",getSalvoLocations());
    return dto;
    }
}

