package titan.client.messages;

public class TokenMessage {
    public final String token;

    public TokenMessage(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }

        this.token = token;
    }
}
