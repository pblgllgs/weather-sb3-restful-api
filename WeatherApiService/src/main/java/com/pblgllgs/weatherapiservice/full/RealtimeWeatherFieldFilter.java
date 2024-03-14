package com.pblgllgs.weatherapiservice.full;
/*
 *
 * @author pblgl
 * Created on 14-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.realtime.RealtimeWeatherDTO;

public class RealtimeWeatherFieldFilter {
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RealtimeWeatherDTO dto) {
            return dto.getStatus() == null;
        }
        return false;
    }
}
