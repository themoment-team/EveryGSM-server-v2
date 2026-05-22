package team.themoment.everygsm.server.v2.domain.user.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import team.themoment.everygsm.server.v2.domain.user.dto.response.UserSearchResDto;
import team.themoment.everygsm.server.v2.domain.user.dto.response.UserSummaryDto;
import team.themoment.everygsm.server.v2.domain.user.repository.UserRepository;
import team.themoment.everygsm.server.v2.global.exception.error.ExpectedException;

@Service
@RequiredArgsConstructor
public class SearchUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserSearchResDto execute(String name) {
        if (name == null || name.isBlank()) {
            throw new ExpectedException("검색어를 입력해주세요.", HttpStatus.BAD_REQUEST);
        }

        List<UserSummaryDto> users = userRepository.findTop20ByNameStartingWithOrderByNameAsc(name).stream()
                .map(u -> new UserSummaryDto(u.getId(), u.getName(), u.getStudentNumber())).toList();
        return new UserSearchResDto(users);
    }
}
