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

package no.nibio.vips.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import no.nibio.vips.entity.ModelConfiguration;
import no.nibio.vips.entity.Result;
import no.nibio.vips.ipmdecisions.DataTransformer;
import no.nibio.vips.ipmdecisions.IPMDecisionsModelConfiguration;
import no.nibio.vips.model.ConfigValidationException;
import no.nibio.vips.model.Model;
import no.nibio.vips.model.ModelExcecutionException;
import no.nibio.vips.model.factory.DuplicateModelIdException;
import no.nibio.vips.model.factory.ModelFactory;
import no.nibio.vips.model.factory.NoSuchModelException;

/**
 * The available resources in this system
 * @copyright 2020 {@link http://www.nibio.no NIBIO}
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class ModelResourceImpl implements ModelResource{
    
    @Context
    HttpServletRequest servletRequest;
    
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
     * The default response if no path given
     * @return 
     */
    @GET
    @Path("")
    @Produces("text/html")
    @Override
    public Response defaultResponse() 
    {
        return Response.ok().entity("<html><head></head><body><a href=\"/models/\">Models</a></body></html>").build();
    }
    
    /**
     * Lists all models available in this instance, using default language (English)
     * @return list of all models available
     */
    @GET
    @Path("models/json")
    @Produces("application/json;charset=UTF-8")
    @Override
    public Response printModelListJSON()
    {
        
        return this.printModelListJSON(Model.DEFAULT_LANGUAGE);
    }
    
    /**
     * Lists all models available in this instance, using default language (English)
     * @return list of all models available
     */
    @GET
    @Path("models/json/{language}")
    @Produces("application/json;charset=UTF-8")
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
                
            } catch (InstantiationException | NoSuchModelException | IllegalAccessException ex) {
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
    @GET
    @Path("models/{language}")
    @Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelList(@PathParam("language") String language)
    {
        // test comment
        StringBuilder retVal = new StringBuilder();
        ModelFactory mF = ModelFactory.getInstance();
        List<String> keySet = new ArrayList(mF.getModelList().keySet());
        Collections.sort(keySet);
        for(String key : keySet)
        {
            try {
                retVal.append(key).append(" ").append(mF.getModelInstance(key).getModelName(language)).append("\n");
            } catch (InstantiationException | IllegalAccessException | NoSuchModelException | DuplicateModelIdException ex) {
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
        try
        {
            String description = ModelFactory.getInstance().getModelDescription(modelId,language);
            return Response.ok().entity(description).build();
        }
        catch(NoSuchModelException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
  
    /**
     * Prints the usage instructions from the requested model
     * @param modelId
     * @param language two-letter code (<a href="http://www.loc.gov/standards/iso639-2/php/English_list.php">ISO-639-2</a>)
     * @return 
     */
    @GET
    @Path("models/{modelId}/usage/{language}")
    @Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelUsage(@PathParam("modelId") String modelId, @PathParam("language") String language)
    {
        try
        {
            String usage = ModelFactory.getInstance().getModelUsage(modelId, language);
            return Response.ok().entity(usage).build();
        }
        catch(NoSuchModelException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Prints the usage instructions from the requested model
     * @param modelId
     * @param language two-letter code (<a href="http://www.loc.gov/standards/iso639-2/php/English_list.php">ISO-639-2</a>)
     * @return 
     */
    @GET
    @Path("models/{modelId}/usage")
    @Produces("text/plain;charset=UTF-8")
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
    @GET
    @Path("models/{modelId}/sampleconfig")
    @Produces("text/plain;charset=UTF-8")
    @Override
    public Response printModelSampleConfig(@PathParam("modelId") String modelId)
    {
        try {
            String sampleConfig;
            try
            {
                 sampleConfig = ModelFactory.getInstance().getModelInstance(modelId).getSampleConfig();
            }
            catch(NoSuchModelException | DuplicateModelIdException ex)
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
    @Override
    public Response runModel(ModelConfiguration config)
    {
        try
        {
            Model calledModel = ModelFactory.getInstance().getModelInstance(config.getModelId());
            calledModel.setConfiguration(config);
            List<Result> retVal = calledModel.getResult();
            if(retVal != null)
            {
                return Response.ok().entity(calledModel.getResult()).build();
            }
            else throw new ModelExcecutionException("Model returned NULL Result");
        }
        catch(InstantiationException | IllegalAccessException | ConfigValidationException | ModelExcecutionException | NoSuchModelException | DuplicateModelIdException ex)
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
    @Override
    public Response runModel(@PathParam("modelId") String modelId, ModelConfiguration config)
    {
        try
        {
            Model calledModel = ModelFactory.getInstance().getModelInstance(modelId);
            calledModel.setConfiguration(config);
            List<Result> results = calledModel.getResult();       
            if(results != null)
            {
                return Response.ok().entity(results).build();
            }
            else throw new ModelExcecutionException("Model returned NULL Result");
        }
        catch(Exception ex)
        {
            Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            if(ex instanceof ConfigValidationException){
            
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build());
            }
            else if(ex instanceof NoSuchModelException)
            {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build());
            }
            else
            {
                throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build());
            }
        }
    }
    
    /**
     * For input data coming from the IPM Decisions platform
     * @param modelId
     * @param config
     * @return 
     */
    @Override
    public Response runModelForIPMDecisions(@PathParam("modelId") String modelId, IPMDecisionsModelConfiguration config)
    {
        try
        {
            DataTransformer dt = new DataTransformer();
            if(config.getWeatherData() != null)
            {
                config.setConfigParameter("observations", dt.getVIPSWeatherData(config.getWeatherData()));
            }
            
            if(config.getConfigParameter("fieldObservations") != null && List.class.isAssignableFrom(config.getConfigParameter("fieldObservations").getClass()))
            {
                config.setConfigParameter("pestObservations", dt.getVIPSPestObservations(
                        (List) config.getConfigParameter("fieldObservations")
                        )
                );
            }
            else if(config.getConfigParameter("fieldObservations") != null && !List.class.isAssignableFrom(config.getConfigParameter("fieldObservations").getClass()))
            {
            	throw new ConfigValidationException("ERROR: fieldObservations must be of type array");
            }
            else if(config.getConfigParameter("fieldObservations") == null)
            {
                config.setConfigParameter("pestObservations",new ArrayList<>());
            }
            Model calledModel = ModelFactory.getInstance().getModelInstance(modelId);
            calledModel.setConfiguration(config);
            List<Result> results = calledModel.getResult();  
            return Response.ok().entity(dt.getIPMDecisionsModelOutput(results)).build(); 
        }
        catch(Exception ex)
        {
            Logger.getLogger(ModelResourceImpl.class.getName()).log(Level.SEVERE, null, ex);
            if(ex instanceof ConfigValidationException){
            
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build());
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
        try
        {
            String name = ModelFactory.getInstance().getModelName(modelId, language);
            return Response.ok().entity(name).build();
        }
        catch(NoSuchModelException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
        
    }

    @Override
    public Response printModelLicense(String modelId) {
        try
        {
            String license = ModelFactory.getInstance().getModelLicense(modelId);
            return Response.ok().entity(license).build();
        }
        catch(NoSuchModelException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @Override
    public Response printModelCopyright(String modelId) {
        try
        {
            String copyright = ModelFactory.getInstance().getModelCopyright(modelId);
            return Response.ok().entity(copyright).build();
        }
        catch(NoSuchModelException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }

    @Override
    public Response printModelWarningStatusInterpretation(String modelId) {
        return this.printModelWarningStatusInterpretation(modelId, Model.DEFAULT_LANGUAGE);
    }

    @Override
    public Response printModelWarningStatusInterpretation(String modelId, String language) {
        try
        {
            String interpretation = ModelFactory.getInstance().getModelWarningStatusInterpretation(modelId,language);
            return Response.ok().entity(interpretation).build();
        }
        catch(NoSuchModelException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }

    
}
