package tgb.cryptoexchange.details.entity;

import jakarta.persistence.*;
import lombok.*;
import tgb.cryptoexchange.details.exception.BaseException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Details")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
public class Details extends BasePersist {

    @Column(name = "NAME")
    private String name;

    private String shortName;

    @Column(name = "REQUISITE")
    private String requisite;

    @OneToMany(mappedBy = "details", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DealInfo> dealInfos = new ArrayList<>();

    @Column(name = "IS_ON")
    private Boolean isOn;

    private Integer targetAmount;

    private Integer reserveAmount;

    private Integer receivedAmount;

    private Integer rangeFrom;

    private Integer rangeTo;

    private Instant lastAccessedAt;

    @PrePersist
    protected void onCreate() {
        if (this.lastAccessedAt == null) {
            this.lastAccessedAt = Instant.now();
        }
    }

    public Details(Long pid) {
        super(pid);
    }

    public void setReserveAmount(Integer reserveAmount) {
        if (reserveAmount < 0) {
            reserveAmount = 0;
        }
        if (reserveAmount > targetAmount) {
            throw new BaseException("reserveAmount must be less than targetAmount");
        }
        this.reserveAmount = reserveAmount;
    }

    public boolean hasRange() {
        return (rangeTo != null && rangeTo != 0);
    }

    public boolean isInRange(Integer amount) {
        if (!hasRange()) {
            return true;
        }
        return amount >= rangeFrom && amount <= rangeTo;
    }

}
