package com.pblgllgs.weatherapiservice.full;
/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;

public class RealtimeWeatherFieldFilter {
    public boolean equals(Object object) {
        if (object instanceof RealtimeWeatherDTO) {
            RealtimeWeatherDTO dto = (RealtimeWeatherDTO) object;
            return dto.getStatus() == null;
        }
        return false;
    }
}
