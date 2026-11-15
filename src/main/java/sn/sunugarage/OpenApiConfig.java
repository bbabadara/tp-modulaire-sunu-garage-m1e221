package sn.sunugarage;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sunuGarageOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SunuGarage API")
                        .description("Monolithe modulaire — atelier de réparation automobile à Dakar.")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi accueilApi() {
        return GroupedOpenApi.builder().group("accueil").pathsToMatch("/api/accueil/**").build();
    }

    @Bean
    public GroupedOpenApi stockApi() {
        return GroupedOpenApi.builder().group("stock").pathsToMatch("/api/stock/**").build();
    }

    @Bean
    public GroupedOpenApi atelierApi() {
        return GroupedOpenApi.builder().group("atelier").pathsToMatch("/api/atelier/**").build();
    }

    @Bean
    public GroupedOpenApi facturationApi() {
        return GroupedOpenApi.builder().group("facturation").pathsToMatch("/api/facturation/**").build();
    }
}
