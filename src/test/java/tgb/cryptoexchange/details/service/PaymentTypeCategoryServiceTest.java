package tgb.cryptoexchange.details.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.details.dto.PaymentTypeCategoryDto;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeCategoryMapper;
import tgb.cryptoexchange.details.repository.PaymentTypeCategoryRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTypeCategoryServiceTest {

    @Mock
    private PaymentTypeCategoryRepository paymentTypeCategoryRepository;

    @Mock
    private PaymentTypeCategoryMapper mapper;

    @InjectMocks
    private PaymentTypeCategoryService paymentTypeCategoryService;

    @Test
    @DisplayName("findAll() должен возвращать список всех категорий")
    void findAll_ShouldReturnListOfCategories() {

        PaymentTypeCategory category = new PaymentTypeCategory();
        List<PaymentTypeCategory> expectedCategories = List.of(category);

        when(paymentTypeCategoryRepository.findAll()).thenReturn(expectedCategories);

        List<PaymentTypeCategory> actualCategories = paymentTypeCategoryService.findAll();

        assertThat(actualCategories).hasSize(1).containsExactly(category);
        verify(paymentTypeCategoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("save() должен маппить DTO в Entity и сохранять в репозиторий")
    void save_ShouldMapDtoToEntityAndSave() {

        PaymentTypeCategoryDto dto = new PaymentTypeCategoryDto();
        PaymentTypeCategory entityBeforeSave = new PaymentTypeCategory();
        PaymentTypeCategory savedEntity = new PaymentTypeCategory();

        when(mapper.toEntity(dto)).thenReturn(entityBeforeSave);
        when(paymentTypeCategoryRepository.save(entityBeforeSave)).thenReturn(savedEntity);

        PaymentTypeCategory result = paymentTypeCategoryService.save(dto);

        assertThat(result).isNotNull().isEqualTo(savedEntity);
        verify(mapper, times(1)).toEntity(dto);
        verify(paymentTypeCategoryRepository, times(1)).save(entityBeforeSave);
    }

    @Test
    @DisplayName("getBaseRepository() должен возвращать экземпляр PaymentTypeCategoryRepository")
    void getBaseRepository_ShouldReturnCorrectRepository() {

        var repository = paymentTypeCategoryService.getBaseRepository();

        assertThat(repository).isSameAs(paymentTypeCategoryRepository);
    }

}
