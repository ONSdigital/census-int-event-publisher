package uk.gov.ons.ctp.common.event;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Date;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import uk.gov.ons.ctp.common.event.EventPublisher.Channel;
import uk.gov.ons.ctp.common.event.EventPublisher.EventType;
import uk.gov.ons.ctp.common.event.EventPublisher.RoutingKey;
import uk.gov.ons.ctp.common.event.EventPublisher.Source;
import uk.gov.ons.ctp.common.event.model.CommonEvent;
import uk.gov.ons.ctp.common.event.model.EventPayload;
import uk.gov.ons.ctp.common.event.model.FulfilmentRequest;
import uk.gov.ons.ctp.common.event.model.RespondentAuthenticatedResponse;
import uk.gov.ons.ctp.common.event.model.RespondentRefusalDetails;
import uk.gov.ons.ctp.common.event.model.Response;

@RunWith(MockitoJUnitRunner.class)
public class EventPublisherTest {

  private static final UUID CASE_ID = UUID.fromString("dc4477d1-dd3f-4c69-b181-7ff725dc9fa4");
  private static final String QUESTIONNAIRE_ID = "1110000009";

  @InjectMocks private EventPublisher eventPublisher;
  @Mock private RabbitTemplate template;
  @Mock private SpringRabbitEventSender sender;

  /** Test event message with SurveyLaunchedResponse payload */
  @Test
  public void sendEventSurveyLaunchedPayload() throws Exception {

    Response surveyLaunchedResponse =
        Response.builder().questionnaireId(QUESTIONNAIRE_ID).caseId(CASE_ID).build();

    ArgumentCaptor<CommonEvent> eventCapture =
        ArgumentCaptor.forClass(CommonEvent.class);

    String transactionId =
        eventPublisher.sendEvent(
            EventType.SURVEY_LAUNCHED, Source.RESPONDENT_HOME, Channel.RH, surveyLaunchedResponse);

    RoutingKey routingKey = RoutingKey.forType(EventType.RESPONDENT_AUTHENTICATED);
    verify(sender, times(1)).sendEvent(eq(routingKey), eventCapture.capture());
    CommonEvent event = eventCapture.getValue();

    assertEquals(event.getEvent().getTransactionId(), transactionId);
    assertThat(UUID.fromString(event.getEvent().getTransactionId()), instanceOf(UUID.class));
    assertEquals(EventPublisher.EventType.SURVEY_LAUNCHED, event.getEvent().getType());
    assertEquals(EventPublisher.Source.RESPONDENT_HOME, event.getEvent().getSource());
    assertEquals(EventPublisher.Channel.RH, event.getEvent().getChannel());
    assertThat(event.getEvent().getDateTime(), instanceOf(Date.class));
    assertEquals(CASE_ID, event.getPayload().getResponse().getCaseId());
    assertEquals(QUESTIONNAIRE_ID, event.getPayload().getResponse().getQuestionnaireId());
  }

  /** Test event message with RespondentAuthenticatedResponse payload */
  @Test
  public void sendEventRespondentAuthenticatedPayload() throws Exception {

    RespondentAuthenticatedResponse respondentAuthenticatedResponse =
        RespondentAuthenticatedResponse.builder()
            .questionnaireId(QUESTIONNAIRE_ID)
            .caseId(CASE_ID)
            .build();

    ArgumentCaptor<CommonEvent> eventCapture =
        ArgumentCaptor.forClass(CommonEvent.class);

    String transactionId =
        eventPublisher.sendEvent(
            EventType.RESPONDENT_AUTHENTICATED,
            Source.RESPONDENT_HOME,
            Channel.RH,
            respondentAuthenticatedResponse);

    RoutingKey routingKey = RoutingKey.forType(EventType.RESPONDENT_AUTHENTICATED);
    verify(sender, times(1)).sendEvent(eq(routingKey), eventCapture.capture());
    CommonEvent event = eventCapture.getValue();

    assertEquals(event.getEvent().getTransactionId(), transactionId);
    assertThat(UUID.fromString(event.getEvent().getTransactionId()), instanceOf(UUID.class));
    assertEquals(EventPublisher.EventType.RESPONDENT_AUTHENTICATED, event.getEvent().getType());
    assertEquals(EventPublisher.Source.RESPONDENT_HOME, event.getEvent().getSource());
    assertEquals(EventPublisher.Channel.RH, event.getEvent().getChannel());
    assertThat(event.getEvent().getDateTime(), instanceOf(Date.class));
    assertEquals(CASE_ID, event.getPayload().getRespondentAuthenticatedResponse().getCaseId());
    assertEquals(QUESTIONNAIRE_ID, event.getPayload().getRespondentAuthenticatedResponse().getQuestionnaireId());
  }

  /** Test event message with FulfilmentRequest payload */
  @Test
  public void sendEventFulfilmentRequestPayload() throws Exception {

    // Build fulfilment
    FulfilmentRequest fulfilmentRequest = new FulfilmentRequest();
    fulfilmentRequest.setCaseId("id-123");

    ArgumentCaptor<CommonEvent> eventCapture =
        ArgumentCaptor.forClass(CommonEvent.class);

    String transactionId =
        eventPublisher.sendEvent(
            EventType.FULFILMENT_REQUESTED,
            Source.CONTACT_CENTRE_API,
            Channel.CC,
            fulfilmentRequest);

    RoutingKey routingKey = RoutingKey.forType(EventType.FULFILMENT_REQUESTED);
    verify(sender, times(1)).sendEvent(eq(routingKey), eventCapture.capture());
    CommonEvent event = eventCapture.getValue();

    assertEquals(event.getEvent().getTransactionId(), transactionId);
    assertThat(UUID.fromString(event.getEvent().getTransactionId()), instanceOf(UUID.class));
    assertEquals(EventPublisher.EventType.FULFILMENT_REQUESTED, event.getEvent().getType());
    assertEquals(EventPublisher.Source.CONTACT_CENTRE_API, event.getEvent().getSource());
    assertEquals(EventPublisher.Channel.CC, event.getEvent().getChannel());
    assertNotNull(event.getEvent().getDateTime());
    assertEquals("id-123", event.getPayload().getFulfilmentRequest().getCaseId());
  }

  /** Test event message with RespondentRefusalDetails payload */
  @Test
  public void sendEventRespondentRefusalDetailsPayload() throws Exception {

    // Build fulfilment
    RespondentRefusalDetails respondentRefusalDetails = new RespondentRefusalDetails();
    respondentRefusalDetails.setAgentId("x1");

    ArgumentCaptor<CommonEvent> eventCapture =
        ArgumentCaptor.forClass(CommonEvent.class);

    String transactionId =
        eventPublisher.sendEvent(
            EventType.REFUSAL_RECEIVED,
            Source.CONTACT_CENTRE_API,
            Channel.CC,
            respondentRefusalDetails);

    RoutingKey routingKey = RoutingKey.forType(EventType.REFUSAL_RECEIVED);
    verify(sender, times(1)).sendEvent(eq(routingKey), eventCapture.capture());
    CommonEvent event = eventCapture.getValue();

    assertEquals(event.getEvent().getTransactionId(), transactionId);
    assertThat(UUID.fromString(event.getEvent().getTransactionId()), instanceOf(UUID.class));
    assertEquals(EventPublisher.EventType.REFUSAL_RECEIVED, event.getEvent().getType());
    assertEquals(EventPublisher.Source.CONTACT_CENTRE_API, event.getEvent().getSource());
    assertEquals(EventPublisher.Channel.CC, event.getEvent().getChannel());
    assertNotNull(event.getEvent().getDateTime());
    assertEquals(
        respondentRefusalDetails.getAgentId(), event.getPayload().getRefusal().getAgentId());
  }

  /** Test build of Respondent Authenticated event message with wrong pay load */
  @Test
  public void sendEventRespondentAuthenticatedWrongPayload() {

    boolean exceptionThrown = false;

    try {
      eventPublisher.sendEvent(
          EventType.ADDRESS_MODIFIED,
          Source.RECEIPT_SERVICE,
          Channel.CC,
          Mockito.mock(EventPayload.class));
    } catch (Exception e) {
      exceptionThrown = true;
      assertThat(e.getMessage(), containsString("incompatible for event type"));
    }

    assertTrue(exceptionThrown);
  }
}