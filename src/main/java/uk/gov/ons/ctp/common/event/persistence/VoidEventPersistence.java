package uk.gov.ons.ctp.common.event.persistence;

import uk.gov.ons.ctp.common.event.EventPublishException;
import uk.gov.ons.ctp.common.event.EventPublisher.EventType;
import uk.gov.ons.ctp.common.event.EventPublisher.RoutingKey;
import uk.gov.ons.ctp.common.event.model.GenericEvent;

/**
 * This event persister is for applications which don't want the ability to persist event data in
 * the event of a Rabbit failure.
 */
public class VoidEventPersistence implements EventPersistence {

  @Override
  public boolean firestorePersistenceSupported() {
    return false;
  }

  @Override
  public void persistEvent(EventType eventType, RoutingKey routingKey, GenericEvent genericEvent) {
    throw new EventPublishException("Application not configured to persist events to Firestore");
  }
}