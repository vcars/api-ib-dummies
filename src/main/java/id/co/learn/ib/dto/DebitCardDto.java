package id.co.learn.ib.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebitCardDto implements Serializable {

    private String cardNo;
    private String cardHolder;
    private String expiry;
    private String cardType;
    private BigDecimal balance;

}
