/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.slurry.cache4guice;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.annotation.SpecialConfig;

/**
 *
 * @author mhoum
 */
public class CalculatorMap {

    private static Logger logger = Logger.getLogger(CalculatorMap.class);

    
    public void totalReset(){
        resetAddNumberToAOldMap();
        resetCalculateAddNumberSleep();
    }
    
    public final static String specialCacheTimeOut = "org.slurry.cache4guice-refreshTime";
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();

    public void resetAddNumberToAOldMap() {
        map.clear();
    }

    @SpecialConfig(cacheConfigurationName = specialCacheTimeOut)
    @Cached(SelfPopulatingScheduledCache = true, refreshTime = 10000, defaultClassToReturn = String.class)
    public String getAddNumberToAOldMap(Integer id, Integer addNumber) {
        logger.info("**** calling getAddNumberToAOldMap id>" + id + "< addNumber>" + addNumber + "<");
        Integer oldNumber = 0;
        if (map.containsKey(id)) {
            oldNumber = map.get(id);
        }
        logger.debug("map size >" + map.size() + "<");
        logger.debug("oldNumber >" + oldNumber + "<");
        oldNumber += addNumber;
        map.put(id, oldNumber);
        final String result = id + "-" + oldNumber;
        logger.info("**** finished calling  getAddNumberToAOldMap id>" + id + "< addNumber>" + addNumber + "< result>" + result + "<");
        return result;
    }

    Map<Integer, Integer> mapForCalculateAddNumberRandomDelayOnActiv = new HashMap<Integer, Integer>();

    public void resetCalculateAddNumberSleep() {
        mapForCalculateAddNumberRandomDelayOnActiv.clear();
    }

    @SpecialConfig(cacheConfigurationName = specialCacheTimeOut)
    @Cached(SelfPopulatingScheduledCache = true, refreshTime = 20000, defaultClassToReturn = String.class)
    public String calculateAddNumberSleep(Integer id, Integer timeToSleep) {
        logger.info("**** calling calculateAddNumberSleep id>" + id + "<");
        try {
            logger.info("**** calculateAddNumberSleep Sleep id>" + id + "< timeToSleep>" + timeToSleep + "<");
            Thread.sleep(timeToSleep);
        } catch (InterruptedException ex) {
            logger.error(ex);
        }
        Integer oldNumber = 0;
        if (mapForCalculateAddNumberRandomDelayOnActiv.containsKey(id)) {
            oldNumber = mapForCalculateAddNumberRandomDelayOnActiv.get(id);
        }
        logger.debug("map size >" + mapForCalculateAddNumberRandomDelayOnActiv.size() + "<");
        logger.debug("oldNumber >" + oldNumber + "<");
        oldNumber += 1;
        mapForCalculateAddNumberRandomDelayOnActiv.put(id, oldNumber);
        final String result = id + "-" + oldNumber;
        logger.info("**** finished calling  calculateAddNumberSleep id>" + id + "< result>" + result + "<");
        return result;
    }
}
