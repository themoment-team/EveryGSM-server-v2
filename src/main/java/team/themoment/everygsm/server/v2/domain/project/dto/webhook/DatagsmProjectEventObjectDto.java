package team.themoment.everygsm.server.v2.domain.project.dto.webhook;

import java.util.List;

public record DatagsmProjectEventObjectDto(Long id, String name, String description, Integer startYear, Integer endYear,
        String status, DatagsmProjectEventClubDto club, List<DatagsmProjectEventParticipantDto> participants) {
}
