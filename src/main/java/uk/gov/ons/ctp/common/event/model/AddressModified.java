package uk.gov.ons.ctp.common.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressModified {

  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String townName;
  private String postcode;
  private String region; // E, W or N
  private String uprn;
  private String arid;
}