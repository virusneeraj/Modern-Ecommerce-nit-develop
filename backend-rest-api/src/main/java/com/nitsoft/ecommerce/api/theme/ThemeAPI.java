/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitsoft.ecommerce.api.theme;

import com.nitsoft.ecommerce.api.APIName;
import com.nitsoft.ecommerce.api.controller.AbstractBaseController;
import com.nitsoft.ecommerce.api.response.model.APIResponse;
import com.nitsoft.ecommerce.database.model.Slider;
import com.nitsoft.ecommerce.repository.SliderRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Theme-API")
@RestController
@RequestMapping("/api/v1/{company_id}"+APIName.THEME_API)
public class ThemeAPI extends AbstractBaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SliderRepository sliderRepository;

    @ApiOperation(value = "get slider data by company id", notes = "")
    @RequestMapping(path = APIName.SLIDER_DATA, method = RequestMethod.GET, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> loadSlider(@PathVariable("company_id") Long companyId) {
        List<Slider> sliderData = sliderRepository.findByCompanyId(companyId);
        return responseUtil.successResponse(sliderData);
    }

    /*@ApiOperation(value = "get slider data by id", notes = "")
    @RequestMapping(path = APIName.GET_SLIDER, method = RequestMethod.GET, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> getSlider(
            @PathVariable("company_id") Long companyId,
            @PathVariable("slider_id") Long sliderId) {
        Slider sliderData = sliderRepository.findByCompanyIdAAndSliderId(companyId, sliderId);
        return responseUtil.successResponse(sliderData);
    }*/

    @ApiOperation(value = "insert/create slider data", notes = "")
    @RequestMapping(path = APIName.CREATE_SLIDER, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> createSlider(@PathVariable("company_id") Long companyId, @RequestBody Slider slider) {
        slider.setCompanyId(companyId);
        return responseUtil.successResponse(sliderRepository.save(slider));
    }

    @ApiOperation(value = "update slider data", notes = "")
    @RequestMapping(path = APIName.UPDATE_SLIDER, method = RequestMethod.PUT, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> updateSlider(@PathVariable("company_id") Long companyId, @RequestBody Slider slider) {
        sliderRepository.delete(slider.getSliderId());
        return responseUtil.successResponse(sliderRepository.save(slider));
    }

    /*@ApiOperation(value = "delete slider data by id", notes = "")
    @RequestMapping(path = APIName.DELETE_SLIDER, method = RequestMethod.DELETE, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> deleteSlider(
            @PathVariable("company_id") Long companyId,
            @PathVariable("slider_id") Long sliderId) {
        Slider sliderData = sliderRepository.findByCompanyIdAAndSliderId(companyId, sliderId);

        if (sliderData != null)
            sliderRepository.delete(sliderData.getSliderId());

        return responseUtil.successResponse(sliderData);
    }*/


}
