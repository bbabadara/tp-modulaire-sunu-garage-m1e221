package sn.sunugarage.facturation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class FacturationModuleTest {

    @Autowired
    private FacturationApi facturation;

    @Test
    void calculeLeTotalEtEncaisseLaFacture() {
        UUID ordreId = UUID.randomUUID();
        FactureRequest requete = new FactureRequest(
                ordreId,
                UUID.randomUUID(),
                new BigDecimal("2"),
                List.of(new LignePieceFacture("PLAQUETTE-FACT-TEST", 2, 15_000)));

        UUID factureId = facturation.emettreFacture(requete);

        FactureDto facture = facturation.facture(factureId);
        assertThat(facture.total()).isEqualTo(50_000);
        assertThat(facture.statut()).isEqualTo("EMISE");

        facturation.payer(factureId);

        assertThat(facturation.facture(factureId).statut()).isEqualTo("PAYEE");
        assertThat(facturation.factureDe(ordreId).id()).isEqualTo(factureId);
        assertThatThrownBy(() -> facturation.payer(factureId))
                .isInstanceOf(FactureDejaPayeeException.class);
    }
}
