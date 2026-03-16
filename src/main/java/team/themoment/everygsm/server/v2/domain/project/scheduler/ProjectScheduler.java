package team.themoment.everygsm.server.v2.domain.project.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.themoment.everygsm.server.v2.domain.project.service.SyncProjectService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectScheduler {

    private final SyncProjectService syncProjectService;

    @Scheduled(cron = "${spring.cloud.datagsm.openapi.sync-cron}")
    public void syncProjects() {
        log.info("Starting project sync...");
        try {
            syncProjectService.execute();
            log.info("Project sync completed.");
        } catch (Exception e) {
            log.error("Project sync failed", e);
        }
    }
}
