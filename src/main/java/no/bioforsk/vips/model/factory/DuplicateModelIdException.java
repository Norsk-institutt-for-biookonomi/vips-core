/*
 * Copyright (c) 2014 Bioforsk <http://www.bioforsk.no/>. 
 * 
 * This file is part of VIPSCore.
 * VIPSCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIPSCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with VIPSCore.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package no.bioforsk.vips.model.factory;

/**
 * Thrown if ModelFactory discovers that the system contains different models
 * with identical modelIds
 * @copyright 2013 <a href="http://www.bioforsk.no/">Bioforsk</a>
 * @author Tor-Einar Skog <tor-einar.skog@bioforsk.no>
 */
public class DuplicateModelIdException extends Exception {

    /**
     * Creates a new instance of
     * <code>DuplicateModelIdException</code> without detail message.
     */
    public DuplicateModelIdException() {
    }

    /**
     * Constructs an instance of
     * <code>DuplicateModelIdException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DuplicateModelIdException(String msg) {
        super(msg);
    }
}
