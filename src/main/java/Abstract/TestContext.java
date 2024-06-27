package Abstract;

import okhttp3.Response;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class TestContext {
    private String endpoint;
    private String APIKey;
    private String JSONBody;
    private Response response;
    private JSONObject testData;
}
