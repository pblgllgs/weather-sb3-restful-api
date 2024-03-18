package com.pblgllgs.weatherapiservice.full;
/*
 *
 * @author pblgl
 * Created on 18-03-2024
 *
 */

import com.pblgllgs.weatherapiservice.GeolocationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FullWeatherModelAssembler implements RepresentationModelAssembler<FullWeatherDTO, EntityModel<FullWeatherDTO>> {
    @Override
    public EntityModel<FullWeatherDTO> toModel(FullWeatherDTO dto) {
        EntityModel<FullWeatherDTO> entityModel = EntityModel.of(dto);
        try {
            entityModel.add(
                    linkTo(
                            methodOn(FullWeatherApiController.class)
                                    .getFullWeatherByIPAddress(null)
                    ).withSelfRel());
        } catch (GeolocationException e) {
            try {
                throw new GeolocationException(e.getMessage());
            } catch (GeolocationException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return entityModel;
    }
}
