package mindpath.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Foknje7ik API",
                version = "1.0",
                description = "This is the API documentation for Tagarde, providing access to our services and data. Please refer to the terms of service and license information for usage guidelines.",
                termsOfService = "https://Foknje7ik.com/terms",
                contact = @Contact(
                        name = "Foknje7ik Support",
                        email = "support@foknje7ik.com",
                        url = "https://Foknje7ik.com/support"
                ),
                license = @License(
                        name = "Apache License 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "Local development server"
                ),
                /*@Server(
                        url = "https://api.tagamuta-valley.com",
                        description = "Production server"
                )*/
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearerAuth",
                type = SecuritySchemeType.HTTP,
                in = SecuritySchemeIn.HEADER,
                scheme = "bearer",
                bearerFormat = "JWT",
                description = "JWT Bearer Token for API authentication"
        ),
})
public class OpenApiConfig {
}