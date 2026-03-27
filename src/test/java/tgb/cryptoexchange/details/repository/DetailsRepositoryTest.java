package tgb.cryptoexchange.details.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tgb.cryptoexchange.details.entity.Details;

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

    private Details detailWithTarget;

    private Details oldestDetail;

    @BeforeEach
    void setUp() {
        detailWithTarget = new Details();
        detailWithTarget.setRequisite("Target Req");
        detailWithTarget.setTargetAmount(10000);
        detailWithTarget.setOn(true);
        entityManager.persist(detailWithTarget);

        oldestDetail = new Details();
        oldestDetail.setRequisite("Oldest Req");
        oldestDetail.setLastAccessedAt(Instant.now().minusSeconds(3600));
        oldestDetail.setTargetAmount(0);
        oldestDetail.setOn(true);
        entityManager.persist(oldestDetail);

        Details freshDetail = new Details();
        freshDetail.setRequisite("Fresh Req");
        freshDetail.setLastAccessedAt(Instant.now());
        freshDetail.setTargetAmount(0);
        freshDetail.setOn(true);
        entityManager.persist(freshDetail);

        entityManager.flush();
    }

    @Test
    void updateRequisiteByPid_ShouldUpdateField() {
        String newReq = "New Requisite 123";
        detailsRepository.updateRequisiteByPid(newReq, detailWithTarget.getPid());

        entityManager.clear();

        Details updated = entityManager.find(Details.class, detailWithTarget.getPid());
        assertThat(updated.getRequisite()).isEqualTo(newReq);
    }

    @Test
    void getWithNotEmptyTargetAmount_ShouldReturnOnlyRelevant() {
        List<Details> result = detailsRepository.getWithNotEmptyTargetAmount();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRequisite()).isEqualTo("Target Req");
    }

    @Test
    void findAllByPidInAndTargetAmountNotEmpty_ShouldReturnLockedEntities() {
        List<Long> pids = List.of(detailWithTarget.getPid(), oldestDetail.getPid());

        List<Details> result = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(pids, true);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPid()).isEqualTo(detailWithTarget.getPid());
    }

    @Test
    void findOldestAvailableDetail_ShouldReturnOldestByAccessTime() {
        List<Long> ids = List.of(oldestDetail.getPid(), 3L);

        Optional<Details> result = detailsRepository.findOldestAvailableDetail(ids, true);

        assertThat(result).isPresent();
        assertThat(result.get().getRequisite()).isEqualTo("Oldest Req");
    }

    @Test
    void findById_ShouldReturnEntityWithLock() {
        Optional<Details> result = detailsRepository.findById(detailWithTarget.getPid());

        assertThat(result).isPresent();
        assertThat(result.get().getRequisite()).isEqualTo("Target Req");
    }
}

