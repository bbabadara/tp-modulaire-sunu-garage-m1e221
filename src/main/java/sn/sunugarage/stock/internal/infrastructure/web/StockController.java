package sn.sunugarage.stock.internal.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import sn.sunugarage.stock.PieceDto;
import sn.sunugarage.stock.StockApi;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@Tag(name = "Stock", description = "Pièces, quantités, prix et alertes de seuil")
public class StockController {

    private final StockApi stock;

    public StockController(StockApi stock) {
        this.stock = stock;
    }

    @PostMapping("/pieces")
    @ResponseStatus(HttpStatus.CREATED)
    public PieceDto ajouterPiece(@RequestBody AjouterPieceRequest requete) {
        stock.ajouterPiece(
                requete.reference(),
                requete.libelle(),
                requete.quantite(),
                requete.prixUnitaire(),
                requete.seuilAlerte());
        return stock.piece(requete.reference());
    }

    @GetMapping("/pieces/{reference}")
    public PieceDto piece(@PathVariable String reference) {
        return stock.piece(reference);
    }

    @GetMapping("/pieces/{reference}/disponible")
    public int disponible(@PathVariable String reference) {
        return stock.disponible(reference);
    }

    @GetMapping("/alertes")
    public List<PieceDto> piecesSousSeuil() {
        return stock.piecesSousSeuil();
    }

    public record AjouterPieceRequest(
            String reference,
            String libelle,
            int quantite,
            long prixUnitaire,
            int seuilAlerte) {
    }
}
