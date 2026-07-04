package io.github.opencivilizationplatform.modules.trade.application;

import io.github.opencivilizationplatform.modules.civilization.domain.Civilization;
import io.github.opencivilizationplatform.modules.civilization.infrastructure.CivilizationRepository;
import io.github.opencivilizationplatform.modules.events.domain.GlobalEvent;
import io.github.opencivilizationplatform.modules.events.infrastructure.GlobalEventRepository;
import io.github.opencivilizationplatform.modules.nexus.domain.MeshTrade;
import io.github.opencivilizationplatform.modules.nexus.infrastructure.MeshTradeRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class MarketPriceService {

    private final StringRedisTemplate redisTemplate;
    private final CivilizationRepository civilizationRepository;
    private final MeshTradeRepository meshTradeRepository;
    private final GlobalEventRepository globalEventRepository;

    private static final String PRICE_KEY_PREFIX = "market:price:";
    private static final String HISTORY_KEY_PREFIX = "market:history:";
    private static final double BASE_PRICE = 10.0;

    public MarketPriceService(StringRedisTemplate redisTemplate,
                              CivilizationRepository civilizationRepository,
                              MeshTradeRepository meshTradeRepository,
                              GlobalEventRepository globalEventRepository) {
        this.redisTemplate = redisTemplate;
        this.civilizationRepository = civilizationRepository;
        this.meshTradeRepository = meshTradeRepository;
        this.globalEventRepository = globalEventRepository;
    }

    public double getCurrentPrice(String resourceType) {
        String key = PRICE_KEY_PREFIX + resourceType.toLowerCase();
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) {
            updatePrices();
            val = redisTemplate.opsForValue().get(key);
        }
        return val != null ? Double.parseDouble(val) : BASE_PRICE;
    }

    public Map<String, Double> getAllPrices() {
        Map<String, Double> prices = new HashMap<>();
        String[] types = {"food", "water", "mineral", "energy", "housing"};
        for (String type : types) {
            prices.put(type.toUpperCase(), getCurrentPrice(type));
        }
        return prices;
    }

    public List<String> getPriceHistory(String resourceType) {
        String key = HISTORY_KEY_PREFIX + resourceType.toLowerCase();
        return redisTemplate.opsForList().range(key, 0, 20);
    }

    public synchronized void updatePrices() {
        List<Civilization> civs = civilizationRepository.findAll();
        List<GlobalEvent> activeEvents = globalEventRepository.findByActiveTrue();
        
        long recentBarters = 0;
        try {
            recentBarters = meshTradeRepository.count();
        } catch (Exception e) {
            // handle case if repo isn't populated
        }

        String[] types = {"food", "water", "mineral", "energy", "housing"};
        for (String type : types) {
            double price = BASE_PRICE;

            // 1. Scarcity demand
            long scarcityCount = civs.stream().filter(c -> {
                if ("food".equals(type)) return (c.getFood() != null && c.getFood() < 30.0);
                if ("water".equals(type)) return (c.getWater() != null && c.getWater() < 30.0);
                if ("mineral".equals(type)) return (c.getMinerals() != null && c.getMinerals() < 30.0);
                if ("energy".equals(type)) return (c.getEnergy() != null && c.getEnergy() < 30.0);
                if ("housing".equals(type)) return (c.getHousing() != null && c.getHousing() < 30.0);
                return false;
            }).count();
            price += scarcityCount * 2.0;

            // 2. Barter volume demand
            price += (recentBarters % 5) * 0.5;

            // 3. World events modifiers
            for (GlobalEvent event : activeEvents) {
                if (event.getType() != null) {
                    String evType = event.getType().name();
                    if ("DROUGHT".equals(evType) && ("food".equals(type) || "water".equals(type))) {
                        price += 8.0;
                    } else if ("PANDEMIC".equals(evType) && ("food".equals(type) || "water".equals(type))) {
                        price += 5.0;
                    } else if ("SOLAR_FLARE".equals(evType) && "energy".equals(type)) {
                        price += 10.0;
                    } else if ("VOLCANIC_ERUPTION".equals(evType) && "mineral".equals(type)) {
                        price += 6.0;
                    } else if ("RESOURCE_BOUNTY".equals(evType)) {
                        price -= 4.0;
                    }
                }
            }

            // Ensure price is positive
            if (price < 2.0) {
                price = 2.0;
            }

            // Save current price to Redis
            String priceKey = PRICE_KEY_PREFIX + type;
            redisTemplate.opsForValue().set(priceKey, String.format(java.util.Locale.ROOT, "%.2f", price));
            redisTemplate.expire(priceKey, 5, TimeUnit.MINUTES);

            // Push to history list (max 20 records)
            String historyKey = HISTORY_KEY_PREFIX + type;
            redisTemplate.opsForList().rightPush(historyKey, String.format(java.util.Locale.ROOT, "%.2f", price));
            Long size = redisTemplate.opsForList().size(historyKey);
            if (size != null && size > 20) {
                redisTemplate.opsForList().leftPop(historyKey);
            }
        }
    }
}
