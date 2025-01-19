package mindpath.core.rest;

import mindpath.config.APIRouters;
import mindpath.core.domain.stats.Stats;
import mindpath.core.service.stats.StatsService;
import mindpath.core.utility.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APIRouters.STATS_ROUTER)
public class StatsController {


    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }


    @GetMapping()
    public CustomerResponse<Stats> getStats(){
        return new CustomerResponse<>(statsService.getStats(), HttpStatus.OK);
    }
}
