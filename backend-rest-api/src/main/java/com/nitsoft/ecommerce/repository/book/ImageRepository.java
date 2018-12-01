package com.nitsoft.ecommerce.repository.book;

import com.nitsoft.ecommerce.database.model.book.Image;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ImageRepository extends CrudRepository<Image, Long> {

    void deleteByBookId(long bookId);

    List<Image> findByBookId(long bookId);
}
