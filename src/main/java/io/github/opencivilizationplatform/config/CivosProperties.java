package io.github.opencivilizationplatform.config;

import io.github.opencivilizationplatform.authorization.Role;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "civos")
public class CivosProperties {

    private EventBus eventbus = new EventBus();
    private Worker worker = new Worker();
    private Game game = new Game();
    private Auth auth = new Auth();
    private Security security = new Security();
    private Seed seed = new Seed();

    public EventBus getEventbus() { return eventbus; }
    public void setEventbus(EventBus eventbus) { this.eventbus = eventbus; }
    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }
    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }
    public Seed getSeed() { return seed; }
    public void setSeed(Seed seed) { this.seed = seed; }

    public static class EventBus {
        private String type = "spring";

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    public static class Worker {
        private String remoteUrl = "";

        public String getRemoteUrl() { return remoteUrl; }
        public void setRemoteUrl(String remoteUrl) { this.remoteUrl = remoteUrl; }
    }

    public static class Game {
        private Events events = new Events();
        private Trade trade = new Trade();
        private Cortex cortex = new Cortex();

        public Events getEvents() { return events; }
        public void setEvents(Events events) { this.events = events; }
        public Trade getTrade() { return trade; }
        public void setTrade(Trade trade) { this.trade = trade; }
        public Cortex getCortex() { return cortex; }
        public void setCortex(Cortex cortex) { this.cortex = cortex; }

        public static class Events {
            private boolean enabled = true;
            private long tickRateMs = 30000;
            public boolean isEnabled() { return enabled; }
            public void setEnabled(boolean enabled) { this.enabled = enabled; }
            public long getTickRateMs() { return tickRateMs; }
            public void setTickRateMs(long tickRateMs) { this.tickRateMs = tickRateMs; }
        }

        public static class Trade {
            private int maxActive = 10;
            private int autoResolveDays = 7;
            public int getMaxActive() { return maxActive; }
            public void setMaxActive(int maxActive) { this.maxActive = maxActive; }
            public int getAutoResolveDays() { return autoResolveDays; }
            public void setAutoResolveDays(int autoResolveDays) { this.autoResolveDays = autoResolveDays; }
        }

        public static class Cortex {
            private long tickRateMs = 30000;
            public long getTickRateMs() { return tickRateMs; }
            public void setTickRateMs(long tickRateMs) { this.tickRateMs = tickRateMs; }
        }
    }

    public static class Auth {
        private long expirationMs = 86400000;
        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }

    public static class Security {
        private Map<String, String> roleMappings = Map.of();
        public Map<String, String> getRoleMappings() { return roleMappings; }
        public void setRoleMappings(Map<String, String> roleMappings) { this.roleMappings = roleMappings; }
    }

    public static class Seed {
        private boolean enabled = true;
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}
