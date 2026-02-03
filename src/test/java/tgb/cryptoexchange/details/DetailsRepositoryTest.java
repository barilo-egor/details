package tgb.cryptoexchange.details;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.repository.DetailsRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DetailsRepositoryTest {

    @Autowired
    private DetailsRepository detailsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void updateRequisiteByPid() {
        Details details = Details.builder()
                .requisite("old_requisite")
                .build();
        details = detailsRepository.save(details);

        detailsRepository.updateRequisiteByPid("new_requisite", details.getPid());
        entityManager.clear();

        Details updated = detailsRepository.findById(details.getPid()).get();
        assertEquals("new_requisite", updated.getRequisite());
    }

    @Test
    void getWithNotEmptyTargetAmount() {
        detailsRepository.save(Details.builder().targetAmount(100).build());
        detailsRepository.save(Details.builder().targetAmount(0).build());
        detailsRepository.save(Details.builder().targetAmount(null).build());

        List<Details> result = detailsRepository.getWithNotEmptyTargetAmount();

        assertEquals(1, result.size());
        assertEquals(100, result.getFirst().getTargetAmount());
    }

    @Test
    void findAllByPidInAndTargetAmountNotEmpty() {
        Details d1 = detailsRepository.save(Details.builder().targetAmount(500).build());
        Details d2 = detailsRepository.save(Details.builder().targetAmount(null).build());
        Details d3 = detailsRepository.save(Details.builder().targetAmount(200).build());

        List<Long> pids = List.of(d1.getPid(), d2.getPid(), d3.getPid());

        List<Details> result = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(pids);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getTargetAmount().equals(500)));
        assertTrue(result.stream().anyMatch(d -> d.getTargetAmount().equals(200)));
    }

    @Test
    void findAllByPidIn() {
        Details d1 = detailsRepository.save(Details.builder().requisite("req1").build());
        Details d2 = detailsRepository.save(Details.builder().requisite("req2").build());

        List<Details> result = detailsRepository.findAllByPidIn(List.of(d1.getPid(), d2.getPid()));

        assertEquals(2, result.size());
    }
}
