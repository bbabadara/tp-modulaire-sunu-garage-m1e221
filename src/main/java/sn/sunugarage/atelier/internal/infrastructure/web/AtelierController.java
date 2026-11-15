package sn.sunugarage.atelier.internal.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import sn.sunugarage.atelier.AtelierApi;
import sn.sunugarage.atelier.OrdreDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/atelier")
@Tag(name = "Atelier", description = "Cycle de vie des ordres de réparation")
public class AtelierController {

    private final AtelierApi atelier;

    public AtelierController(AtelierApi atelier) {
        this.atelier = atelier;
    }

    @PostMapping("/ordres")
    @ResponseStatus(HttpStatus.CREATED)
    public OrdreDto ouvrirOrdre(@RequestBody OuvrirOrdreRequest requete) {
        UUID ordreId = atelier.ouvrirOrdre(requete.vehiculeId());
        return atelier.ordre(ordreId);
    }

    @PostMapping("/ordres/{id}/main-oeuvre")
    public OrdreDto ajouterMainOeuvre(
            @PathVariable UUID id,
            @RequestBody AjouterMainOeuvreRequest requete) {
        atelier.ajouterMainOeuvre(id, requete.heures());
        return atelier.ordre(id);
    }

    @PostMapping("/ordres/{id}/pieces")
    public OrdreDto ajouterPiece(
            @PathVariable UUID id,
            @RequestBody AjouterPieceRequest requete) {
        atelier.ajouterPiece(id, requete.reference(), requete.quantite());
        return atelier.ordre(id);
    }

    @PostMapping("/ordres/{id}/cloture")
    public OrdreDto cloturer(@PathVariable UUID id) {
        atelier.cloturer(id);
        return atelier.ordre(id);
    }

    @GetMapping("/ordres/{id}")
    public OrdreDto ordre(@PathVariable UUID id) {
        return atelier.ordre(id);
    }

    @GetMapping("/vehicules/{vehiculeId}/ordre-ouvert")
    public boolean ordreOuvert(@PathVariable UUID vehiculeId) {
        return atelier.ordreOuvert(vehiculeId);
    }

    public record OuvrirOrdreRequest(UUID vehiculeId) {
    }

    public record AjouterMainOeuvreRequest(BigDecimal heures) {
    }

    public record AjouterPieceRequest(String reference, int quantite) {
    }
}
