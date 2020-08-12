package com.excercise.playstation.core.models;

import static org.apache.sling.models.annotations.DefaultInjectionStrategy.OPTIONAL;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, defaultInjectionStrategy = OPTIONAL)
public class JokeResource {

    /**
     * Defining the property names for how the Joke Resource data will be stored within the JCR.
     */
    public static final String CATEGORY_PROPERTY = "category";

    public static final String ICON_PROPERTY = "iconUrl";

    public static final String ID_PROPERTY = "id";

    public static final String URL_PROPERTY = "url";

    public static final String VALUE_PROPERTY = "value";

    public static final String CREATION_PROPERTY = "creationTime";

    @Self
    private Resource sourceResource;

    @Inject
    private String iconUrl;

    @Inject
    private String id;

    @Inject
    private String url;

    @Inject
    private String value;

    @Inject
    private List<String> categories;

    @Inject
    private Date creationTime;

    public Resource getSourceResource() {
        return sourceResource;
    }

    public List<String> getCategories() {
        return categories;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getValue() {
        return value;
    }

    public Date getCreationTime() {
        return creationTime;
    }

}
