package org.crochet.subscription.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.subscription.service.SubscriptionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    /**
     * Scheduled task to process subscription renewals.
     * Runs daily at midnight (00:00:00)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processSubscriptionRenewals() {
        log.info("Starting scheduled task: Processing subscription renewals");
        try {
            subscriptionService.processRenewals();
            log.info("Completed subscription renewals processing");
        } catch (Exception e) {
            log.error("Error processing subscription renewals", e);
        }
    }

    /**
     * Scheduled task to process subscription expirations.
     * Runs daily at 1 AM (01:00:00)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processSubscriptionExpirations() {
        log.info("Starting scheduled task: Processing subscription expirations");
        try {
            subscriptionService.processExpirations();
            log.info("Completed subscription expirations processing");
        } catch (Exception e) {
            log.error("Error processing subscription expirations", e);
        }
    }
}