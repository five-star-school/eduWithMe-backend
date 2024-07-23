package com.sparta.eduwithme.domain.room;

import com.sparta.eduwithme.common.response.DataCommonResponse;
import com.sparta.eduwithme.common.response.StatusCommonResponse;
import com.sparta.eduwithme.domain.room.dto.CreateRoomRequestDto;
import com.sparta.eduwithme.domain.room.dto.SelectRoomListResponseDto;
import com.sparta.eduwithme.security.UserDetailsImpl;
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
     * [방 생성 기능]
     * @param requestDto : (방 제목, 방 패스워드)
     * @return : message, HttpStatusCode
     */
    @PostMapping
    public ResponseEntity<StatusCommonResponse> createRoom(
            @RequestBody CreateRoomRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        roomService.createRoom(requestDto, userDetails.getUser());
        StatusCommonResponse response = new StatusCommonResponse(
                HttpStatus.CREATED.value(),
                "성공적으로 방 생성이 되었습니다.");
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
}
