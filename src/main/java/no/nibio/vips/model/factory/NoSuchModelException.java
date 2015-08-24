/*
 * Copyright (c) 2015 NIBIO <http://www.nibio.no/>. 
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

/**
 * @copyright 2015 <a href="http://www.bioforsk.no/">Bioforsk</a>
 * @author Tor-Einar Skog <tor-einar.skog@bioforsk.no>
 */
public class NoSuchModelException extends Exception {

    /**
     * Creates a new instance of <code>NoSuchModelException</code> without detail message.
     */
    public NoSuchModelException() {
    }


    /**
     * Constructs an instance of <code>NoSuchModelException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public NoSuchModelException(String msg) {
        super(msg);
    }
}
