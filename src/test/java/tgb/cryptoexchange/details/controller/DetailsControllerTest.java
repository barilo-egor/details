package tgb.cryptoexchange.details.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.dto.DetailsMapper;
import tgb.cryptoexchange.details.interfaces.service.IDetailsService;
import tgb.cryptoexchange.details.kafka.DetailsResponse;
import tgb.cryptoexchange.details.service.DetailsResponseService;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DetailsController.class)
class DetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDetailsService detailsService;

    @MockitoBean
    private DetailsResponseService detailsResponseService;

    @MockitoBean
    private DetailsMapper detailsMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_ShouldReturnAllDetails_WhenNoParams() throws Exception {
        List<Details> entities = List.of(new Details());
        List<DetailsDto> dtos = List.of(new DetailsDto());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Details> detailsPage = new PageImpl<>(entities, pageable, entities.size());
        when(detailsService.findAll(nullable(List.class), anyBoolean(), any(Pageable.class))).thenReturn(detailsPage);
        when(detailsMapper.toDto(any(Details.class))).thenReturn(dtos.getFirst());

        mockMvc.perform(get("/details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void findById_ShouldReturnDto_WhenExists() throws Exception {
        Long pid = 1L;
        Details entity = new Details();
        DetailsDto dto = new DetailsDto();
        dto.setPid(pid);

        when(detailsService.findById(pid)).thenReturn(entity);
        when(detailsMapper.toDto(entity)).thenReturn(dto);

        mockMvc.perform(get("/details/{pid}", pid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pid").value(pid));
    }

    @Test
    void save_ShouldReturnSavedDto() throws Exception {
        DetailsDto inputDto = new DetailsDto();
        inputDto.setRequisite("Test Req");

        Details entity = new Details();
        Details savedEntity = new Details();
        DetailsDto savedDto = new DetailsDto();
        savedDto.setPid(100L);

        when(detailsMapper.toEntity(any(DetailsDto.class))).thenReturn(entity);
        when(detailsService.save(any(Details.class))).thenReturn(savedEntity);
        when(detailsMapper.toDto(savedEntity)).thenReturn(savedDto);

        mockMvc.perform(post("/details/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pid").value(100));
    }

    @Test
    void delete_ShouldInvokeServiceAndResponseService() throws Exception {
        List<Long> ids = List.of(1L, 2L);

        mockMvc.perform(delete("/details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk());

        verify(detailsService).deleteAll(anyList());
        verify(detailsResponseService).process(any(DetailsResponse.class));
    }

    @Test
    void handleEntityNotFound_ShouldReturn404() throws Exception {
        when(detailsService.findById(999L)).thenThrow(new EntityNotFoundException("Not found"));

        mockMvc.perform(get("/details/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("Not found"));
    }
}

