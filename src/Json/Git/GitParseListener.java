package Json.Git;

/**
 * Created by Unknown on 6/01/2016.
 */
public class GitParseListener implements IGitParseListener {

    @Override
    public String getLatestRevision() {
        return "111";
    }

    @Override
    public void parseResponse(String returnedString) {
        //parse string into json obj, make getLatest revision return the SHA of the altest commit
    }
}
