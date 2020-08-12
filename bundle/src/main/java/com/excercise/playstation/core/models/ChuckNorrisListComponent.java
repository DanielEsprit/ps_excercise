package com.excercise.playstation.core.models;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.excercise.playstation.core.service.data.ChuckNorrisDataService;

/**
 * Chuck Norris List Component.
 */
@Model(adaptables = Resource.class)
public class ChuckNorrisListComponent {

   @OSGiService
   private ChuckNorrisDataService dataService;

   private List<JokeResource> jokes;

   /**
    * Init the sling model.
    */
   @PostConstruct
   public void init() {
      jokes = dataService.retrieveStoredRecords();
   }

   /**
    * @return a list of the JokeResources.
    */
   public List<JokeResource> getJokes() {
      return jokes;
   }

}
