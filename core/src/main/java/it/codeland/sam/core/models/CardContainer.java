package it.codeland.sam.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

@Model(adaptables = { SlingHttpServletRequest.class,
        Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardContainer {
    private final Logger LOG = LoggerFactory.getLogger(CardContainer.class);

    @SlingObject
    private Resource currentResource;

    @Inject
    private String cardsNumber;

    @PostConstruct
    public void init() {
        LOG.debug("VVVVVVVVVVVVVVVVVVV {}", cardsNumber);
    }

   // public String[] getCardsNumber() {

      //  if (cardsNumber != null) {
         //   try {

          //      int cardnum = new int[Integer.parseInt(cardsNumber)];
           //     String[] cards = new String[cardnum];
             //   for (int i = 0; i < cardnum; i++) {
          //          cards[i] = "card" + Integer.toString(i);
            //    }
            //    return cards;
        //    } catch (NumberFormatException e) {
          //      LOG.error("Failed to parse cardsNumber: {}", cardsNumber, e);
         //   }
      //  }
      //  return new String[] { "cardt423" }; // or handle the error in an appropriate way
//
   // }

    public int[] getCardsNumber() {
        LOG.debug("VVVVVVVVVVVVVVVVVVV {}", cardsNumber);
        int arr[] = new int[Integer.parseInt(cardsNumber)];
        for (int i = 0; i < Integer.parseInt(cardsNumber); i++) {
            arr[i] = i + 1;
        }
        return arr;
    }

}
