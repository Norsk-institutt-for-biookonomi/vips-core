/*
 * Copyright (c) 2014 NIBIO <http://www.nibio.no/>. 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package no.nibio.vips.core.startup;

import jakarta.servlet.ServletContextEvent;
import no.nibio.vips.model.factory.ModelFactory;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import java.util.Set;
/**
 * This class is created and method contextInitialized called when the application
 * is deployed (either at startup of application container or when redeploying the app)
 * 
 * @copyright 2013 {@link http://www.nibio.no NIBIO}
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class StartupListener implements jakarta.servlet.ServletContextListener{

    /**
     * Called when the application
     * is deployed (either at startup of application container or when redeploying the app)
     * @param sce 
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("VIPSCore system initializing");
        // We create the singleton instance of ModelFactory at startup, as this
        // takes a few seconds (due to scanning of file system)
        ModelFactory singletonFactory = ModelFactory.getInstance();

        // Configure OpenAPI to only scan VIPSCore packages to avoid leaking endpoints
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
            .resourcePackages(Set.of("no.nibio.vips.core"))
            .resourceClasses(Set.of(
                no.nibio.vips.core.service.ModelResourceImpl.class.getName(),
                no.nibio.vips.core.web.SwaggerUiResource.class.getName()
            ))
            .prettyPrint(true);
            
        try {
            new JaxrsOpenApiContextBuilder()
                .openApiConfiguration(oasConfig)
                .buildContext(true);
        } catch (OpenApiConfigurationException e) {
            e.printStackTrace();    
        }
    }

    /**
     * Called when the web app is closed
     * @param sce 
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("VIPSCore system shutting down");
    }
}
