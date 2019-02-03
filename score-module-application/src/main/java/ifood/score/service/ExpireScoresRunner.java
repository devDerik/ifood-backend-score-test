package ifood.score.service;

import org.springframework.scheduling.annotation.Scheduled;

public class ExpireScoresRunner {

    private final ScoreService scoreService;

    public ExpireScoresRunner(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Scheduled(cron = "0 0 0/1 1/1 * ?")// every 1 hours
    public void run() {
        scoreService.expireScores();
    }
}
