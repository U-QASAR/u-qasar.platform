package eu.uqasar.util.rules;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.inject.Inject;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.easyrules.core.AnnotatedRulesEngine;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.jboss.weld.context.http.Http;

import eu.uqasar.util.UQasarUtil;

/**
 * Timer service for interpreting and executing rules
 * 
 */
@NoArgsConstructor
@Stateless
public class RulesTimer {

	private final String RULES_TIMER_ABBR = "Rules-Timer";
	private final Logger logger = Logger.getLogger(RulesTimer.class);
	private final int timerInterval = 60; // default update interval for timer is 60 mins
	private static final AnnotatedRulesEngine rulesEngine = new AnnotatedRulesEngine();

	@Inject
	@Http
	private ConversationContext context;

	@Inject
	@Bound
	private BoundConversationContext boundContext;

	@Resource
	private TimerService timerService;

	/**
	 * Initialize the provided sample rules to be triggered when needed
	 */
	public void initRules() {
		// Register rules to the engine
		ProjectQualityRule projRule = new ProjectQualityRule();
		ThresholdReachedRule threshRule = new ThresholdReachedRule();
		MetricUpdateRule metRule = new MetricUpdateRule();
		rulesEngine.registerRule(projRule);
		rulesEngine.registerRule(threshRule);
		rulesEngine.registerRule(metRule);
		initRuleTimer();
	}

	/**
	 * Initialize the timers for all the adapters taking care of data updates
	 */
    private void initRuleTimer() {

		logger.debug("initRuleTimer() [" + new Date() + "]");

		// Cancel the existing timer if exists
		cancelTimer();

		// Convert the default value to ms
		Integer updateInterval = UQasarUtil.minsToMs(this.timerInterval);

		// Create a timer for executing rules
		timerService.createTimer(0, updateInterval, RULES_TIMER_ABBR);
	}

	/**
	 * Cancels the existing timer
	 */
    private void cancelTimer() {

		if (timerService.getTimers() != null
				&& timerService.getTimers().size() > 0) {
			logger.info("Cancelling U-QASAR rule timer...");

			for (Timer timer : timerService.getTimers()) {
				if (timer.getInfo().toString().contains(RULES_TIMER_ABBR)) {
					logger.info("Cancelling timer " + timer.getInfo());
					timer.cancel();
				}
			}
		}
	}

	/**
	 * Execute the defined rules
	 * 
	 * @param timer
	 */
	@Timeout
	public void executeRules(Timer timer) {

		logger.info("Executing the rules with timer "
				+ timer.getInfo().toString() + " at " + new Date());

		// simulate user interaction in order to get ConversationContext
		BoundRequest storage = null;
		try {
			if (!context.isActive() && !boundContext.isActive()) {
				Map<String, Object> session = new HashMap<>();
				Map<String, Object> request = new HashMap<>();
				storage = new MutableBoundRequest(request, session);
				boundContext.associate(storage);
				boundContext.activate();
			}

			// Get the rules' engine and fire the existing rules
			rulesEngine.fireRules();

		} catch (Exception e) {
			logger.error("Error when running rules", e);
		} finally {
			if (storage != null) {
				boundContext.deactivate();
				boundContext.dissociate(storage);
			}
		}

	}
}
