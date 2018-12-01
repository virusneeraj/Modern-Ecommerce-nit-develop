/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitsoft.ecommerce.service.book;

import com.nitsoft.ecommerce.database.model.book.Book;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *
 * @author acer
 */
public interface BookService {
    //get all Book
    Iterable<Book> findAllBook();
    //get Book by id
    Book getBookById(long companyId, long bookId);

    // get by company id
    Page<Book> getByCompanyId(long companyId, int pageNumber, int pageSize);
    //get by company id category id
    Page<Book> getByCompanyIdAndCategoryId(long companyId, long categoryId, int pageNumber, int pageSize);
    //get filter
    Page<Book> doFilterSearchSortPagingBook(long comId, long catId, long attrId, String searchKey, double mnPrice, double mxPrice, int minRank, int maxRank, int sortKey, boolean isAscSort, int pSize, int pNumber);
    //get list Book by id
    Iterable<Book> getBooksById(long companyId, List<Long> bookIds);
    //save product
    Book save(Book book);
    //update product
    Book update(Book book);

}
