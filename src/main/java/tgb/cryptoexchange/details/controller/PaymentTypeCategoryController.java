package tgb.cryptoexchange.details.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tgb.cryptoexchange.controller.ApiController;
import tgb.cryptoexchange.details.dto.PaymentTypeCategoryDto;
import tgb.cryptoexchange.details.entity.PaymentTypeCategory;
import tgb.cryptoexchange.details.interfaces.dto.PaymentTypeCategoryMapper;
import tgb.cryptoexchange.details.interfaces.service.IPaymentTypeCategoryService;
import tgb.cryptoexchange.details.kafka.CategoryUnlinkingProducer;
import tgb.cryptoexchange.web.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/details/payment-type-category")
public class PaymentTypeCategoryController extends ApiController {

    private final IPaymentTypeCategoryService paymentTypeCategoryService;

    private final PaymentTypeCategoryMapper mapper;

    private final CategoryUnlinkingProducer categoryUnlinkingProducer;

    public PaymentTypeCategoryController(IPaymentTypeCategoryService paymentTypeCategoryService,
                                         PaymentTypeCategoryMapper mapper,
                                         CategoryUnlinkingProducer categoryUnlinkingProducer) {
        this.paymentTypeCategoryService = paymentTypeCategoryService;
        this.mapper = mapper;
        this.categoryUnlinkingProducer = categoryUnlinkingProducer;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentTypeCategoryDto>>> findAll() {
        List<PaymentTypeCategoryDto> paymentTypeCategories = mapper.toDtoList(paymentTypeCategoryService.findAll());
        return new ResponseEntity<>(ApiResponse.success(paymentTypeCategories), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<PaymentTypeCategoryDto>> save(@RequestBody PaymentTypeCategoryDto paymentTypeCategoryDto) {
        PaymentTypeCategory saved = paymentTypeCategoryService.save(paymentTypeCategoryDto);
        return new ResponseEntity<>(ApiResponse.success(mapper.toDto(saved)), HttpStatus.OK);
    }

    @DeleteMapping("/{pid}")
    public ResponseEntity<Void> delete(@PathVariable Long pid) {
        PaymentTypeCategory paymentTypeCategory = new PaymentTypeCategory();
        paymentTypeCategory.setPid(pid);
        paymentTypeCategoryService.delete(paymentTypeCategory);
        if (categoryUnlinkingProducer != null) {
            categoryUnlinkingProducer.send(pid);
        }
        return ResponseEntity.ok().build();
    }

}
