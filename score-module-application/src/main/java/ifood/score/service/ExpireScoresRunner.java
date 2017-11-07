package ifood.score.service;

import org.springframework.scheduling.annotation.Scheduled;

public class ExpireScoresRunner {

    private final ScoreService scoreService;

    public ExpireScoresRunner(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @Scheduled(cron = "0 10 0 1/1 * ?")// once at a day at 12:10 AM
    public void run() {
        scoreService.expireScores();
    }
}
