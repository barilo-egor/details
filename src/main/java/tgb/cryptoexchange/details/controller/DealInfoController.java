package tgb.cryptoexchange.details.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.details.dto.DealInfoDto;
import tgb.cryptoexchange.details.entity.DealInfo;
import tgb.cryptoexchange.details.interfaces.dto.DealInfoMapper;
import tgb.cryptoexchange.details.interfaces.service.IDealInfoService;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/details/deal-info")
@Slf4j
public class DealInfoController extends ApiController {

    private final IDealInfoService dealInfoService;

    private final DealInfoMapper dealInfoMapper;

    public DealInfoController(IDealInfoService dealInfoService, DealInfoMapper dealInfoMapper) {
        this.dealInfoService = dealInfoService;
        this.dealInfoMapper = dealInfoMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DealInfoDto>>> findAll(
            @RequestBody(required = false) DealInfoDto dealInfoDto, @PageableDefault(size = 20) Pageable pageable) {
        if(dealInfoDto==null) dealInfoDto = new DealInfoDto();
        Page<DealInfo> dealInfos = dealInfoService.findAll(dealInfoDto, pageable);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(dealInfos.getTotalElements()))
                .body(ApiResponse.success(dealInfos.stream().map(dealInfoMapper::toDto).toList()));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<DealInfoDto>> save(@RequestBody DealInfoDto dealInfoDto) {
        DealInfo saved = dealInfoService.save(dealInfoMapper.toEntity(dealInfoDto));
        return new ResponseEntity<>(ApiResponse.success(dealInfoMapper.toDto(saved)), HttpStatus.OK);
    }

}
