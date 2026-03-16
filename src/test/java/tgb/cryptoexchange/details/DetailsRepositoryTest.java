package tgb.cryptoexchange.details;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
        // Given
        Details details = new Details();
        details.setRequisite("Old Requisite");
        details =  detailsRepository.save(details);

        // When
        detailsRepository.updateRequisiteByPid("New Requisite", details.getPid());
        entityManager.clear(); // Очищаем кэш, чтобы подтянулись данные из БД

        // Then
        Optional<Details> updated = detailsRepository.findById(details.getPid());
        assertThat(updated).isPresent();
        assertThat(updated.get().getRequisite()).isEqualTo("New Requisite");
    }

    @Test
    @DisplayName("Получение реквизитов с непустой целевой суммой")
    void getWithNotEmptyTargetAmount() {
        // Given
        Details d1 = new Details();
        d1.setTargetAmount(1000);
        Details d2 = new Details();
        d2.setTargetAmount(0); // Должен игнорироваться
        Details d3 = new Details();
        d3.setTargetAmount(null); // Должен игнорироваться
        detailsRepository.saveAll(List.of(d1, d2, d3));

        // When
        List<Details> result = detailsRepository.getWithNotEmptyTargetAmount();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTargetAmount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Поиск старейшего доступного реквизита")
    void findOldestAvailableDetail() {
        // Given
        Details old = new Details();
        old.setTargetAmount(0);
        old.setLastAccessedAt(Instant.now().minusSeconds(100)); // Делаем его реально старым

        Details fresh = new Details();
        fresh.setTargetAmount(0);
        fresh.setLastAccessedAt(Instant.now());

        // Сохраняем и получаем объекты с актуальными ID
        List<Details> saved = detailsRepository.saveAllAndFlush(List.of(old, fresh));
        List<Long> pids = saved.stream().map(Details::getPid).toList();

        // When - передаем список динамических ID
        Optional<Details> result = detailsRepository.findOldestAvailableDetail(pids);

        // Then
        assertThat(result).isPresent();
        // Проверяем, что вернулся именно тот, у которого дата меньше (old)
        assertThat(result.get().getPid()).isEqualTo(saved.getFirst().getPid());
    }

    @Test
    @DisplayName("Поиск по списку PID с фильтром targetAmount")
    void findAllByPidInAndTargetAmountNotEmpty() {
        // Given
        Details d1 = new Details();
        d1.setTargetAmount(500);
        Details d2 = new Details();
        d2.setTargetAmount(0);
        List<Details> saved = detailsRepository.saveAllAndFlush(List.of(d1, d2));
        List<Long> pids = saved.stream().map(Details::getPid).toList();

        // When
        List<Details> result = detailsRepository.findAllByPidInAndTargetAmountNotEmpty(pids);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPid()).isEqualTo(pids.getFirst());
    }

}
