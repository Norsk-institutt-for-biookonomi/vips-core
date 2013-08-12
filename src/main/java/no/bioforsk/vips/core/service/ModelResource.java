package no.bioforsk.vips.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import no.bioforsk.vips.entity.ModelConfiguration;
import no.bioforsk.vips.entity.Result;
import no.bioforsk.vips.entity.ResultImpl;
import no.bioforsk.vips.entity.Weather;
import no.bioforsk.vips.model.Model;
import no.bioforsk.vips.model.factory.ModelFactory;

/**
 * The available resources in this system
 * @copyright 2013 {@link http://www.bioforsk.no Bioforsk}
 * @author Tor-Einar Skog <tor-einar.skog@bioforsk.no>
 */
@Path("") 
public class ModelResource {
    
    /**
     * Lists all models available in this instance
     * @return 
     */
    @GET
    @Path("models")
    @Produces("text/plain;charset=UTF-8")
    public Response printModels()
    {
        StringBuilder retVal = new StringBuilder();
        ModelFactory mF = ModelFactory.getInstance();
        for(String key : mF.getModelList().keySet())
        {
            retVal.append(key).append("\n");
        }
        return Response.ok().entity(retVal.toString()).build();
    }
    
    /**
     * Prints the usage instructions from the requested model
     * @param modelId
     * @return 
     */
    @GET
    @Path("models/{modelId}/usage")
    @Produces("text/plain;charset=UTF-8")
    public Response printModelUsage(@PathParam("modelId") String modelId)
    {
        String usage = ModelFactory.getInstance().getModelUsage(modelId);
        return Response.ok().entity(usage).build();
    }
    
    /** Tests for XML and JSON output */
    @GET
    @Path("models/resulttest")
    @Produces("application/json")
    public Response printResultTest()
    {
        List<Result> allResults = new ArrayList();
        Result retVal = new ResultImpl();
        retVal.setResultValidTime(new Date());
        retVal.setValue("Parameter1", "Verdi1");
        retVal.setValue("Parameter2", "Verdi2");
        allResults.add(retVal);
        retVal = new ResultImpl();
        retVal.setResultValidTime(new Date());
        retVal.setValue("Parameter1", "Verdi5");
        retVal.setValue("Parameter2", "Verdi7");
         allResults.add(retVal);
        return Response.ok().entity(allResults).build();
    }
    
    @GET
    @Path("weather/xml")
    @Produces("application/xml")
    public Response printWeatherXML()
    {
        return Response.ok().entity(new Weather(new Date(),"Goddag, goddag. Dette her er Meteorologisk institutt.æøåæøå")).build();
    }
    
    /**
     * Marshalling fra POJO til JSON gjøres automagisk (med Jackson, tror jeg)
     * @return gladmelding
     */
    @GET
    @Path("weather/json")
    @Produces("application/json")
    public Response printWeatherJSON()
    {
        return Response.ok().entity(new Weather(new Date(),"Goddag, goddag. Dette her er Meteorologisk institutt.æøåæøå")).build();
    }
    
    /**
     * Test for input / POST
     */
    
    @POST
    @Path("models/run")
    @Consumes("application/json")
    @Produces("application/json")
    //@Produces("text/plain;charset=UTF-8")
    public Response runModel(ModelConfiguration config)
    {
        //return Response.ok().entity("runModell called").build();
        try
        {
            Model calledModel = ModelFactory.getInstance().getModelInstance(config.getModelId());
            return Response.ok().entity(calledModel.getResult()).build();
        }
        catch(InstantiationException | IllegalAccessException iae)
        {
            return Response.serverError().build();
        }
    }
}
