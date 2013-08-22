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
