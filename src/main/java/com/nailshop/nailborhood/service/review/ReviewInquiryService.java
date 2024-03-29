package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.review.ReviewReport;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewDetailResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewReportRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ReviewInquiryService {

    private final CommonService commonService;
    private final ShopRepository shopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final CategoryReviewRepository categoryReviewRepository;


    // 리뷰 상세조회
    public CommonResponseDto<Object> detailReview(Long reviewId, Long shopId) {

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 가져오기
        Review review = reviewRepository.findReviewByFalse(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        // 고객 닉네임, 프로필 이미지 가져오기
        String nickName = review.getCustomer().getMember().getNickname();
        String profileImg = review.getCustomer().getMember().getProfileImg();

        // 영업상태, 신고상태, 카테고리
        ShopStatus shopStatus = shop.getStatus();

        ReviewReport reviewReport = reviewReportRepository.findReviewReportByReviewId(reviewId);
        String reviewReportStatus = (reviewReport != null) ? reviewReport.getStatus() : "신고 되지 않았음";  // "리뷰 보고서 없음" 대신 적절한 기본값을 설정해주세요.

        List<String> categoryList = categoryReviewRepository.findCategoryTypeByReviewId(reviewId);



        //리뷰 이미지
        List<ReviewImg> reviewImgList = reviewImgRepository.findByReviewImgListReviewId(reviewId);
        Map<Integer, String> reviewImgPathMap = new HashMap<>();
        for (ReviewImg reviewImg : reviewImgList) {
            reviewImgPathMap.put(reviewImg.getImgNum(), reviewImg.getImgPath());
        }

        ReviewDetailResponseDto reviewDetailResponseDto = ReviewDetailResponseDto.builder()
                .reviewId(reviewId)
                .shopName(shop.getName())
                .shopStatus(shopStatus)
                .reviewReportStatus(reviewReportStatus)
                .categoryTypeList(categoryList)
                .imgPathMap(reviewImgPathMap)
                .contents(review.getContents())
                .rate(review.getRate())
                .likeCnt(review.getLikeCnt())
                .reviewAuthor(nickName)
                .reviewAuthorProfileImg(profileImg)
                .reviewCreatedAt(review.getCreatedAt())
                .reviewUpdatedAt(review.getUpdatedAt())
                .build();

        return commonService.successResponse(SuccessCode.REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, reviewDetailResponseDto);
    }


    // 리뷰 전체 조회
    public CommonResponseDto<Object> allReview(int page, int size, String sortBy, String category) {

        // category 리스트화
        List<Long> categoryIdList = null;
        if (category != null && !category.isEmpty()){

            categoryIdList = Arrays.stream(category.split(","))
                    .map(Long::parseLong)
                    .toList();
        }

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy).descending());
        Page<Review> reviewPage;

        if(categoryIdList == null || categoryIdList.isEmpty()){
            // 카테고리 x
            reviewPage = reviewRepository.findAllIsDeletedFalse(pageable);
        }
        else {
            reviewPage = reviewRepository.findByCategoryIdListAndIsDeletedFalse(categoryIdList, pageable);
        }

        if(reviewPage.isEmpty()){
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }

        List<Review> reviewList = reviewPage.getContent();
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Review review : reviewList ){

            String mainImgPath = review.getReviewImgList().getFirst().getImgPath();

            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .mainImgPath(mainImgPath)
                    .categoryTypeList(categoryTypeList)
                    .contents(review.getContents())
                    .rate(review.getRate())
                    .likeCnt(review.getLikeCnt())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();

            reviewResponseDtoList.add(reviewResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .pageNo(reviewPage.getNumber())
                .isLastPage(reviewPage.isLast())
                .build();

        ReviewListResponseDto reviewListResponseDto = ReviewListResponseDto.builder()
                .reviewResponseDtoList(reviewResponseDtoList)
                .paginationDto(paginationDto)
                .build();


        return commonService.successResponse(SuccessCode.REVIEW_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, reviewListResponseDto);
    }

}
