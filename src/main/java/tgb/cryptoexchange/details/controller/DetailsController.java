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

import static tgb.cryptoexchange.web.ApiResponse.Error.ErrorCode.ENTITY_NOT_FOUND;

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
        Optional<DetailsDto> details = detailsService.findByIdOptional(pid).map(detailsMapper::toDto);
        return details.map(dto -> new ResponseEntity<>(ApiResponse.success(
                dto),
                HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<>(ApiResponse.error(
                "Не найден реквизит"),
                HttpStatus.NOT_FOUND
        ));
    }

    @GetMapping("/target")
    public ResponseEntity<ApiResponse<DetailsDto>> getTarget(@RequestParam("detailIds") List<Long> detailIds,
            @RequestParam("amount") Integer amount) {
        Optional<DetailsDto> details = detailsService.getTarget(detailIds, amount).map(detailsMapper::toDto);
        return details.map(dto -> new ResponseEntity<>(ApiResponse.success(
                dto),
                HttpStatus.OK
        )).orElseGet(() -> new ResponseEntity<>(ApiResponse.error(
                "Не найден целевой реквизит"),
                HttpStatus.NOT_FOUND
        ));
    }

    @PostMapping("/non-target")
    public ResponseEntity<ApiResponse<String>> getNonTargetRequisite(@RequestBody PaymentTypeDto paymentTypeDto) {
        try {
            return new ResponseEntity<>(ApiResponse.success(detailsService.getNotTargetRequisite(paymentTypeDto)),
                    HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.NOT_FOUND);
        }
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
        if(detailsResponseService!=null) {
            detailsResponseService.process(new DetailsResponse(detailIds));
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{pid}")
    public ResponseEntity<Void> updateDetails(@PathVariable Long pid, @RequestBody DetailsDto dto) {
        detailsService.patchDetails(pid, dto);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/order/{paymentTypeId}")
//    public ResponseEntity<ApiResponse<Integer>> getOrder(@PathVariable Long paymentTypeId) {
//        return new ResponseEntity<>(ApiResponse.success(detailsService.getOrder(paymentTypeId)), HttpStatus.OK);
//    }

//    @PostMapping("/order/check")
//    public ResponseEntity<Void> checkOrder(@RequestBody PaymentTypeDto paymentType) {
//        detailsService.checkOrder(paymentType);
//        return ResponseEntity.ok().build();
//    }

//    @PutMapping("/order")
//    public ResponseEntity<Void> updateOrder(@RequestBody PaymentTypeDto paymentType) {
//        detailsService.updateOrder(paymentType);
//        return ResponseEntity.ok().build();
//    }

//    @DeleteMapping("/order/{paymentTypeId}")
//    public ResponseEntity<Void> removeOrder(@PathVariable Long paymentTypeId) {
//        detailsService.removeOrder(paymentTypeId);
//        return ResponseEntity.ok().build();
//    }

    @PatchMapping("/{pid}/reserve")
    public ResponseEntity<Void> saveReserveAmount(@PathVariable("pid") Long detailsId,
            @RequestParam("dealAmount") Integer dealAmount) {
        detailsService.saveReserveAmount(detailsId, dealAmount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pid}/confirm-payment")
    public ResponseEntity<Void> confirmPayment(@PathVariable("pid") Long detailsId,
            @RequestParam("dealAmount") Integer dealAmount) {
        detailsService.confirmPayment(detailsId, dealAmount);
        return ResponseEntity.ok().build();
    }

}
