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
public class DealInfoDto implements Serializable {

    private Long pid;

    private Long dealId;

    private Long detailsId;

    private String botLink;

}