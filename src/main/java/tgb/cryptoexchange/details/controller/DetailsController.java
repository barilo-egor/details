package tgb.cryptoexchange.details.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.details.dto.DetailsDto;
import tgb.cryptoexchange.details.entity.Details;
import tgb.cryptoexchange.details.interfaces.dto.DetailsMapper;
import tgb.cryptoexchange.details.interfaces.service.IDetailsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/details")
@Slf4j
public class DetailsController extends ApiController {

    private final IDetailsService detailsService;

    private final DetailsMapper detailsMapper;

    public DetailsController(IDetailsService detailsService, DetailsMapper detailsMapper) {
        this.detailsService = detailsService;
        this.detailsMapper = detailsMapper;
    }

    @PatchMapping("/{pid}/requisite")
    public ResponseEntity<Void> updateRequisite(@PathVariable Long pid, @RequestParam String requisite) {
        detailsService.updateRequisiteByPid(requisite, pid);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<DetailsDto> findAll(@RequestParam(value = "detailIds", required = false) List<Long> pids,
                                    @RequestParam(value = "hasTargetAmount", required = false, defaultValue = "false") boolean hasTargetAmount) {
        if (pids != null && !pids.isEmpty()) {
            return detailsMapper.toDtoList(detailsService.findAllByPids(pids));
        }
        if (hasTargetAmount) {
            return detailsMapper.toDtoList(detailsService.getWithNotEmptyTargetAmount());
        }
        return List.of();
    }

    @GetMapping("/{pid}")
    public Optional<DetailsDto> findById(@PathVariable Long pid) {
        return detailsService.findByIdOptional(pid)
                .map(detailsMapper::toDto);
    }

    @GetMapping("/target")
    public Optional<DetailsDto> getTarget(@RequestParam("detailIds") List<Long> detailIds,
                                          @RequestParam("amount") Integer amount) {
        Optional<Details> details = detailsService.getTarget(detailIds, amount);
        if (details.isEmpty()) {
            return Optional.empty();
        }
        return details.map(detailsMapper::toDto);
    }

    @GetMapping("/order/{paymentTypeId}")
    public Integer getOrder(@PathVariable("paymentTypeId") Long paymentTypeId) {
        return detailsService.getOrder(paymentTypeId);
    }

    @PutMapping("/order/{paymentTypeId}")
    public void updateOrder(@PathVariable("paymentTypeId") Long paymentTypeId, @RequestBody List<Long> detailIds) {
        detailsService.updateOrder(paymentTypeId, detailIds);
    }

    @PostMapping("/order/{paymentTypeId}/check")
    public ResponseEntity<Void> checkOrder(@PathVariable Long paymentTypeId, @RequestBody List<Long> detailIds) {
        detailsService.checkOrder(paymentTypeId, detailIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/order/{paymentTypeId}")
    public void removeOrder(@PathVariable("paymentTypeId") Long paymentTypeId) {
        detailsService.removeOrder(paymentTypeId);
    }

    @PatchMapping("/{pid}/reserve")
    public void saveReserveAmount(@PathVariable("pid") Long detailsId, @RequestParam("dealAmount") Integer dealAmount) {
        detailsService.saveReserveAmount(detailsId, dealAmount);
    }

    @PostMapping("/{pid}/confirm-payment")
    public void confirmPayment(@PathVariable("pid") Long detailsId, @RequestParam("dealAmount") Integer dealAmount) {
        detailsService.confirmPayment(detailsId, dealAmount);
    }

}
