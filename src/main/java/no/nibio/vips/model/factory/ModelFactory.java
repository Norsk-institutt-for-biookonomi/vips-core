/*
 * Copyright (c) 2014 NIBIO <http://www.nibio.no/>. 
 * 
 * This file is part of VIPSCore.
 * VIPSCore is free software: you can redistribute it and/or modify
 * it under the terms of the NIBIO Open Source License as published by 
 * NIBIO, either version 1 of the License, or (at your option) any
 * later version.
 * 
 * VIPSCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * NIBIO Open Source License for more details.
 * 
 * You should have received a copy of the NIBIO Open Source License
 * along with VIPSCore.  If not, see <http://www.nibio.no/licenses/>.
 * 
 */

package no.nibio.vips.model.factory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.nibio.vips.model.Model;

/**
 * Provides the models available. Singleton.
 * 
 * <h2>Deploying models</h2>
 * To make a model available, it must
 * <ul>
 *  <li>Implement the {@link no.nibio.vips.model.Model} interface</li>
 *  <li>
 *      Be on the class path of the running application, preferably in a separate JAR.
 *      The guaranteed way to make it work is to put it inside the VIPSCore WAR-file, in /WEB-INF/lib
 *      This could be accomplished most easily by using a ZIP application, or accessing
 *      The VIPSCore source and building it with the JAR(s) as (a) dependenc(y/ies)
 *  </li>
 * </ul>
 * 
 * @copyright 2013 {@link http://www.nibio.no NIBIO}
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
public class ModelFactory {
    // The singleton instance
    private static ModelFactory modelFactory;
    // Internal collection of models found from scanning the class path
    private Map<String,Model> models;
    // Internal list of model IDs and names
    private Map<String,String> modelList;
    // If duplicate ids found, put them here
    private Set<String> duplicateModelIds;
    private final static String DUPLICATE_MODEL_ID_WARNING = "ERROR: DUPLICATE MODELS EXIST FOR MODEL WITH THIS ID. CHECK SERVER LOGS.";
    
    /**
     * Declared private to protect from unauthorized instatiation. Part of the
     * Singleton design pattern.
     */
    private ModelFactory()
    {
        this.init();
    }
    
    /**
     * Overriding {@link java.lang.Object}'s clone() method to prevent cloning.
     * Part of the Singleton design pattern.
     * @return
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    /**
     * Use this method to get the singleton instance of the class
     * @return an instance (the only one in the JVM) of ModelFactory
     */
    public static synchronized ModelFactory getInstance(){
        if(modelFactory == null)
        {
            modelFactory = new ModelFactory();
        }
        return modelFactory;
    }
    
    /**
     * Scans the whole class path for classes implementing the Model interface.
     * Builds an inventory. 
     */
    private void init(){
        /* 
         * Reflections API insists on using at least one package prefix. So we scan
         * through all TLDs. List is compiled from 
         * http://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
         */ 
        String[] defaultTopDomains = {
            "aero","asia","biz","cat","com","coop","info","int","jobs","mobi",
            "museum","name","net","org","post","pro","tel","travel","edu","gov",
            "ac","ad","ae","af","ag","ai","al","am","an","ao","aq","ar","as","at",
            "au","aw","ax","az","ba","bb","bd","be","bf","bg","bh","bi","bj","bm",
            "bn","bo","br","bs","bt","bv","bw","by","bz","ca","cc","cd","cf","cg",
            "ch","ci","ck","cl","cm","cn","co","cr","cs","cu","cv","cx","cy","cz",
            "dd","de","dj","dk","dm","do","dz","ec","ee","eg","eh","er","es","et",
            "eu","fi","fj","fk","fm","fo","fr","ga","gb","gd","ge","gf","gg","gh",
            "gi","gl","gm","gn","gp","gq","gr","gs","gt","gu","gw","gy","hk","hm",
            "hn","hr","ht","hu","id","ie","il","im","in","io","iq","ir","is","it",
            "je","jm","jo","jp","ke","kg","kh","ki","km","kn","kp","kr","kw","ky",
            "kz","la","lb","lc","li","lk","lr","ls","lt","lu","lv","ly","ma","mc",
            "md","me","mg","mh","mk","ml","mm","mn","mo","mp","mq","mr","ms","mt",
            "mu","mv","mw","mx","my","mz","na","nc","ne","nf","ng","ni","nl","no",
            "np","nr","nu","nz","om","pa","pe","pf","pg","ph","pk","pl","pm","pn",
            "pr","ps","pt","pw","py","qa","re","ro","rs","ru","rw","sa","sb","sc",
            "sd","se","sg","sh","si","sj","sk","sl","sm","sn","so","sr","ss","st",
            "su","sv","sx","sy","sz","tc","td","tf","tg","th","tj","tk","tl","tm",
            "tn","to","tp","tr","tt","tv","tw","tz","ua","ug","uk","us","uy","uz",
            "va","vc","ve","vg","vi","vn","vu","wf","ws","ye","yt","yu","za","zm",
            "zw",
        };
        
        /**
         * Warning: This generates a WARNING from a filesystem/JARfile scan error
         * for each TLD not found. Use application container's config to suppress
         * this in log files. 
         */

        Set<Class<? extends Model>> subTypes = new HashSet();
        
        /*for(String topDomain: defaultTopDomains)
        {
            reflections = new Reflections(topDomain);
            subTypes.addAll(reflections.getSubTypesOf(Model.class));   
        }*/
        
        try (ScanResult scanResult =                // Assign scanResult in try-with-resources
        new ClassGraph()                    // Create a new ClassGraph instance
            .verbose()                      // If you want to enable logging to stderr
            .enableClassInfo()            // Scan classes, methods, fields, annotations
            //.acceptPackages("com.xyz")      // Scan com.xyz and subpackages
            .scan()) {                      // Perform the scan and return a ScanResult
            
            //ClassInfoList classInfoList = 
            for(ClassInfo cInfo:scanResult.getClassesImplementing(Model.class))
            {
                subTypes.add(cInfo.loadClass(Model.class));
            }

                 
        /**
         * Iterates and instantiates one object for each class
         */
        this.models = new HashMap();
        for(Class<? extends Model> subType : subTypes)
        {
               try {
                   Model model = subType.getDeclaredConstructor().newInstance();
                   if(models.get(model.getModelId().toString()) == null)
                   {
                        models.put(model.getModelId().toString(), model);
                   }
                   else
                   {
                       this.addDuplicateModelId(model.getModelId().toString());
                       String message = "Duplicate model Ids found for at least two models: ModelId \"" 
                               + model.getModelId() + "\" is assigned to " 
                               + model.getClass().getName() + " and " 
                               + models.get(model.getModelId().toString()).getClass().getName();
                       throw new DuplicateModelIdException(message);
                   }
                   //System.out.println("Model " + model.getModelName() + " with id=" + model.getModelId().toString() + " was found");
               } catch (InstantiationException | IllegalAccessException | DuplicateModelIdException | NoClassDefFoundError | NoSuchMethodException |
                        InvocationTargetException ex) {
                   Logger.getLogger(ModelFactory.class.getName()).log(Level.SEVERE, null, ex);
               }
        }
        }
    }
    
    /**
     * TODO: Add support for locale
     * @return a list of all the available models in this JVM
     */
    public synchronized Map<String,String> getModelList()
    {
        if(this.modelList == null)
        {
            this.modelList = new HashMap();
            for(Model model:this.models.values())
            {
                String modelName = this.isDuplicateModelId(model.getModelId().toString()) ? this.getDuplicateModelIdWarning() : model.getModelName();
                this.modelList.put(model.getModelId().toString(), modelName);
            }
        }
        return this.modelList;
    }
    
    /**
     * Gets the name of the model, in the appropriate language
     * @param modelId
     * @param language
     * @return 
     */
    public String getModelName(String modelId, String language) throws NoSuchModelException
    {
        if(this.isDuplicateModelId(modelId))
        {
            return this.getDuplicateModelIdWarning();
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return language != null ? this.models.get(modelId).getModelName(language) : this.models.get(modelId).getModelName();
    }
    
    /**
     * 
     * @param modelId
     * @return The license for the requested model
     */
    public String getModelLicense(String modelId) throws NoSuchModelException
    {
        if(this.isDuplicateModelId(modelId))
        {
            return this.getDuplicateModelIdWarning();
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return this.models.get(modelId).getLicense();
    }
    
    /**
     * 
     * @param modelId
     * @return the copyright information for the requested model
     */
    public String getModelCopyright(String modelId) throws NoSuchModelException
    {
        if(this.isDuplicateModelId(modelId))
        {
            return this.getDuplicateModelIdWarning();
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return this.models.get(modelId).getCopyright();
    }
    
    /**
     * 
     * @param modelId ID of the requested model
     * @param language language two-letter code (<a href="http://www.loc.gov/standards/iso639-2/php/English_list.php">ISO-639-2</a>)
     * @return Description of model in requested language (Default/fallback is English)
     */
    public String getModelDescription(String modelId, String language) throws NoSuchModelException {
        if(this.isDuplicateModelId(modelId))
        {
            return this.getDuplicateModelIdWarning();
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return language != null ? this.models.get(modelId).getModelDescription(language) : this.models.get(modelId).getModelDescription();
    }
    
    /**
     * @param modelId the id of the Model
     * @return usage instructions for the requested Model
     * 
     */
    public String getModelUsage(String modelId, String language) throws NoSuchModelException
    {
        if(this.isDuplicateModelId(modelId))
        {
            return this.getDuplicateModelIdWarning();
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return language != null ? this.models.get(modelId).getModelUsage(language) : this.models.get(modelId).getModelUsage();
    }
    
    /**
     * 
     * @param modelId the id of the Model
     * @return a new instance of the model with the given Id
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public Model getModelInstance(String modelId) throws 
                InstantiationException, 
                IllegalAccessException, 
                DuplicateModelIdException, 
                NoSuchModelException
    {
        if(this.isDuplicateModelId(modelId))
        {
            throw new DuplicateModelIdException(this.getDuplicateModelIdWarning());
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return this.models.get(modelId).getClass().newInstance();
    }

    private void addDuplicateModelId(String modelId) {
        if(this.duplicateModelIds == null)
        {
            this.duplicateModelIds = new HashSet();
        }
        this.duplicateModelIds.add(modelId);
    }

    private Set<String> getDuplicateModelIds()
    {
        return this.duplicateModelIds;
    }
    
    private boolean isDuplicateModelId(String modelId)
    {
        return this.duplicateModelIds == null ? false : this.duplicateModelIds.contains(modelId);
    }

    private String getDuplicateModelIdWarning() {
        return DUPLICATE_MODEL_ID_WARNING;
    }

    public String getModelWarningStatusInterpretation(String modelId, String language) throws NoSuchModelException {
        if(this.isDuplicateModelId(modelId))
        {
            return this.getDuplicateModelIdWarning();
        }
        if(this.models.get(modelId) == null)
        {
            throw new NoSuchModelException("ERROR: No model found with id = " + modelId);
        }
        return language != null ? this.models.get(modelId).getWarningStatusInterpretation(language) : this.models.get(modelId).getWarningStatusInterpretation();
    }
    
    
    
}
