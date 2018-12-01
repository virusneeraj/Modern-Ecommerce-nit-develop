package com.nitsoft.ecommerce.service.book;

import com.nitsoft.ecommerce.database.model.book.Audience;
import com.nitsoft.ecommerce.database.model.book.Author;
import com.nitsoft.ecommerce.database.model.book.Book;
import com.nitsoft.ecommerce.database.model.book.Image;
import com.nitsoft.ecommerce.repository.book.AudienceRepository;
import com.nitsoft.ecommerce.repository.book.AuthorRepository;
import com.nitsoft.ecommerce.repository.book.BookRepository;
import com.nitsoft.ecommerce.repository.book.ImageRepository;
import com.nitsoft.ecommerce.repository.specification.BookSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private AudienceRepository audienceRepository;

    // currently this method is implement just for testing
    @Override
    public Iterable<Book> findAllBook() {
        Iterable<Book> books = bookRepository.findAll();
        for(Book book:books){
            book = loadChildTableData(book);
        }

        return books;
    }

    @Override
    public Book getBookById(long companyId, long productId) {
        Book book = bookRepository.findOne(productId);
        return loadChildTableData(book);
    }

//    @Override
//    public List<Object[]> getProductById(long productId) {
////        return bookRepository.findByProductId(productId);
//        return null;
//    }

    @Override
    public Page<Book> getByCompanyId(long companyId, int pageNumber, int pageSize) {
        Page<Book> booksPage = bookRepository.findByCompanyId(companyId, new PageRequest(pageNumber, pageSize));
        final Page<Book> booksPageBooks = booksPage.map(this::loadChildTableData);
        return booksPageBooks;
    }

    @Override
    public Page<Book> getByCompanyIdAndCategoryId(long companyId, long categoryId, int pageNumber, int pageSize) {
        Page<Book> booksPage = bookRepository.findByCompanyId(companyId, new PageRequest(pageNumber, pageSize));
        final Page<Book> booksPageBooks = booksPage.map(this::loadChildTableData);
        return booksPageBooks;
        //return null;
        //return bookRepository.findByCompanyId(companyId, new PageRequest(pageNumber, pageSize));
        //return bookRepository.findByCategoryId(companyId, categoryId, new PageRequest(pageNumber, pageSize));
    }

    @Override
    public Page<Book> doFilterSearchSortPagingBook(long comId, long catId, long attrId, String searchKey, double mnPrice, double mxPrice, int minRank, int maxRank, int sortKey, boolean isAscSort, int pSize, int pNumber) {
        Page<Book> booksPage = bookRepository.findAll(new BookSpecification(comId, catId, attrId, searchKey, mnPrice, mxPrice, minRank, maxRank, sortKey, isAscSort), new PageRequest(pNumber, pSize));
        final Page<Book> booksPageBooks = booksPage.map(this::loadChildTableData);
        return booksPageBooks;
        //return bookRepository.findAll(new BookSpecification(comId, catId, attrId, searchKey, mnPrice, mxPrice, minRank, maxRank, sortKey, isAscSort), new PageRequest(pNumber, pSize));
    }

    @Override
    public Iterable<Book> getBooksById(long companyId, List<Long> BookIds) {
        Iterable<Book> books = bookRepository.findByBookIds(companyId, BookIds);
        for(Book book:books){
            book = loadChildTableData(book);
        }

        return books;
        //return bookRepository.findByBookIds(companyId, BookIds);
    }

    @Override
    public Book save(Book book) {
        book.setBookId(null);
        Book book1 = bookRepository.save(book);
        book.setBookId(book1.getBookId());
        saveChildTableData(book);
        return book1;
    }

    @Override
    public Book update(Book book) {
        saveChildTableData(book);
        return bookRepository.save(book);
    }


    public boolean saveChildTableData(Book book){
        long bookId = book.getBookId();

        if(book != null){
            //delete auhtor of book and insert
            if(book.getAuthors() != null){
                authorRepository.deleteByBookId(bookId);
                for(String authorStr: book.getAuthors()){
                    authorRepository.save(bookId, authorStr);
                }
            }

            //delete audience of book and insert
            if(book.getTargetAudienceBases() != null){
                audienceRepository.deleteByBookId(bookId);
                for(String audienceStr: book.getTargetAudienceBases()){
                    audienceRepository.save(bookId, audienceStr);
                }
            }

            //delete imges of book and insert
            if(book.getImages() != null){
                imageRepository.deleteByBookId(bookId);
                for(Image image: book.getImages()){
                    image.setBookId(bookId);
                    imageRepository.save(image);
                }
            }
        }
        return true;
    }

    public Book loadChildTableData(Book book) {
        long bookId = book.getBookId();
        List<String> stringList = null;

        if (book != null) {
            //get author data
            List<Author> authors = authorRepository.findByBookId(bookId);
            stringList = new ArrayList();
            for(Author author: authors){
                stringList.add(author.getName());
            }
            book.setAuthors(stringList);

            //get audience data
            List<Audience> audiences = audienceRepository.findByBookId(bookId);
            stringList = new ArrayList();
            for(Audience audience: audiences){
                stringList.add(audience.getName());
            }
            book.setTargetAudienceBases(stringList);

            //get images data
            List<Image> imageList = imageRepository.findByBookId(bookId);
            book.setImages(imageList);
        }

        return book;
    }


}
