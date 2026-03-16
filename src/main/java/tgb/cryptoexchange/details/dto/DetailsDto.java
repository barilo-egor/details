package tgb.cryptoexchange.details.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailsDto implements Serializable {

    private Long pid;

    private String name;

    private String requisite;

    private Boolean isOn;

    private Integer targetAmount;

    private Integer reserveAmount;

    private Integer receivedAmount;

    private Integer rangeFrom;

    private Integer rangeTo;
}