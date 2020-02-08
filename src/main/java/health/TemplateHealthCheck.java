package health;

import com.codahale.metrics.health.HealthCheck;

public class TemplateHealthCheck extends HealthCheck {
    private final String appName;

    public TemplateHealthCheck(String template) {
        this.appName = template;
    }

    @Override
    protected Result check() throws Exception {
        final String saying = String.format(appName, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }
}
