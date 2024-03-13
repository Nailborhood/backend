package com.nailshop.nailborhood.controller.owner;


import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ShopReviewListLookupResponseDto;
import com.nailshop.nailborhood.service.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailborhood")
public class OwnerController {

    private final OwnerService ownerService;

    // 검색기능이랑 통합
    @GetMapping("/owner/review/{shopId}")
    public ResponseEntity<ResultDto<ShopReviewListLookupResponseDto>> getShopReviewList( @PathVariable Long shopId,
                                                                                        @RequestParam(value = "keyword",required = false) String keyword,
                                                                                        @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                        @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                                        @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                                        @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort){
        CommonResponseDto<Object> shopReview = ownerService.getAllReviewListByShopId(keyword, page, size, criteria, sort, shopId);
        ResultDto<ShopReviewListLookupResponseDto> resultDto = ResultDto.in(shopReview.getStatus(), shopReview.getMessage());
        resultDto.setData((ShopReviewListLookupResponseDto) shopReview.getData());

        return ResponseEntity.status(shopReview.getHttpStatus()).body(resultDto);
    }
}
