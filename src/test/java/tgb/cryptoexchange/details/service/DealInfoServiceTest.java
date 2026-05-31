package tgb.cryptoexchange.details.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.entity.DealInfo;
import tgb.cryptoexchange.details.interfaces.dto.DealInfoMapper;
import tgb.cryptoexchange.details.repository.DealInfoRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealInfoServiceTest {

    @Mock
    private DealInfoRepository dealInfoRepository;

    @Mock
    private DealInfoMapper mapper;

    @InjectMocks
    private DealInfoService dealInfoService;

    @Test
    @DisplayName("Должен успешно смапить DTO и сохранить сущность")
    void shouldMapToEntityAndSaveSuccessfully() {
        DealInfoDto inputDto = new DealInfoDto();
        DealInfo mappedEntity = new DealInfo();
        DealInfo savedEntity = new DealInfo();

        when(mapper.toEntity(inputDto)).thenReturn(mappedEntity);
        when(dealInfoRepository.save(mappedEntity)).thenReturn(savedEntity);

        DealInfo result = dealInfoService.save(inputDto);

        assertThat(result).isNotNull().isEqualTo(savedEntity);
        verify(mapper, times(1)).toEntity(inputDto);
        verify(dealInfoRepository, times(1)).save(mappedEntity);
    }

    @Test
    @DisplayName("Должен корректно настроить ExampleMatcher и вернуть страницу данных")
    @SuppressWarnings("unchecked")
    void shouldFindAllUsingExampleMatcher() {
        DealInfoDto filterDto = new DealInfoDto();
        DealInfo probeEntity = new DealInfo();
        Pageable pageable = PageRequest.of(0, 20);

        List<DealInfo> content = List.of(new DealInfo());
        Page<DealInfo> expectedPage = new PageImpl<>(content, pageable, 1L);

        when(mapper.toEntity(filterDto)).thenReturn(probeEntity);
        when(dealInfoRepository.findAll(any(Example.class), eq(pageable))).thenReturn(expectedPage);

        Page<DealInfo> resultPage = dealInfoService.findAll(filterDto, pageable);

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1L);
        assertThat(resultPage.getContent()).hasSize(1);

        ArgumentCaptor<Example<DealInfo>> exampleCaptor = ArgumentCaptor.forClass(Example.class);
        verify(dealInfoRepository).findAll(exampleCaptor.capture(), eq(pageable));

        Example<DealInfo> capturedExample = exampleCaptor.getValue();
        assertThat(capturedExample.getProbe()).isEqualTo(probeEntity);

        ExampleMatcher matcher = capturedExample.getMatcher();
        assertThat(matcher.getMatchMode()).isEqualTo(ExampleMatcher.MatchMode.ALL);
        assertThat(matcher.getNullHandler()).isEqualTo(ExampleMatcher.NullHandler.IGNORE);

        verify(mapper, times(1)).toEntity(filterDto);
    }

    @Test
    @DisplayName("Должен возвращать корректный инстанс репозитория")
    void shouldReturnCorrectBaseRepository() {
        var repository = dealInfoService.getBaseRepository();

        assertThat(repository).isNotNull().isEqualTo(dealInfoRepository);
    }

}