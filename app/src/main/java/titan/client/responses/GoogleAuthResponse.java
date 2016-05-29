package titan.client.responses;

public class GoogleAuthResponse {
    public final String token;
    public final String id;
    public final String name;
    public final String email;
    public final byte[] picture;

    public GoogleAuthResponse(String token,String id, String name, String email, byte[] picture) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("token cannot be null or empty");
        }
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
        if (picture == null || picture.length == 0) {
            throw new IllegalArgumentException("picture cannot be null or empty");
        }

        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }
}
