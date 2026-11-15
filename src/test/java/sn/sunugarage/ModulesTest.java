package sn.sunugarage;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulesTest {

    private final ApplicationModules modules = ApplicationModules.of(SunuGarageApplication.class);

    @Test
    void verifieLesFrontieresDesModules() {
        modules.verify();
    }
}
