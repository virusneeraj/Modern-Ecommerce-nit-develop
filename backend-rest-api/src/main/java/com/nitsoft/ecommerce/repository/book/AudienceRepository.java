package com.nitsoft.ecommerce.repository.book;

import com.nitsoft.ecommerce.database.model.book.Audience;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AudienceRepository extends CrudRepository<Audience, Long> {
    @Modifying
    @Query(value = "insert into Audience (book_id,name) VALUES (:book_id,:name)", nativeQuery = true)
    @Transactional
    void save(@Param("book_id") long book_id, @Param("name") String name);

    void deleteByBookId(long bookId);

    List<Audience> findByBookId(long bookId);
}
