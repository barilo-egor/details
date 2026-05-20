package tgb.cryptoexchange.details.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.entity.DealInfo;
import tgb.cryptoexchange.details.interfaces.dto.DealInfoMapper;
import tgb.cryptoexchange.details.interfaces.service.IDealInfoService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealInfoController.class)
class DealInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IDealInfoService dealInfoService;

    @MockitoBean
    private DealInfoMapper dealInfoMapper;

    @Test
    @DisplayName("Должен вернуть список сделок, статус 200 и заголовок X-Total-Count")
    void shouldReturnDealInfoListSuccessfully() throws Exception {
        DealInfoDto requestDto = new DealInfoDto();
        DealInfo dealInfo = new DealInfo();
        DealInfoDto responseDto = new DealInfoDto();
        Pageable pageable = PageRequest.of(0, 20);
        Page<DealInfo> page = new PageImpl<>(List.of(dealInfo), pageable, 1L);

        when(dealInfoService.findAll(any(DealInfoDto.class), any(Pageable.class))).thenReturn(page);
        when(dealInfoMapper.toDto(dealInfo)).thenReturn(responseDto);

        mockMvc.perform(get("/details/deal-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(dealInfoService).findAll(any(DealInfoDto.class), any(Pageable.class));
        verify(dealInfoMapper).toDto(dealInfo);
    }

    @Test
    @DisplayName("Должен успешно работать, если RequestBody отсутствует (null)")
    void shouldHandleNullRequestBodyAndReturnEmptyList() throws Exception {
        Page<DealInfo> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0L);
        when(dealInfoService.findAll(any(DealInfoDto.class), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/details/deal-info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "0"))
                .andExpect(jsonPath("$.data").isEmpty());

        verify(dealInfoService).findAll(any(DealInfoDto.class), any(Pageable.class));
    }


    @Test
    @DisplayName("Должен сохранить сделку и вернуть статус 200 с сохраненным объектом")
    void shouldSaveDealInfoSuccessfully() throws Exception {
        DealInfoDto inputDto = new DealInfoDto();
        DealInfo entityToSave = new DealInfo();
        DealInfo savedEntity = new DealInfo();
        DealInfoDto resultDto = new DealInfoDto();

        when(dealInfoMapper.toEntity(any(DealInfoDto.class))).thenReturn(entityToSave);
        when(dealInfoService.save(entityToSave)).thenReturn(savedEntity);
        when(dealInfoMapper.toDto(savedEntity)).thenReturn(resultDto);


        mockMvc.perform(post("/details/deal-info/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(dealInfoMapper).toEntity(any(DealInfoDto.class));
        verify(dealInfoService).save(entityToSave);
        verify(dealInfoMapper).toDto(savedEntity);
    }

}
