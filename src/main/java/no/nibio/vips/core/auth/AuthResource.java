package no.nibio.vips.core.auth;

import java.util.Map;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(Map<String, String> credentials) {
        String clientId = credentials.get("clientId");
        String clientSecret = credentials.get("clientSecret");

        if (clientId == null || clientSecret == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"clientId and clientSecret are required\"}")
                    .build();
        }

        if (!ClientCredentialStore.validateCredentials(clientId, clientSecret)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid credentials\"}")
                    .build();
        }

        String token = JwtUtil.generateToken(clientId);
        return Response.ok()
                .entity("{\"token\":\"" + token + "\"}")
                .build();
    }
}
