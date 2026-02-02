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

    @PostMapping("/update-requisite")
    public ResponseEntity<Void> updateRequisite(@RequestParam String requisite, @RequestParam Long pid) {
        detailsService.updateRequisiteByPid(requisite, pid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/with-target-amount")
    public List<DetailsDto> getWithNotEmptyTargetAmount() {
        List<Details> details = detailsService.getWithNotEmptyTargetAmount();
        return detailsMapper.toDtoList(details);
    }

    @GetMapping("/{pid}")
    public Optional<DetailsDto> findById(@PathVariable Long pid) {
        return detailsService.findByIdOptional(pid)
                .map(detailsMapper::toDto);
    }

    @GetMapping("/by-ids")
    public List<DetailsDto> findByIds(@RequestParam("detailIds") List<Long> detailIds) {
        return detailsMapper.toDtoList(detailsService.findAllByPids(detailIds));

    }

    @GetMapping("/target")
    public Optional<DetailsDto> getTarget(@RequestParam("detailIds") List<Long> detailIds,
            @RequestParam("amount") Integer amount) {
        Optional<Details> details = detailsService.getTarget(detailIds, amount);
        if(details.isEmpty()){
            return Optional.empty();
        }
        return details.map(detailsMapper::toDto);
    }

    @GetMapping("/order/{pid}")
    public Integer getOrder(@PathVariable("pid") Long paymentTypeId) {
        return detailsService.getOrder(paymentTypeId);
    }

    @PostMapping("/order/update/{pid}")
    public void updateOrder(@PathVariable("pid") Long paymentTypeId,@RequestParam("detailIds") List<Long> detailIds) {
        detailsService.updateOrder(paymentTypeId, detailIds);
    }

    @PostMapping("/order/check/{pid}")
    public void checkOrder(@PathVariable("pid") Long paymentTypeId,@RequestParam("detailIds") List<Long> detailIds) {
        detailsService.checkOrder(paymentTypeId, detailIds);
    }

    @DeleteMapping("/order/{pid}")
    public void removeOrder(@PathVariable("pid") Long paymentTypeId) {
        detailsService.removeOrder(paymentTypeId);
    }

    @PostMapping("/save-reserve-amount")
    public void saveReserveAmount(@RequestParam("pid") Long detailsId, @RequestParam("dealAmount") Integer dealAmount){
        detailsService.saveReserveAmount(detailsId, dealAmount);
    }

    @PostMapping("/confirm-payment")
    public void confirmPayment(@RequestParam("pid") Long detailsId, @RequestParam("dealAmount") Integer dealAmount){
        detailsService.confirmPayment(detailsId, dealAmount);
    }

}
