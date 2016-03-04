package titan.client.messages;

public class JwtAuth {
    public final String token;

    public JwtAuth(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }

        this.token = token;
    }
}
