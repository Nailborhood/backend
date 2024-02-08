package com.nailshop.nailborhood.controller.review;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.service.review.ReviewInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/nailshop")
public class ReviewInquiryController {

    private final ReviewInquiryService reviewInquiryService;


    // 리뷰 상세 조회
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ResultDto<ReviewDetailResponseDto>> detailReview(@PathVariable Long reviewId,
                                                                           @RequestParam(value = "customerId") Long customerId,
                                                                           @RequestParam(value = "shopId") Long shopId){
        CommonResponseDto<Object> detailReview = reviewInquiryService.detailReview(reviewId, customerId, shopId);
        ResultDto<ReviewDetailResponseDto> resultDto = ResultDto.in(detailReview.getStatus(), detailReview.getMessage());
        resultDto.setData((ReviewDetailResponseDto) detailReview.getData());

        return ResponseEntity.status(detailReview.getHttpStatus()).body(resultDto);
    }

    // 리뷰 리스트 조회
//    @GetMapping("/review")
//    public ResponseEntity<ResultDto<ReviewListResponseDto>> allReview(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
//                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) int size,
//                                                                      @RequestParam(value = "sortBy", defaultValue = "likeCnt", required = false) String sortBy,
//                                                                      @RequestParam(value = "sort", defaultValue = "DESC", required = false)String sort){
//        CommonResponseDto<Object> allReview = reviewInquiryService.allReview(page, size, sortBy);
//        ResultDto<ReviewListResponseDto> resultDto = ResultDto.in(allReview.getStatus(), allReview.getMessage());
//        resultDto.setData((ReviewListResponseDto) allReview.getData());
//
//        return ResponseEntity.status(allReview.getHttpStatus()).body(resultDto);
//    }
}