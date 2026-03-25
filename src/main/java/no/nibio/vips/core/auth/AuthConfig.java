package no.nibio.vips.core.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.yaml.snakeyaml.Yaml;

public class AuthConfig {

    private static final Logger LOGGER = Logger.getLogger(AuthConfig.class.getName());
    private static final String CONFIG_FILENAME = "auth-config.yml";

    private static AuthConfig instance;

    private boolean jwtEnabled;
    private SecretKey signingKey;
    private String issuer;
    private int expirationMinutes;

    private AuthConfig() {}

    public static synchronized AuthConfig getInstance() {
        if (instance == null) {
            instance = new AuthConfig();
            instance.load();
        }
        return instance;
    }

    /**
     * Resolves the config directory. Priority:
     * 1. VIPSCORE_CONFIG_DIR environment variable
     * 2. jboss.server.config.dir system property + /vipscore
     */
    static Path getConfigDir() {
        String envDir = System.getenv("VIPSCORE_CONFIG_DIR");
        if (envDir != null && !envDir.isBlank()) {
            return Paths.get(envDir);
        }
        String jbossConfigDir = System.getProperty("jboss.server.config.dir");
        if (jbossConfigDir != null) {
            return Paths.get(jbossConfigDir, "vipscore");
        }
        throw new IllegalStateException(
                "Auth config directory not resolved. Set VIPSCORE_CONFIG_DIR environment variable "
                + "or ensure jboss.server.config.dir system property is available.");
    }

    @SuppressWarnings("unchecked")
    private void load() {
        Yaml yaml = new Yaml();
        try (InputStream is = openConfigFile(CONFIG_FILENAME)) {
            Map<String, Object> root = yaml.load(is);
            Map<String, Object> jwt = (Map<String, Object>) root.get("jwt");

            this.jwtEnabled = Boolean.TRUE.equals(jwt.get("enabled"));

            String secret = System.getenv("JWT_SECRET");
            if (secret == null || secret.isBlank()) {
                secret = (String) jwt.get("secret");
            }
            this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

            this.issuer = (String) jwt.get("issuer");
            this.expirationMinutes = (int) jwt.get("expirationMinutes");

            LOGGER.info("Loaded " + CONFIG_FILENAME + " (jwt.enabled=" + this.jwtEnabled + ")");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILENAME, e);
        }
    }

    /**
     * Opens a config file from the external config directory.
     * Throws if the directory cannot be resolved or the file does not exist.
     */
    static InputStream openConfigFile(String filename) throws IOException {
        Path configDir = getConfigDir();
        Path filePath = configDir.resolve(filename);
        if (!Files.exists(filePath)) {
            throw new IOException(filename + " not found at " + filePath
                    + ". Ensure the file exists in the config directory.");
        }
        LOGGER.info("Loading " + filename + " from " + filePath);
        return new FileInputStream(filePath.toFile());
    }

    public boolean isJwtEnabled() {
        return jwtEnabled;
    }

    public SecretKey getSigningKey() {
        return signingKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public int getExpirationMinutes() {
        return expirationMinutes;
    }
}
