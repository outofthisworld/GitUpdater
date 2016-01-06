package git.sync.listener;

import com.google.gson.*;

/**
 * Created by Unknown on 6/01/2016.
 */
public class GitParseListener implements IGitParseListener {
    private JsonObject jsonObject = null;
    private static final JsonParser jsonParser = new JsonParser();

    @Override
    public String getLatestRevision() {
        if(jsonObject == null)
            return null;

        return jsonObject.get("sha").toString();
    }

    @Override
    public void parseResponse(String returnedString) {
        jsonObject = jsonParser.parse(returnedString).getAsJsonArray().get(0).getAsJsonObject();
    }
}
