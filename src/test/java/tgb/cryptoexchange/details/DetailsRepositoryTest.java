package tgb.cryptoexchange.details;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.repository.DetailsRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DetailsRepositoryTest {

    @Autowired
    private DetailsRepository detailsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Обновление реквизита по PID")
    void updateRequisiteByPid() {
        Details details = new Details();
        details.setRequisite("Old Requisite");
        details = detailsRepository.save(details);

        detailsRepository.updateRequisiteByPid("New Requisite", details.getPid());
        entityManager.clear();

        Optional<Details> updated = detailsRepository.findById(details.getPid());
        assertThat(updated).isPresent();
        assertThat(updated.get().getRequisite()).isEqualTo("New Requisite");
    }

    @Test
    @DisplayName("Получение реквизитов с непустой целевой суммой")
    void getWithNotEmptyTargetAmount() {
        Details d1 = new Details();
        d1.setTargetAmount(1000);
        Details d2 = new Details();
        d2.setTargetAmount(0);
        Details d3 = new Details();
        d3.setTargetAmount(null);
        detailsRepository.saveAll(List.of(d1, d2, d3));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Details> result = detailsRepository.getWithNotEmptyTargetAmount(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().getTargetAmount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Поиск старейшего доступного реквизита")
    void findOldestAvailableDetail() {
        Details old = new Details();
        old.setTargetAmount(0);
        old.setIsOn(true);
        old.setLastAccessedAt(Instant.now().minusSeconds(100));

        Details fresh = new Details();
        fresh.setTargetAmount(0);
        fresh.setLastAccessedAt(Instant.now());

        List<Details> saved = detailsRepository.saveAllAndFlush(List.of(old, fresh));
        List<Long> pids = saved.stream().map(Details::getPid).toList();

        Optional<Details> result = detailsRepository.findOldestAvailableDetail(pids, true);

        assertThat(result).isPresent();
        assertThat(result.get().getPid()).isEqualTo(saved.getFirst().getPid());
    }

    @Test
    @DisplayName("Поиск по списку PID с фильтром targetAmount")
    void findAllByPidInAndTargetAmountNotEmpty() {
        Details d1 = new Details();
        d1.setTargetAmount(500);
        d1.setIsOn(true);
        Details d2 = new Details();
        d2.setTargetAmount(0);
        List<Details> saved = detailsRepository.saveAllAndFlush(List.of(d1, d2));
        List<Long> pids = saved.stream().map(Details::getPid).toList();

        List<Details> result = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(pids, true);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPid()).isEqualTo(pids.getFirst());
    }

}
