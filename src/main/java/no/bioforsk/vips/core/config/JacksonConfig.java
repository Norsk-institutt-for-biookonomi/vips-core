package no.bioforsk.vips.core.config;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * This config ensures that Jackson serializes dates as [ISO-8601 ]-compliant notation,
 * and not a timestamp
 * @copyright 2013 <a href="http://www.bioforsk.no/">Bioforsk</a>
 * @author Tor-Einar Skog <tor-einar.skog@bioforsk.no>
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonConfig  implements ContextResolver<ObjectMapper>{
    private final ObjectMapper objectMapper;

    public JacksonConfig()
    {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType)
    {
        return objectMapper;
    }
}
