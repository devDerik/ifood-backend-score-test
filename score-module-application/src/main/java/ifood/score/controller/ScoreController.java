package ifood.score.controller;

import ifood.score.exception.ScoreServiceException;
import ifood.score.exception.ScoreServiceNoContentException;
import ifood.score.model.CategoryScore;
import ifood.score.model.MenuItemScore;
import ifood.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScoreController {

    private final ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/menu/{menuId}/score")
    public MenuItemScore getMenuScore(@PathVariable String menuId) throws ScoreServiceException {
        return scoreService.findMenuScoreById(menuId);
    }

    @GetMapping("/category/{categoryType}/score")
    public CategoryScore getCategoryScore(@PathVariable String categoryType) throws ScoreServiceException {
        return scoreService.findCategoryScoreByType(categoryType);
    }

    @GetMapping("/menu/score")
    public List<MenuItemScore> getMenuScoreBetweenMinMax(@RequestParam(value = "min", defaultValue = "0") String min,
                                                 @RequestParam(value = "max", defaultValue = "100") String max) throws ScoreServiceException, ScoreServiceNoContentException {
        return scoreService.findMenuScoreBetweenMinMax(min, max);
    }

    @GetMapping("/category/score")
    public List<CategoryScore> getCategoryScoreBetweenMinMax(@RequestParam(value = "min", defaultValue = "0") String min,
                                                 @RequestParam(value = "max", defaultValue = "100") String max) throws ScoreServiceException, ScoreServiceNoContentException {
        return scoreService.findCategoryScoreBetweenMinMax(min, max);
    }

    @GetMapping("/categoryAll")
    public List<CategoryScore> getAllCategoryScore() {
        return scoreService.getAllCategoryScore();
    }

    @GetMapping("/menuAll")
    public List<MenuItemScore> getAllMenuScore() {
        return scoreService.getAllMenuScore();
    }
}
