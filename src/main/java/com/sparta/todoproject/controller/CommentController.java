package com.sparta.todoproject.controller;

import com.sparta.todoproject.dto.*;
import com.sparta.todoproject.security.UserDetailsImpl;
import com.sparta.todoproject.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/comments")

public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 댓글 추가
     * @param scheduleId
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<ResponseMsg<CommentResponseDto>> addComment(@PathVariable Long scheduleId, @RequestBody @Valid CommentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CommentResponseDto responseDto = commentService.addComment(scheduleId, requestDto, userDetails);

        // 제네릭은 builder 바로 앞에 적어주는 것이 특징
        ResponseMsg<CommentResponseDto> responseMsg = ResponseMsg.<CommentResponseDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("댓글이 추가되었습니다.")
                .data(responseDto)
                .build();

        // new로 생성해주지 않아도 되는 이유가 무엇일까..
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseMsg);
    }

    /**
     * 댓글 수정
     * @param id
     * @param requestDto
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMsg<CommentResponseDto>> updateComment(@PathVariable Long scheduleId, @PathVariable Long id, @RequestBody @Valid CommentRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CommentResponseDto responseDto = commentService.updateComment(scheduleId, id, requestDto, userDetails);

        ResponseMsg<CommentResponseDto> responseMsg = ResponseMsg.<CommentResponseDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("댓글이 수정되었습니다.")
                .data(responseDto)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseMsg);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMsg<Void>> deleteComment(@PathVariable Long scheduleId, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(scheduleId, id, userDetails);

        ResponseMsg<Void> responseMsg = ResponseMsg.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("댓글이 삭제되었습니다.")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseMsg);
    }
}
