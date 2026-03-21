package tgb.cryptoexchange.details.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.exception.BaseException;
import tgb.cryptoexchange.details.interfaces.dto.DetailsMapper;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;
import tgb.cryptoexchange.details.repository.DetailsRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailsServiceTest {

    @Mock
    private DetailsRepository detailsRepository;

    @Mock
    private DetailsMapper mapper;

    @InjectMocks
    private DetailsService detailsService;

    private Details testDetails;

    @BeforeEach
    void setUp() {
        testDetails = new Details();
        testDetails.setPid(1L);
        testDetails.setRequisite("Test Requisite");
        testDetails.setTargetAmount(10000);
        testDetails.setReserveAmount(0);
        testDetails.setReceivedAmount(0);
        testDetails.setRangeFrom(100);
        testDetails.setRangeTo(5000);
        testDetails.setIsOn(true);
    }

    @Test
    void getNotTargetRequisite_ShouldReturnRequisiteAndUpdateTime() {
        PaymentTypeDto dto = new PaymentTypeDto();
        dto.setDetails(List.of(1L));

        when(detailsRepository.findOldestAvailableDetail(anyList(), eq(true))).thenReturn(Optional.of(testDetails));

        String result = detailsService.getNotTargetRequisite(dto, true);

        assertEquals("Test Requisite", result);
        assertNotNull(testDetails.getLastAccessedAt());
    }

    @Test
    void getNotTargetRequisite_ShouldThrowException_WhenNotFound() {
        PaymentTypeDto dto = new PaymentTypeDto();
        dto.setName("Card");
        when(detailsRepository.findOldestAvailableDetail(any(), eq(true))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> detailsService.getNotTargetRequisite(dto, true));
    }

    @Test
    void getTarget_ShouldReserveAndReturn_WhenConditionsMet() {
        when(detailsRepository.findAllByPidInAndTargetAmountNotEmpty(anyList(), eq(true)))
                .thenReturn(List.of(testDetails));
        when(detailsRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Details result = detailsService.getTarget(List.of(1L), 500, true);

        assertNotNull(result);
        assertEquals(500, result.getReserveAmount());
        verify(detailsRepository).save(testDetails);
    }

    @Test
    void getTarget_ShouldReturnNull_WhenAmountIsOutOfRange() {
        when(detailsRepository.findAllByPidInAndTargetAmountNotEmpty(anyList(), eq(true)))
                .thenReturn(List.of(testDetails));

        Details result = detailsService.getTarget(List.of(1L), 10000, true);

        assertNull(result);
    }

    @Test
    void getTarget_ShouldReturnNull_WhenNoCapacityLeft() {
        testDetails.setReceivedAmount(9800);
        when(detailsRepository.findAllByPidInAndTargetAmountNotEmpty(anyList(), eq(true)))
                .thenReturn(List.of(testDetails));

        Details result = detailsService.getTarget(List.of(1L), 500, true);

        assertNull(result);
    }


    @Test
    void findById_ShouldReturnDetails_WhenExists() {
        when(detailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));
        Details result = detailsService.findById(1L);
        assertEquals(testDetails, result);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        when(detailsRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> detailsService.findById(99L));
    }

    @Test
    void saveReserveAmount_ShouldDecreaseReserveAndHandleNegative() {
        testDetails.setReserveAmount(500);
        when(detailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        detailsService.saveReserveAmount(1L, 600);

        assertEquals(0, testDetails.getReserveAmount());
        verify(detailsRepository).save(testDetails);
    }

    @Test
    void confirmPayment_ShouldUpdateBothAmounts() {
        testDetails.setReserveAmount(1000);
        testDetails.setReceivedAmount(2000);
        when(detailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));
        when(detailsRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Details result = detailsService.confirmPayment(1L, 400);

        assertEquals(600, result.getReserveAmount());
        assertEquals(2400, result.getReceivedAmount());
    }

    @Test
    void confirmPayment_ShouldThrowBaseException_IfReserveExceedsTarget() {
        testDetails.setTargetAmount(1000);
        testDetails.setReserveAmount(100);
        when(detailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        assertThrows(BaseException.class, () -> detailsService.confirmPayment(1L, -1000));
    }

    @Test
    void patchDetails_ShouldUseNotNullMapper_WhenFlagIsTrue() {
        DetailsDto dto = new DetailsDto();
        when(detailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        detailsService.patchDetails(1L, dto, true);

        verify(mapper).updateEntityFromDtoNotNull(dto, testDetails);
        verify(detailsRepository).save(testDetails);
    }

    @Test
    void patchDetails_ShouldUseFullMapper_WhenFlagIsFalse() {
        DetailsDto dto = new DetailsDto();
        when(detailsRepository.findById(1L)).thenReturn(Optional.of(testDetails));

        detailsService.patchDetails(1L, dto, false);

        verify(mapper).updateEntityFromDto(dto, testDetails);
        verify(detailsRepository).save(testDetails);
    }

    @Test
    void saveAll_ShouldInvokeRepository() {
        List<Details> list = List.of(testDetails);
        detailsService.saveAll(list);
        verify(detailsRepository).saveAll(list);
    }

    @Test
    void updateRequisiteByPid_ShouldInvokeRepository() {
        detailsService.updateRequisiteByPid("New Req", 1L);
        verify(detailsRepository).updateRequisiteByPid("New Req", 1L);
    }
}
