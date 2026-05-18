package team.themoment.everygsm.server.v2.domain.project.event;

import java.util.Set;

public record ProjectRegisteredEvent(String projectTitle, String affiliation, String description, int startYear,
        String prodUrl, Set<String> stackNames, Set<String> repoUrls, String userName, String studentNumber,
        String userEmail) {

    public ProjectRegisteredEvent {
        stackNames = stackNames == null ? Set.of() : Set.copyOf(stackNames);
        repoUrls = repoUrls == null ? Set.of() : Set.copyOf(repoUrls);
    }
}
