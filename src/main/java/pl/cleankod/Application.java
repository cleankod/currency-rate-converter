package pl.cleankod;

import org.codejargon.feather.Feather;
import pl.cleankod.exchange.entrypoint.AccountController;
import spark.servlet.SparkApplication;

public class Application implements SparkApplication {

    private ContextHolder context;

    @Override
    public void init() {
        context = ContextHolder.CONTEXT;
    }

    private enum ContextHolder {
        CONTEXT;

        ContextHolder() {
            Feather feather = Feather.with(
                    new ApplicationConfiguration()
            );
            feather.instance(AccountController.class);
        }
    }
}
