package tgb.cryptoexchange.details.interfaces.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaymentTypeDto implements Serializable {

    private Long pid;

    private String name;

    @Builder.Default
    private List<Long> details = new ArrayList<>();


}
