package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ShipController {

    @Autowired
    public PlayerRepository playerRepository;

    @Autowired
    public GamePlayerRepository gamePlayerRepository;

    @Autowired
    public ShipRepository shipRepository;

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable Long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {

        Player player = playerRepository.findPlayerByUserName(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);

        if (isGuest(authentication))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);

        if (gamePlayer == null)
            return new ResponseEntity<>(makeMap("error", "Player not found"), HttpStatus.UNAUTHORIZED);

        if (player == null)
            return new ResponseEntity<>(makeMap("error", "Player Not Found "), HttpStatus.UNAUTHORIZED);

        if (!(gamePlayer.getPlayer().getId() == player.getId()))
            return new ResponseEntity<>(makeMap("error", "Unauthorized "), HttpStatus.UNAUTHORIZED);

        if (gamePlayer.getShips().size() > 0)
            return new ResponseEntity<>(makeMap("error", "Full ships in game "), HttpStatus.FORBIDDEN);

        ships.forEach(sh -> sh.setGamePlayer(gamePlayer));
        ships.forEach(sh -> shipRepository.save(sh));

        return new ResponseEntity<>(makeMap("OK", "Success"), HttpStatus.CREATED);

    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}
