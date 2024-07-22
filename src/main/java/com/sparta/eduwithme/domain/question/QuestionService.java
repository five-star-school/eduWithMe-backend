package com.sparta.eduwithme.domain.question;

import com.sparta.eduwithme.common.exception.CustomException;
import com.sparta.eduwithme.common.exception.ErrorCode;
import com.sparta.eduwithme.domain.question.dto.AnswerRequestDTO;
import com.sparta.eduwithme.domain.question.dto.QuestionRequestDTO;
import com.sparta.eduwithme.domain.question.dto.QuestionResponseDTO;
import com.sparta.eduwithme.domain.question.entity.Answer;
import com.sparta.eduwithme.domain.question.entity.Question;
import com.sparta.eduwithme.domain.room.RoomRepository;
import com.sparta.eduwithme.domain.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public QuestionResponseDTO createQuestion(Long roomId, QuestionRequestDTO requestDTO) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        Question question = new Question(room, requestDTO);

        for (AnswerRequestDTO answerRequestDTO : requestDTO.getAnswerList()) {
            Answer answer = new Answer(answerRequestDTO);

            question.addAnswer(answer);
        }
        questionRepository.save(question);
        return new QuestionResponseDTO(question);

    }
}
