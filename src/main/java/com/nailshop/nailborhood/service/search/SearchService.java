package com.nailshop.nailborhood.service.search;

import com.nailshop.nailborhood.domain.artboard.ArtRef;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.artboard.ArtResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewListResponseDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.artboard.ArtRefRepository;
import com.nailshop.nailborhood.repository.category.CategoryArtRepository;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CommonService commonService;
    private final ReviewRepository reviewRepository;
    private final ArtRefRepository artRefRepository;
    private final CategoryReviewRepository categoryReviewRepository;
    private final CategoryArtRepository categoryArtRepository;

    // 리뷰로 검색
    public CommonResponseDto<Object> searchReviewInquiry(String keyword, int page, int size, String sortBy) {

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                .descending());

        Page<Review> reviewSearchPage = reviewRepository.findReviewListBySearch(keyword, pageable);
        if(reviewSearchPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.REVIEW_NOT_FOUND);
        }

        List<Review> reviewList = reviewSearchPage.getContent();
        List<ReviewResponseDto> reviewResponseDtoList = new ArrayList<>();

        for(Review review : reviewList ){

            String mainImgPath = review.getReviewImgList().get(0).getImgPath();
            String shopName = review.getShop().getName();

            List<String> categoryTypeList = categoryReviewRepository.findCategoryTypeByReviewId(review.getReviewId());

            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .reviewId(review.getReviewId())
                    .shopName(shopName)
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
                .totalPages(reviewSearchPage.getTotalPages())
                .totalElements(reviewSearchPage.getTotalElements())
                .pageNo(reviewSearchPage.getNumber())
                .isLastPage(reviewSearchPage.isLast())
                .build();

        ReviewListResponseDto reviewListResponseDto = ReviewListResponseDto.builder()
                .reviewResponseDtoList(reviewResponseDtoList)
                .paginationDto(paginationDto)
                .build();

        return commonService.successResponse(SuccessCode.SEARCH_BY_REVIEW_SUCCESS.getDescription(), HttpStatus.OK, reviewListResponseDto);
    }

    // 아트판으로 검색
    public CommonResponseDto<Object> searchArtRefInquiry(String keyword, int page, int size, String sortBy) {

        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy)
                .descending());

        Page<ArtRef> artSearchPage = artRefRepository.findArtRefListBySearch(keyword, pageable);
        if(artSearchPage.isEmpty()) {
            throw new NotFoundException(ErrorCode.ART_NOT_FOUND);
        }

        List<ArtRef> artRefList = artSearchPage.getContent();
        List<ArtResponseDto> artResponseDtoList = new ArrayList<>();

        for(ArtRef artRef : artRefList ){

            String mainImgPath = artRef.getArtImgList().get(0).getImgPath();
            String shopName = artRef.getShop().getName();

            List<String> categoryTypeList = categoryArtRepository.findCategoryTypesByArtRefId(artRef.getArtRefId());

            ArtResponseDto artResponseDto = ArtResponseDto.builder()
                    .name(artRef.getName())
                    .content(artRef.getContent())
                    .likeCount(artRef.getLikeCount())
                    .mainImgPath(mainImgPath)
                    .shopName(shopName)
                    .categoryTypeList(categoryTypeList)
                    .createdAt(artRef.getCreatedAt())
                    .updatedAt(artRef.getUpdatedAt())
                    .build();

            artResponseDtoList.add(artResponseDto);
        }

        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(artSearchPage.getTotalPages())
                .totalElements(artSearchPage.getTotalElements())
                .pageNo(artSearchPage.getNumber())
                .isLastPage(artSearchPage.isLast())
                .build();

        ArtListResponseDto artListResponseDto = ArtListResponseDto.builder()
                .artResponseDtoList(artResponseDtoList)
                .paginationDto(paginationDto)
                .build();

        return commonService.successResponse(SuccessCode.SEARCH_BY_ART_SUCCESS.getDescription(), HttpStatus.OK, artListResponseDto);
    }
}