package com.nailshop.nailborhood.controller.member;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.response.FavoriteResponseDto;
import com.nailshop.nailborhood.dto.shop.response.ShopDetailListResponseDto;
import com.nailshop.nailborhood.service.member.FavoriteShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailshop")
public class FavoriteController {
    private final FavoriteShopService favoriteShopService;

    // TODO: memberId -> accessToken으로 변경
    @Tag(name = "favorite", description = "favorite API")
    @Operation(summary = "매장 찜 ", description = "favorite API")
    // 매장 상세 조회
    @GetMapping("/favorite/{shopId}")
    public ResponseEntity<ResultDto<FavoriteResponseDto>> getShopDetail(@PathVariable Long shopId , @RequestParam("memberId") Long memberId){
        CommonResponseDto<Object> favoriteShop = favoriteShopService.favoriteShop(shopId,memberId);
        ResultDto<FavoriteResponseDto> resultDto = ResultDto.in(favoriteShop.getStatus(), favoriteShop.getMessage());
        resultDto.setData((FavoriteResponseDto) favoriteShop.getData());

        return ResponseEntity.status(favoriteShop.getHttpStatus()).body(resultDto);
    }
}