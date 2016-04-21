package titan.client.messages;

public class TokenAuth {
    public final String token;

    public TokenAuth(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }

        this.token = token;
    }
}
