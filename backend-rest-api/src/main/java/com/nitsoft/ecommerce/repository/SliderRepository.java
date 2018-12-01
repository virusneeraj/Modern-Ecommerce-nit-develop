package com.nitsoft.ecommerce.repository;

import com.nitsoft.ecommerce.database.model.Slider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SliderRepository extends PagingAndSortingRepository<Slider, Long>, JpaSpecificationExecutor<Slider> {
    List<Slider> findByCompanyId(@Param("companyId") long companyId);
    //Slider findByCompanyIdAAndSliderId(@Param("companyId") long companyId,@Param("sliderId") long sliderId);
}
