package sn.sunugarage.accueil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccueilModuleTest {

    @Autowired
    private AccueilApi accueil;

    @Test
    void enregistreUnClientEtSonVehicule() {
        UUID clientId = accueil.enregistrerClient("Awa Diop", "770000001");
        UUID vehiculeId = accueil.enregistrerVehicule("DK-001-AA", clientId);

        assertThat(accueil.vehiculeExiste(vehiculeId)).isTrue();
        assertThat(accueil.vehicule(vehiculeId).statut()).isEqualTo("EN_ATELIER");

        accueil.marquerSortable(vehiculeId);

        assertThat(accueil.vehicule(vehiculeId).statut()).isEqualTo("PEUT_SORTIR");
    }

    @Test
    void refuseUnVehiculePourUnClientInconnu() {
        assertThatThrownBy(() -> accueil.enregistrerVehicule("DK-002-AA", UUID.randomUUID()))
                .isInstanceOf(ClientIntrouvableException.class);
    }
}
