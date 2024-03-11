package com.nailshop.nailborhood.controller.shop.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.service.shop.admin.ShopDeleteService;
import com.nailshop.nailborhood.service.shop.admin.ShopStatusChangeService;
import com.nailshop.nailborhood.service.shop.owner.ShopModificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailborhood")
public class AdminShopController {

    private final ShopModificationService shopModificationService;
    private final ShopDeleteService shopDeleteService;
    private final ShopStatusChangeService shopStatusChangeService;


    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "매장 정보 수정", description = "owner API")
    // 매장 정보 수정
    @PutMapping(consumes = {"multipart/form-data"}, value = "/owner/update/{shopId}")
    public ResponseEntity<ResultDto<Void>> updateShop(@RequestHeader(AUTH) String accessToken,
                                                      @PathVariable Long shopId,
                                                      @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                      @RequestPart(value = "data") ShopModifiactionRequestDto shopModifiactionRequestDto) {
        CommonResponseDto<Object> commonResponseDto = shopModificationService.updateShop(accessToken,shopId, multipartFileList, shopModifiactionRequestDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }


    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 삭제", description = "admin API")
    // 매장 삭제
    @DeleteMapping("/admin/deleteShop/{shopId}")
    public ResponseEntity<ResultDto<Void>> deleteShop(@RequestHeader(AUTH) String accessToken,
                                                      @PathVariable Long shopId) {
        CommonResponseDto<Object> commonResponseDto = shopDeleteService.deleteShop(accessToken,shopId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 상태 변경 ", description = "admin API")
    // 매장 상태 변경
    @PutMapping("/admin/shopStatus/{reportId}")
    public ResponseEntity<ResultDto<Void>> changeReviewReportStatus(@RequestHeader(AUTH) String accessToken,
                                                                    @PathVariable Long shopId,
                                                                    @RequestParam(value = "status") String status) {
        CommonResponseDto<Object> commonResponseDto = shopStatusChangeService.changeShopStatus(accessToken,shopId, status);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }
}
