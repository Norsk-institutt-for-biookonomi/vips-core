package no.nibio.vips.core.web;

import jakarta.inject.Singleton;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;

@Singleton
@Path("/swagger")
public class SwaggerUiResource {

    @Context
    ServletContext servletContext;

    @GET
    public Response redirectToIndex() {
        return Response.seeOther(URI.create("swagger/index.html")).build();
    }

    @GET
    @Path("/index.html")
    @Produces(MediaType.TEXT_HTML)
    public Response getIndexHtml() {
        InputStream is = servletContext.getResourceAsStream("/swagger/index.html");
        if (is == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Swagger UI not found").build();
        }
        return Response.ok(is, MediaType.TEXT_HTML_TYPE).build();
    }
}
