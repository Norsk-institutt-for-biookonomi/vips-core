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

package no.nibio.vips.core;

import java.util.Set;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;


/**
 * Responsible for adding REST resources
 * @copyright 2013-2020 {@link http://www.nibio.no NIBIO}
 * @author Tor-Einar Skog <tor-einar.skog@nibio.no>
 */
@ApplicationPath("/")
public class VIPSCoreApplication extends Application
{
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        //addRestResourceClasses(resources); //We don't use this!
        addRestResourceClassesManually(resources);
        return resources;
    }

    /**
     * Handmade because NetBeans doesn't discover the services that are implementations
     * of service interfaces. And it adds the interfaces it finds, which do not work
     * @param resources 
     */
    private void addRestResourceClassesManually(Set<Class<?>> resources) {
        resources.add(no.nibio.vips.core.config.JacksonConfig.class);
        resources.add(no.nibio.vips.core.service.ModelResourceImpl.class);
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(no.nibio.vips.core.config.JacksonConfig.class);
        resources.add(no.nibio.vips.core.service.ModelResourceImpl.class);
    }
}