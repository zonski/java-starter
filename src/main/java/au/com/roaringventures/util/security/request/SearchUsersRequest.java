package au.com.roaringventures.util.security.request;


import au.com.roaringventures.util.request.PagedRequest;

public class SearchUsersRequest extends PagedRequest {

    private String term;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
