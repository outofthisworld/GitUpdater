package git.sync.git;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Unknown on 5/01/2016.
 */
public class GitUpdateDetails {
    private final String repo;
    private final String gitUser;
    private final HashMap<String,String> params = new HashMap<>();
    private static final String ISO_8601_DATE_FMT = "yyyy-MM-dd'T'HH:mmX";

    public GitUpdateDetails(String repo,String gitUser){
        this.repo = repo;
        this.gitUser = gitUser;
    }

    public GitUpdateDetails(String repo,String gitUser,String shaOrBranch){
        this.repo = repo;
        this.gitUser = gitUser;
        this.params.put("sha", shaOrBranch);
    }

    public GitUpdateDetails(String repo,String gitUser, String shaOrBranch, Date since){
        this(repo,gitUser);
        this.params.put("sha",shaOrBranch);
        this.params.put("since", DateTimeFormatter.ofPattern(ISO_8601_DATE_FMT)
                .withZone(ZoneOffset.UTC)
                .format(since.toInstant()));
    }

    public GitUpdateDetails(String repo,String gitUser, String shaOrBranch,String authorFilter){
        this(repo,gitUser,shaOrBranch);
        params.put("author", authorFilter);
    }

    public GitUpdateDetails(String repo,String gitUser, String shaOrBranch, Date since,String authorFilter){
        this(repo,gitUser,shaOrBranch,since);
        params.put("author", authorFilter);
    }

    public String removeParam(Object key){
        return params.remove(key.toString());
    }

    public void addParam(Object key,Object value){
        params.put(key.toString(),value.toString());
    }

    public final String getRepo(){
        return repo;
    }

    public final String getGitUser(){
        return gitUser;
    }

    public final String createGetQueryString(){
        StringBuilder queryStringBuilder = new StringBuilder();

        for(Iterator<Map.Entry<String,String>> it = params.entrySet().iterator(); it.hasNext();){
            Map.Entry<String,String> paramEntry = it.next();
            queryStringBuilder.append(paramEntry.getKey());
            queryStringBuilder.append("=");
            queryStringBuilder.append(paramEntry.getValue());
            if(it.hasNext())
                queryStringBuilder.append("&");
        }
        return queryStringBuilder.toString();
    }

    public GitDetailsBuilder<GitUpdateDetails> getGitUpdateDetailsBuilder(){
        return new GitDetailsBuilder<>(this);
    }

    public class GitDetailsBuilder<T extends GitUpdateDetails>{
        private final T gitUpdateDetails;

        public GitDetailsBuilder(T gitUpdateDetails){
            this.gitUpdateDetails = gitUpdateDetails;
        }

        public GitDetailsBuilder<T> addDetails(Object key,Object value){
            gitUpdateDetails.addParam(key,value);
            return this;
        }

        public T getGitUpdateDetails(){
            return gitUpdateDetails;
        }
    }
}
