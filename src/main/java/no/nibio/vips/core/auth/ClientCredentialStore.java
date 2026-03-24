package no.nibio.vips.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.yaml.snakeyaml.Yaml;

public class ClientCredentialStore {

    private static final Logger LOGGER = Logger.getLogger(ClientCredentialStore.class.getName());

    private ClientCredentialStore() {}

    /**
     * Reads clients.yml from disk and returns a map of clientId to secretHash.
     * Called on every auth request so new clients are picked up without a restart.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> loadClients() {
        Map<String, String> clientSecrets = new HashMap<>();
        Yaml yaml = new Yaml();
        try (InputStream is = AuthConfig.openConfigFile("clients.yml")) {
            Map<String, Object> root = yaml.load(is);
            List<Map<String, String>> clients = (List<Map<String, String>>) root.get("clients");
            if (clients != null) {
                for (Map<String, String> client : clients) {
                    String clientId = client.get("clientId");
                    String secretHash = client.get("secretHash");
                    if (clientId != null && secretHash != null) {
                        clientSecrets.put(clientId, secretHash);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load clients.yml", e);
        }
        return clientSecrets;
    }

    public static boolean validateCredentials(String clientId, String rawSecret) {
        Map<String, String> clientSecrets = loadClients();
        String storedHash = clientSecrets.get(clientId);
        if (storedHash == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(rawSecret.toCharArray(), storedHash);
        return result.verified;
    }
}
