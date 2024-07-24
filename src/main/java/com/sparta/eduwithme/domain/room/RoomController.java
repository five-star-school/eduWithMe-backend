package com.sparta.eduwithme.domain.room;

import com.sparta.eduwithme.common.response.DataCommonResponse;
import com.sparta.eduwithme.common.response.StatusCommonResponse;
import com.sparta.eduwithme.domain.room.dto.CreatePrivateRoomRequestDto;
import com.sparta.eduwithme.domain.room.dto.CreatePublicRoomRequestDto;
import com.sparta.eduwithme.domain.room.dto.SelectRoomListResponseDto;
import com.sparta.eduwithme.domain.room.dto.UpdateRequestDto;
import com.sparta.eduwithme.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    /**
     * [public room 생성 기능]
     * @param requestDto : roomTitle
     * @return : message, HttpStatusCode
     */
    @PostMapping("/public")
    public ResponseEntity<StatusCommonResponse> createPublicRoom(
            @RequestBody @Valid CreatePublicRoomRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        roomService.createPublicRoom(requestDto, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(
                HttpStatus.CREATED.value(),
                "성공적으로 public 룸이 생성 되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/private")
    public ResponseEntity<StatusCommonResponse> createPrivateRoom(
            @RequestBody @Valid CreatePrivateRoomRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        roomService.createPrivateRoom(requestDto, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(
                HttpStatus.CREATED.value(),
                "성공적으로 private 룸이 생성 되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<DataCommonResponse<List<SelectRoomListResponseDto>>> getRoomListWithPage(
            @RequestParam(value = "page") int page)
    {
        List<SelectRoomListResponseDto> responseDtoList = roomService.getRoomListWithPage(page);
        DataCommonResponse<List<SelectRoomListResponseDto>> response = new DataCommonResponse<>(
                HttpStatus.OK.value(),
                "성공적으로 조회가 되었습니다.",
                responseDtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<StatusCommonResponse> updateRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @RequestBody UpdateRequestDto requestDto,
                                                           @PathVariable Long roomId)
    {
        roomService.updateRoom(userDetails.getUser(), roomId, requestDto);
        StatusCommonResponse response = new StatusCommonResponse(HttpStatus.OK.value(), "방 제목 변경 성공");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<StatusCommonResponse> deleteRoom(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                           @PathVariable Long roomId) {
        roomService.deleteRoom(userDetails.getUser(), roomId);
        StatusCommonResponse response = new StatusCommonResponse(
                HttpStatus.NO_CONTENT.value(),
                "방 삭제가 완료 되었습니다.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
