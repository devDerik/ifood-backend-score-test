package ifood.score.service.controller;

import ifood.score.controller.ScoreController;
import ifood.score.model.MenuItemScore;
import ifood.score.service.ScoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ScoreController.class)
public class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ScoreService scoreServiceMock;

    @Test
    public void getMenuScoreTest() throws Exception {
        final UUID menuId = UUID.randomUUID();
        when(scoreServiceMock.findMenuScoreById(menuId.toString())).thenReturn(new MenuItemScore(menuId, BigDecimal.TEN));

        this.mockMvc.perform(get(String.format("/menu/%s/score", menuId.toString())))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("derik")));
    }
}
