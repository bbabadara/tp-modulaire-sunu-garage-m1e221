package sn.sunugarage.stock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StockModuleTest {

    @Autowired
    private StockApi stock;

    @Test
    void sortLesPiecesEtRefuseLeDepassement() {
        String reference = "PLAQUETTE-STOCK-TEST";
        stock.ajouterPiece(reference, "Plaquettes de frein", 4, 15_000, 3);

        assertThat(stock.disponible(reference)).isEqualTo(4);
        assertThat(stock.piecesSousSeuil()).extracting(PieceDto::reference).doesNotContain(reference);

        PieceSortieDto sortie = stock.sortirPiece(reference, 2);

        assertThat(sortie.prixUnitaire()).isEqualTo(15_000);
        assertThat(stock.disponible(reference)).isEqualTo(2);
        assertThat(stock.piecesSousSeuil()).extracting(PieceDto::reference).contains(reference);

        assertThatThrownBy(() -> stock.sortirPiece(reference, 3))
                .isInstanceOf(StockInsuffisantException.class);
        assertThat(stock.disponible(reference)).isEqualTo(2);
    }
}
