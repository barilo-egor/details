package tgb.cryptoexchange.details.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DealInfoDto implements Serializable {

    private Long pid;

    private Long dealId;

    private Long detailsId;

    private String botLink;

}