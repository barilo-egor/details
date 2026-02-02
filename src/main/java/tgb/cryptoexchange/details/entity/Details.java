package tgb.cryptoexchange.details.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import tgb.cryptoexchange.details.exception.BaseException;

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

    @Column(name = "REQUISITE")
    private String requisite;

    @Column(name = "IS_ON")
    private Boolean isOn;

    private Integer targetAmount;

    private Integer reserveAmount;

    private Integer receivedAmount;

    private Integer rangeFrom;

    private Integer rangeTo;

    //Вообще не используется нигде
    //    @Builder.Default
    //    @OneToMany
    //    private List<Deal> deals = new ArrayList<>();

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
