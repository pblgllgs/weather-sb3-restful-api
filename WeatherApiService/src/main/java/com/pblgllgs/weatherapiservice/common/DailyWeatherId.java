package com.pblgllgs.weatherapiservice.common;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyWeatherId implements Serializable {

    private int dayOfMonth;
    private int month;

    @ManyToOne
    @JoinColumn(name = "location_code")
    private Location location;
}
