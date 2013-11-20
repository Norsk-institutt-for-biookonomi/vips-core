package no.bioforsk.vips.core.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import no.bioforsk.vips.entity.ModelConfiguration;
import no.bioforsk.vips.entity.Result;
import no.bioforsk.vips.model.ConfigValidationException;
import no.bioforsk.vips.model.Model;
import no.bioforsk.vips.model.ModelExcecutionException;
import no.bioforsk.vips.model.factory.DuplicateModelIdException;
import no.bioforsk.vips.model.factory.ModelFactory;

/**
 * The available resources in this system
 * @copyright 2013 {@link http://www.bioforsk.no Bioforsk}
 * @author Tor-Einar Skog <tor-einar.skog@bioforsk.no>
 */
public class ModelResourceImpl implements ModelResource{
     
    /**
     * Lists all models available in this instance, using default language (English)
     * @return list of all models available
     */
    @Override
    public Response printModelList()
    {
        return this.printModelList(Model.DEFAULT_LANGUAGE);
        
    }
    
    /**
     * Lists all models available in this instance, using default language (English)
     * @return list of all models available
     */
    //@GET
    //@Path("models/json")
    //@Produces("application/json;charset=UTF-8")
    @Override
    public Response printModelListJSON()
    {
        
        return this.printModelListJSON(null);
    }
    
    /**
     * Lists all models available in this instance, using default language (English)
     * @return list of all models available
     */
    //@GET
    //@Path("models/json/{language}")
    //@Produces("application/json;charset=UTF-8")
    @Override
    public Response printModelListJSON(@PathParam("language") String language)
    {
        StringBuilder retVal = new StringBuilder();
        ModelFactory mF = ModelFactory.getInstance();
        retVal.append("[");
        boolean first=true;
        for(String key : mF.getModelList().keySet())
        {
            try {
                if(first) first=false;
                else retVal.append(",");
                retVal.append("{")
                    .append("\"modelId\":\"").append(key).append("\", \"modelName\":\"");
                try
                { 
                    retVal.append(mF.getModelInstance(key).getModelName(language));
                }
                catch(DuplicateModelIdException ex)
                {
                    retVal.append(ex.getMessage());
                }
                retVal.append("\"").append("}");
                
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        retVal.append("]");
        return Response.ok().entity(retVal.toString()).build();
    }
    
    /**
     * Lists all models available in this instance, using default language (English)
     * @return list of all models available
     */
    //@GET
    //@Path("models/{language}")
    //@Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelList(@PathParam("language") String language)
    {
        // test comment
        StringBuilder retVal = new StringBuilder();
        ModelFactory mF = ModelFactory.getInstance();
        for(String key : mF.getModelList().keySet())
        {
            try {
                retVal.append(key).append(" ").append(mF.getModelInstance(key).getModelName(language)).append("\n");
            } catch (InstantiationException | IllegalAccessException | DuplicateModelIdException ex) {
                retVal.append(key).append(" ").append(ex.getMessage()).append("\n");
                Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return Response.ok().entity(retVal.toString()).build();
    }
    
    @Override
    public Response printModelDescription(String modelId) {
        return this.printModelDescription(modelId, Model.DEFAULT_LANGUAGE);
    }

    @Override
    public Response printModelDescription(String modelId, String language) {
        String description = ModelFactory.getInstance().getModelDescription(modelId,language);
        return Response.ok().entity(description).build();
    }
  
    /**
     * Prints the usage instructions from the requested model
     * @param modelId
     * @param language two-letter code (<a href="http://www.loc.gov/standards/iso639-2/php/English_list.php">ISO-639-2</a>)
     * @return 
     */
    //@GET
    //@Path("models/{modelId}/usage/{language}")
    //@Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelUsage(@PathParam("modelId") String modelId, @PathParam("language") String language)
    {
        String usage = ModelFactory.getInstance().getModelUsage(modelId, language);
        return Response.ok().entity(usage).build();
    }
    
    /**
     * Prints the usage instructions from the requested model
     * @param modelId
     * @param language two-letter code (<a href="http://www.loc.gov/standards/iso639-2/php/English_list.php">ISO-639-2</a>)
     * @return 
     */
    //@GET
    //@Path("models/{modelId}/usage")
    //@Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelUsage(@PathParam("modelId") String modelId)
    {
        return this.printModelUsage(modelId, Model.DEFAULT_LANGUAGE);
    }
    
    /**
     * Outputs a sample configuration for the requested model
     * @param modelId the requested model
     * @return 
     */
    //@GET
    //@Path("models/{modelId}/sampleconfig")
    //@Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelSampleConfig(@PathParam("modelId") String modelId)
    {
        try {
            String sampleConfig;
            try
            {
                 sampleConfig = ModelFactory.getInstance().getModelInstance(modelId).getSampleConfig();
            }
            catch(DuplicateModelIdException ex)
            {
                sampleConfig = ex.getMessage();
            }
            return Response.ok().entity(sampleConfig).build();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }
    }
    
    /**
     * Instantiates and runs the requested model
     * @param config input data for the model
     * @return list of result objects
     */
    //@POST
    //@Path("models/run")
    //@Consumes("application/json")
    //@Produces("application/json")
    @Override
    public Response runModel(ModelConfiguration config)
    {
        try
        {
            Model calledModel = ModelFactory.getInstance().getModelInstance(config.getModelId());
            calledModel.setConfiguration(config);
            return Response.ok().entity(calledModel.getResult()).build();
        }
        catch(InstantiationException | IllegalAccessException | ConfigValidationException | ModelExcecutionException | DuplicateModelIdException ex)
        {
            Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Instantiates and runs the requested model
     * @param modelId
     * @param config input data for the model
     * @return list of result objects
     */
    //@POST
    //@Path("models/{modelId}/run")
    //@Consumes("application/json")
    //@Produces("application/json")
    @Override
    public Response runModel(@PathParam("modelId") String modelId, ModelConfiguration config)
    {
        try
        {
            Model calledModel = ModelFactory.getInstance().getModelInstance(modelId);
            calledModel.setConfiguration(config);
            List<Result> results = calledModel.getResult();         
            return Response.ok().entity(results).build();
        }
        catch(InstantiationException | IllegalAccessException | ConfigValidationException | ModelExcecutionException | DuplicateModelIdException ex)
        {
            Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            if(ex instanceof ConfigValidationException){
            
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(ex).build());
            }
            else
            {
                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build());
            }
        }
    }

    @Override
    public Response printModelName(String modelId) {
        return this.printModelName(modelId, Model.DEFAULT_LANGUAGE);
    }

    @Override
    public Response printModelName(String modelId, String language) {
        String name = ModelFactory.getInstance().getModelName(modelId, language);
        return Response.ok().entity(name).build();
    }

    
}
