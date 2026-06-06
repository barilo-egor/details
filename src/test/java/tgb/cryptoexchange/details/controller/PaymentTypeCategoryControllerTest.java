package tgb.cryptoexchange.details.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.details.dto.PaymentTypeCategoryDto;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeCategoryMapper;
import tgb.cryptoexchange.details.interfaces.service.IPaymentTypeCategoryService;
import tgb.cryptoexchange.details.kafka.CategoryUnlinkingProducer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentTypeCategoryController.class)
class PaymentTypeCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPaymentTypeCategoryService paymentTypeCategoryService;

    @MockitoBean
    private PaymentTypeCategoryMapper mapper;

    @MockitoBean
    private CategoryUnlinkingProducer categoryUnlinkingProducer;

    @Test
    @DisplayName("GET /details/payment-type-category — Успешное получение списка всех категорий")
    void shouldFindAllCategories() throws Exception {

        List<PaymentTypeCategory> mockEntities = List.of(new PaymentTypeCategory());
        List<PaymentTypeCategoryDto> mockDtoList = List.of(new PaymentTypeCategoryDto());

        when(paymentTypeCategoryService.findAll()).thenReturn(mockEntities);
        when(mapper.toDtoList(mockEntities)).thenReturn(mockDtoList);

        mockMvc.perform(get("/details/payment-type-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /details/payment-type-category — Успешное сохранение новой категории")
    void shouldSaveCategory() throws Exception {

        PaymentTypeCategoryDto inputDto = new PaymentTypeCategoryDto();
        PaymentTypeCategory savedEntity = new PaymentTypeCategory();
        PaymentTypeCategoryDto outputDto = new PaymentTypeCategoryDto();

        when(paymentTypeCategoryService.save(any(PaymentTypeCategoryDto.class))).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(outputDto);

        mockMvc.perform(post("/details/payment-type-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /details/payment-type-category/{pid} — Удаление категории и отправка события отвязки в Kafka")
    void shouldDeleteCategoryAndTriggerKafkaEvent() throws Exception {

        Long testPid = 123L;

        mockMvc.perform(delete("/details/payment-type-category/{pid}", testPid))
                .andExpect(status().isOk());

        verify(paymentTypeCategoryService, times(1)).delete(argThat(category ->
                category != null && testPid.equals(category.getPid())
        ));

        verify(categoryUnlinkingProducer, times(1)).send(testPid);
    }

}
