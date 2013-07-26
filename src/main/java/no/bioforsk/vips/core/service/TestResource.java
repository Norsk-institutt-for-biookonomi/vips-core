package no.bioforsk.vips.core.service;

import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import no.bioforsk.vips.core.entities.Weather;

/**
 *
 * @author treinar
 */
@Path("/") 
public class TestResource {
    @GET
    @Path("Hello/{name}") 
    public Response printMessageHolihili(@PathParam("name") String name){
        return Response.ok().entity("Neimen heisann, " + name).build();
    };
    
    @GET
    @Path("weather/xml")
    @Produces("application/xml")
    public Weather printWeatherXML()
    {
        return new Weather(new Date(),"Goddag, goddag. Dette her er Meteorologisk institutt.");
    }
    
    /**
     * Marshalling fra POJO til JSON gjøres automagis (med Jackson)
     * @return 
     */
    @GET
    @Path("weather/json")
    @Produces("application/json")
    public Response printWeatherJSON()
    {
        return Response.ok().entity(new Weather(new Date(),"Goddag, goddag. Dette her er Meteorologisk institutt.")).build();
    }
    

}
