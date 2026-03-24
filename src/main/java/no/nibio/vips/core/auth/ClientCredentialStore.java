package no.nibio.vips.core.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.yaml.snakeyaml.Yaml;

public class ClientCredentialStore {

    private static final Logger LOGGER = Logger.getLogger(ClientCredentialStore.class.getName());

    private static ClientCredentialStore instance;

    private final Map<String, String> clientSecrets = new HashMap<>();

    private ClientCredentialStore() {}

    public static synchronized ClientCredentialStore getInstance() {
        if (instance == null) {
            instance = new ClientCredentialStore();
            instance.load();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    private void load() {
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
            LOGGER.info("Loaded " + clientSecrets.size() + " client credential(s).");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load clients.yml", e);
        }
    }

    public boolean validateCredentials(String clientId, String rawSecret) {
        String storedHash = clientSecrets.get(clientId);
        if (storedHash == null) {
            return false;
        }
        BCrypt.Result result = BCrypt.verifyer().verify(rawSecret.toCharArray(), storedHash);
        return result.verified;
    }

    /**
     * Utility for generating BCrypt hashes when onboarding new clients.
     * Run from a unit test or CLI: ClientCredentialStore.hashSecret("my-secret")
     */
    public static String  hashSecret(String rawSecret) {
        return BCrypt.withDefaults().hashToString(12, rawSecret.toCharArray());
    }
}
