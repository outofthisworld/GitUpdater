package git.sync.listener;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Unknown on 6/01/2016.
 */
public class GitParseListener implements IGitParseListener {
    private static final JsonParser jsonParser = new JsonParser();
    private JsonObject jsonObject = null;

    @Override
    public String getLatestRevision() {
        if (jsonObject == null)
            return null;

        return jsonObject.get("sha").toString().replaceAll("\"", "");
    }

    @Override
    public void parseResponse(String returnedString) {
        jsonObject = jsonParser.parse(returnedString).getAsJsonArray().get(0).getAsJsonObject();
    }
}
