/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.slurry.cache4guice;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.slurry.cache4guice.annotation.Cached;
import org.slurry.cache4guice.annotation.SpecialConfig;

/**
 *
 * @author mhoum
 */
public class CalculatorMap {

    private static Logger logger = Logger.getLogger(CalculatorMap.class);
    
    public final static String specialCacheTimeOut="org.slurry.cache4guice-refreshTime";
    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    
    @SpecialConfig(cacheConfigurationName = specialCacheTimeOut)
    @Cached(SelfPopulatingScheduledCache = true, refreshTime = 10000)
    public String getAddNumberToAOldMap(Integer id, Integer addNumber){
        logger.info("**** calling getAddNumberToAOldMap id>"+id+"< addNumber>"+addNumber+"<");
        Integer oldNumber = 0;
        if(map.containsKey(id)){
            oldNumber = map.get(id);
        }
        logger.debug("map size >"+map.size()+"<");
        logger.debug("oldNumber >"+oldNumber+"<");
        oldNumber += addNumber;
        map.put(id, oldNumber);
        final String result = id +"-"+ oldNumber;
        logger.info("**** finished calling  getAddNumberToAOldMap id>"+id+"< addNumber>"+addNumber+"< result>"+result+"<");
        return result;
    }
}
