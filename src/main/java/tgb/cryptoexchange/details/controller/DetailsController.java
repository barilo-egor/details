package tgb.cryptoexchange.details.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.dto.DetailsMapper;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeDto;
import tgb.cryptoexchange.details.interfaces.service.IDetailsService;
import tgb.cryptoexchange.details.kafka.DetailsResponse;
import tgb.cryptoexchange.details.service.DetailsResponseService;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/details")
@Slf4j
public class DetailsController extends ApiController {

    private final IDetailsService detailsService;

    private final DetailsResponseService detailsResponseService;

    private final DetailsMapper detailsMapper;

    public DetailsController(IDetailsService detailsService,
            @Autowired(required = false) DetailsResponseService detailsResponseService,
            DetailsMapper detailsMapper) {
        this.detailsService = detailsService;
        this.detailsResponseService = detailsResponseService;
        this.detailsMapper = detailsMapper;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetailsDto>>> findAll(
            @RequestParam(value = "detailIds", required = false) List<Long> pids,
            @RequestParam(value = "hasTargetAmount", required = false, defaultValue = "false") boolean hasTargetAmount) {
        if (pids != null && !pids.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.success(
                    detailsMapper.toDtoList(detailsService.findAllByPids(pids))),
                    HttpStatus.OK
            );

        }
        if (hasTargetAmount) {
            return new ResponseEntity<>(ApiResponse.success(
                    detailsMapper.toDtoList(detailsService.getWithNotEmptyTargetAmount())),
                    HttpStatus.OK
            );
        }
        return new ResponseEntity<>(ApiResponse.error(
                "Переданы не правильные параметры"),
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("/{pid}")
    public ResponseEntity<ApiResponse<DetailsDto>> findById(@PathVariable Long pid) {
        DetailsDto details = detailsMapper.toDto(detailsService.findById(pid));
        return new ResponseEntity<>(ApiResponse.success(details), HttpStatus.OK);
    }

    @GetMapping("/target")
    public ResponseEntity<ApiResponse<DetailsDto>> getTarget(@RequestParam("detailIds") List<Long> detailIds,
            @RequestParam("amount") Integer amount) {
        DetailsDto details = detailsMapper.toDto(detailsService.getTarget(detailIds, amount));
        return new ResponseEntity<>(ApiResponse.success(details), HttpStatus.OK);
    }

    @PostMapping("/non-target")
    public ResponseEntity<ApiResponse<String>> getNonTargetRequisite(@RequestBody PaymentTypeDto paymentTypeDto) {
        return new ResponseEntity<>(ApiResponse.success(detailsService.getNotTargetRequisite(paymentTypeDto)),
                HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<DetailsDto>> save(@RequestBody DetailsDto detailsDto) {
        Details entity = detailsMapper.toEntity(detailsDto);
        Details saved = detailsService.save(entity);
        return new ResponseEntity<>(ApiResponse.success(detailsMapper.toDto(saved)), HttpStatus.OK);
    }

    @PostMapping("/save-all")
    public ResponseEntity<ApiResponse<List<DetailsDto>>> save(@RequestBody List<DetailsDto> detailsDto) {
        List<Details> entity = detailsDto.stream().map(detailsMapper::toEntity).toList();
        return new ResponseEntity<>(
                ApiResponse.success(detailsService.saveAll(entity).stream().map(detailsMapper::toDto).toList()),
                HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> detailIds(@RequestBody List<Long> detailIds) {
        detailsService.deleteAll(detailIds.stream().map(id -> {
            Details details = new Details();
            details.setPid(id);
            return details;
        }).toList());
        if (detailsResponseService != null) {
            detailsResponseService.process(new DetailsResponse(detailIds));
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{pid}")
    public ResponseEntity<Void> updateDetails(@PathVariable Long pid, @RequestBody DetailsDto dto) {
        detailsService.patchDetails(pid, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{pid}/reserve")
    public ResponseEntity<Void> saveReserveAmount(@PathVariable("pid") Long detailsId,
            @RequestParam("dealAmount") Integer dealAmount) {
        detailsService.saveReserveAmount(detailsId, dealAmount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pid}/confirm-payment")
    public ResponseEntity<ApiResponse<DetailsDto>> confirmPayment(@PathVariable("pid") Long detailsId,
            @RequestParam("dealAmount") Integer dealAmount) {
        return new ResponseEntity<>(
                ApiResponse.success(detailsMapper.toDto(detailsService.confirmPayment(detailsId, dealAmount))),
                HttpStatus.OK);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEntityNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

}
