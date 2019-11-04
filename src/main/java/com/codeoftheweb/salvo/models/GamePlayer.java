package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {

//---------ATRIBUTES--------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_ID")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_ID")
    private Player player;


    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    Set<Salvo> salvoes;


//---------CONSTRUCTORS--------------

    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player, LocalDateTime parse) {
        this.game = game;
        this.player = player;
        this.joinDate = parse;
    }

//---------GETTERS AND SETTERS--------------

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public Set<Ship> getShips() {
        return ships;
    }

//---------METHODS--------------

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
        return dto;
    }

    public List<Map<String, Object>> makeGamePlayerSalvoesDTO() {
        return this.getSalvoes().stream().map(sv -> sv.makeSalvoDTO()).collect(toList());
    }

    public GamePlayer getOpponent() {
        return this.getGame().getGamePlayers().stream().filter(gp -> gp.getId() != this.getId()).findFirst().orElse(null);
    }

    public Map<String, Object> makeHitsDTO() {

        Map<String, Object> dto = new LinkedHashMap();

        GamePlayer opponent = this.getOpponent();


        if (opponent == null) {
            dto.put("self", new ArrayList<>());
            dto.put("opponent", new ArrayList<>());
            return dto;
        }

        dto.put("self", this.getHits(opponent));
        dto.put("opponent", opponent.getHits(this));
        return dto;
    }

    private List<Map<String, Object>> getHits(GamePlayer opponent) {

        List<Map<String, Object>> listHits = new ArrayList<Map<String, Object>>();

        Map<String, Object> info = null;

        Map<String, Object> damages = null;

        long carrierAcum = 0;
        long battleshipAcum = 0;
        long submarineAcum = 0;
        long destroyerAcum = 0;
        long patrolBoatAcum = 0;

        for (Salvo salvoOpponent : opponent.getSalvoes()
                .stream()
                .sorted((a, b) -> (int) (a.getTurn() - b.getTurn()))
                .collect(toList())) {

            info = infoHits(salvoOpponent);
            damages = (Map<String, Object>) info.get("damages");

            carrierAcum += (long) damages.get("carrier");
            battleshipAcum += (long) damages.get("battleship");
            submarineAcum += (long) damages.get("submarine");
            destroyerAcum += (long) damages.get("destroyer");
            patrolBoatAcum += (long) damages.get("patrolboat");

            info = setAcums(info, carrierAcum, battleshipAcum, submarineAcum, destroyerAcum, patrolBoatAcum);

            listHits.add(info);
        }
        return sortedObjectTurn(listHits);
    }

    private Map<String, Object> infoHits(Salvo salvoOpponent) {

        Map<String, Object> hits = new LinkedHashMap<String, Object>();

        List<String> hitLocations = hitsLocations(salvoOpponent.getSalvoLocations(), this.getShips());

        hits.put("turn", salvoOpponent.getTurn());
        hits.put("hitLocations", hitLocations);
        hits.put("damages", damages(salvoOpponent));
        hits.put("missed", salvoOpponent.getSalvoLocations().size() - hitLocations.size());

        return hits;
    }

    private List<String> hitsLocations(List<String> salvoLocation, Set<Ship> ships) {

        List<String> hitLocation = new ArrayList<>();

        ships.forEach(ship -> {
            salvoLocation.stream().forEach(str -> {
                if (ship.getLocations().contains(str))
                    hitLocation.add(str);
            });
        });

        return hitLocation;

    }

    private Map<String, Object> damages(Salvo salvoOpponent) {
        Map<String, Object> damagesShips = new LinkedHashMap<>();
        Set<Ship> ships = this.getShips();

        String carrier = "carrier";
        String battleships = "battleship";
        String submarine = "submarine";
        String destroyer = "destroyer";
        String patrolboat = "patrolboat";
        String hits = "Hits";

        long countHitsCarrier = countHits(carrier, salvoOpponent, ships);
        long countHitsBattleships = countHits(battleships, salvoOpponent, ships);
        long countHitsSubmarine = countHits(submarine, salvoOpponent, ships);
        long countHitsDestroyer = countHits(destroyer, salvoOpponent, ships);
        long countHitsPatrolBoat = countHits(patrolboat, salvoOpponent, ships);

        damagesShips.put(carrier + hits, countHitsCarrier);
        damagesShips.put(battleships + hits, countHitsBattleships);
        damagesShips.put(submarine + hits, countHitsSubmarine);
        damagesShips.put(destroyer + hits, countHitsDestroyer);
        damagesShips.put(patrolboat + hits, countHitsPatrolBoat);

        damagesShips.put(carrier, countHitsCarrier);
        damagesShips.put(battleships, countHitsBattleships);
        damagesShips.put(submarine, countHitsSubmarine);
        damagesShips.put(destroyer, countHitsDestroyer);
        damagesShips.put(patrolboat, countHitsPatrolBoat);

        return damagesShips;

    }

    private long countHits(String shipType, Salvo salvoOpponent, Set<Ship> ships) {

        Ship ship = ships.stream()
                .filter(sh -> sh.getType().equals(shipType))
                .findFirst().get();

        long result = ship.getLocations()
                .stream()
                .filter(str -> salvoOpponent.getSalvoLocations().contains(str))
                .count();

        return result;

    }

    private Map<String, Object> setAcums(Map<String, Object> info, long carrierAcum, long battleshipAcum, long submarineAcum, long destroyerAcum, long patrolBoatAcum) {

        Map<String, Object> dto = (Map<String, Object>) info.get("damages");

        dto.put("carrier", carrierAcum);
        dto.put("battleship", battleshipAcum);
        dto.put("submarine", submarineAcum);
        dto.put("destroyer", destroyerAcum);
        dto.put("patrolboat", patrolBoatAcum);

        info.put("damages", dto);

        return info;
    }

    private List<Map<String, Object>> sortedObjectTurn(List<Map<String, Object>> listObject) {
        return listObject.stream().sorted((o1, o2) -> (int) ((long) o1.get("turn") - (long) o2.get("turn"))).collect(toList());
    }

    public String getGameState() {

        GamePlayer gamePlayerOpponent = this.getOpponent();

        Map<String, Object> hits = this.makeHitsDTO();

        String result = "";

        if (this.getShips().isEmpty())
            result = "PLACESHIPS";

        else if (gamePlayerOpponent == null || gamePlayerOpponent.getShips().size()==0)
            result = "WAITINGFOROPP";


        else if ( gamePlayerOpponent.getSalvoes().size() == 0 && this.getSalvoes().size() == 0)
            result = "PLAY";

        else if ( this.getSalvoes().size()>gamePlayerOpponent.getSalvoes().size() )
            result = "WAIT";

        else if (this.getSalvoes().size()< gamePlayerOpponent.getSalvoes().size())
            result = "PLAY";

        else if(this.getSalvoes().size() == gamePlayerOpponent.getSalvoes().size())
            result = isOver((List<Map<String, Object>>) hits.get("self"),(List<Map<String,Object>>)hits.get("opponent"));

        return result;
    }

    private String isOver(List<Map<String,Object>> self ,List<Map<String,Object>> opponent ) {

        String result = "PLAY";

        long totalShipsLocations = this.getShips().stream().map(ship -> ship.getLocations()).flatMap(x->x.stream()).count();

        if (self.isEmpty() || opponent.isEmpty())
            return result;

        else if ( gamePlayerWon( totalShipsLocations, self.get( self.size()-1 ), opponent.get( opponent.size()-1 ) ) )
            result = "WON";

        else if ( gamePlayerLost(totalShipsLocations, self.get( self.size()-1 ), opponent.get( opponent.size()-1 ) ) )
            result = "LOST";

        else if ( gamePlayerTie( totalShipsLocations, self.get( self.size()-1 ), opponent.get( opponent.size()-1 ) ) )
            result ="TIE";

        return result;
    }

    private boolean gamePlayerWon(long totalShipsLocations,Map<String, Object> self, Map<String, Object> opponent) {

        Map<String, Object> selfDamages = (Map<String, Object>) self.get("damages");
        Map<String, Object> opponentDamages = (Map<String, Object>) opponent.get("damages");

        long totalSelfHits = totalShipsLocations - totalAcum(selfDamages);
        long totalOpponent = totalShipsLocations - totalAcum(opponentDamages);

        return totalSelfHits>0 && totalOpponent==0;
    }

    private boolean gamePlayerTie(long totalShipsLocations,Map<String, Object> self,Map<String, Object> opponent) {

        Map<String, Object> selfDamages = (Map<String, Object>) self.get("damages");
        Map<String, Object> opponentDamages = (Map<String, Object>) opponent.get("damages");

        long totalSelfHits = totalShipsLocations - totalAcum(selfDamages);
        long totalOpponent = totalShipsLocations - totalAcum(opponentDamages);

        return totalSelfHits==0 && totalOpponent==0;
    }

    private boolean gamePlayerLost( long totalShipsLocations,Map<String, Object> self,Map<String, Object> opponent){
        Map<String, Object> selfDamages = (Map<String, Object>) self.get("damages");
        Map<String, Object> opponentDamages = (Map<String, Object>) opponent.get("damages");

        long totalSelfHits = totalShipsLocations - totalAcum(selfDamages);
        long totalOpponent = totalShipsLocations - totalAcum(opponentDamages);

        return totalSelfHits==0 && totalOpponent>0;
    }

    private long totalAcum( Map<String, Object> damages) {

        return (long)damages.get("carrier") +(long) damages.get("battleship") + (long)damages.get("submarine") + (long)damages.get("destroyer") + (long)damages.get("patrolboat");
    }

}
