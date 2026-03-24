package no.nibio.vips.core.auth;

import java.io.IOException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Provider
public class JwtAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!AuthConfig.getInstance().isJwtEnabled()) {
            return;
        }

        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (path.equals("auth") || path.startsWith("auth/") || path.equals("auth")) {
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Missing or invalid Authorization header\"}")
                            .build());
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            Claims claims = JwtUtil.validateToken(token);
            requestContext.setProperty("clientId", claims.getSubject());
        } catch (JwtException e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("{\"error\":\"Invalid or expired token\"}")
                            .build());
        }
    }
}
