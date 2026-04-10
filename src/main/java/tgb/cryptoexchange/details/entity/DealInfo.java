package tgb.cryptoexchange.details.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;
import tgb.cryptoexchange.details.exception.BaseException;

import java.time.Instant;

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

    @Column
    private Long detailsId;

    @Column(length = 512)
    private String botLink;

}
