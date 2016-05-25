package titan.client.responses;

public class GoogleAuthResponse {
    public final String ID;
    public final String JWTToken;
    public final String Name;
    public final String Email;
    public final byte[] Picture;

    public GoogleAuthResponse(String id, String jwtToken, String name, String email, byte[] picture) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalArgumentException("jwtToken cannot be null or empty");
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

        ID = id;
        JWTToken = jwtToken;
        Name = name;
        Email = email;
        Picture = picture;
    }
}
