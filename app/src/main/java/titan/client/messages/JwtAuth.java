package titan.client.messages;

public class JwtAuth {
    private final String token;

    public JwtAuth(String token) {
        this.token = token;
    }
}
