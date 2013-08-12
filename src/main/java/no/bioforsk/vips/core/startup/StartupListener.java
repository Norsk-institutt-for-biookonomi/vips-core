package no.bioforsk.vips.core.startup;

import javax.servlet.ServletContextEvent;

import no.bioforsk.vips.model.factory.ModelFactory;
/**
 * This class is created and method contextInitialized called when the application
 * is deployed (either at startup of application container or when redeploying the app)
 * 
 * @copyright 2013 {@link http://www.bioforsk.no Bioforsk}
 * @author Tor-Einar Skog <tor-einar.skog@bioforsk.no>
 */
public class StartupListener implements javax.servlet.ServletContextListener{

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
