package no.nibio.vips.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "VIPSCore API",
        version = "v1",
        description = "OpenAPI documentation for VIPSCore endpoints.",
        contact = @Contact(name = "NIBIO", url = "https://www.nibio.no"),
        license = @License(name = "NIBIO Open Source License")
    )
)
public class OpenApiConfig {
    // Annotation-only configuration holder
}
