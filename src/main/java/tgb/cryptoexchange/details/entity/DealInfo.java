package tgb.cryptoexchange.details.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deal_info")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
public class DealInfo extends BasePersist {

    @Column
    private Long dealId;

    @OneToOne
    @JoinColumn(name = "details_id")
    private Details details;

    @Column(length = 512)
    private String botLink;

}
