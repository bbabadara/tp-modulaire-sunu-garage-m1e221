package sn.sunugarage.facturation.internal.infrastructure.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import sn.sunugarage.facturation.FacturationApi;
import sn.sunugarage.facturation.FactureDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/facturation")
@Tag(name = "Facturation", description = "Émission et paiement des factures")
public class FacturationController {

    private final FacturationApi facturation;

    public FacturationController(FacturationApi facturation) {
        this.facturation = facturation;
    }

    @GetMapping("/factures")
    public List<FactureDto> factures() {
        return facturation.factures();
    }

    @GetMapping("/factures/{id}")
    public FactureDto facture(@PathVariable UUID id) {
        return facturation.facture(id);
    }

    @GetMapping("/ordres/{ordreId}/facture")
    public FactureDto factureDe(@PathVariable UUID ordreId) {
        return facturation.factureDe(ordreId);
    }

    @PostMapping("/factures/{id}/paiement")
    public FactureDto payer(@PathVariable UUID id) {
        facturation.payer(id);
        return facturation.facture(id);
    }
}
