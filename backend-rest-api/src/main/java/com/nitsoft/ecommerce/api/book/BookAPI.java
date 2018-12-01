package com.nitsoft.ecommerce.api.book;

import com.nitsoft.ecommerce.api.APIName;
import com.nitsoft.ecommerce.api.controller.AbstractBaseController;
import com.nitsoft.ecommerce.api.request.model.ListBookModel;
import com.nitsoft.ecommerce.api.response.model.APIResponse;
import com.nitsoft.ecommerce.api.response.model.PagingResponseModel;
import com.nitsoft.ecommerce.api.response.util.APIStatus;
import com.nitsoft.ecommerce.database.model.book.Book;
import com.nitsoft.ecommerce.database.model.Category;
import com.nitsoft.ecommerce.database.model.ProductCategory;
import com.nitsoft.ecommerce.database.model.ProductCategoryId;
import com.nitsoft.ecommerce.exception.ApplicationException;
import com.nitsoft.ecommerce.repository.CategoryRepository;
import com.nitsoft.ecommerce.repository.ProductCategoryRepository;
import com.nitsoft.ecommerce.service.book.BookServiceImpl;
import com.nitsoft.util.Constant;
import com.nitsoft.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Api(value = "books")
@RestController
@RequestMapping(APIName.BOOKS)
public class BookAPI extends AbstractBaseController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @ApiOperation(value = "get book by company id", notes = "")
    @RequestMapping(method = RequestMethod.GET, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> getAllBooks(
            @PathVariable("company_id") Long companyId,
            @RequestParam(required = false, defaultValue = Constant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(required = false, defaultValue = Constant.DEFAULT_PAGE_SIZE) Integer pageSize) {

        Page<Book> books = bookService.getByCompanyId(companyId, pageNumber, pageSize);

        return responseUtil.successResponse(books.getContent());
    }

    @ApiOperation(value = "get books by product id", notes = "")
    @RequestMapping(path = APIName.BOOK_BY_ID, method = RequestMethod.GET, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> geBookById(HttpServletRequest request,
                                                  @PathVariable Long book_id,
                                                  @PathVariable Long company_id) {
        logger.info("id company : " + company_id.toString());
        // get product
        Book b = bookService.getBookById(company_id, book_id);
        if (b != null) {
            // get all attributes of product
//        ProductAttributeDetail pad = productAttributeService.findByProductIdAndAttributeId(product_Id, Constant.PRODUCT_ATTRIBUTE.DETAIL_IMAGES.getId());
            List<ProductCategory> listBookCate = productCategoryRepository.getProCateByProductId(book_id);
            List<Map<String, Object>> listCate = new ArrayList<Map<String, Object>>();
            for (ProductCategory result : listBookCate) {
                Map<String, Object> category = new HashMap();
                //find category name with categoryId
                Category cate = categoryRepository.findByCategoryId(result.getId().getCategoryId());
                if (cate != null) {
                    category.put("text", cate.getName());
                    category.put("id", cate.getCategoryId());
                }
                //add category name to list String
                listCate.add(category);
            }
            Map<String, Object> result = new HashMap();
            result.put("product", b);
            result.put("list_category", listCate);

            return responseUtil.successResponse(result);
        } else {
            throw new ApplicationException(APIStatus.GET_PRODUCT_ERROR);
        }
    }

    @ApiOperation(value = "get list book by book ids", notes = "")
    @RequestMapping(path = APIName.BOOK_BY_IDS, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> getListBookByIds(
            @PathVariable Long companyId,
            @RequestBody List<Long> bookIds) {

        if (bookIds != null && !bookIds.isEmpty()) {
            List<Book> books = (List<Book>) bookService.getBooksById(companyId, bookIds);
            if (books != null) {
//                statusResponse = new StatusResponse(APIStatus.OK.getCode(), products, products.size());
                return responseUtil.successResponse(books);
            } else {
                throw new ApplicationException(APIStatus.INVALID_PARAMETER);
            }
        } else {
            throw new ApplicationException(APIStatus.INVALID_PARAMETER);
        }

//        return writeObjectToJson(statusResponse);
    }

    @ApiOperation(value = "get books by category id", notes = "")
    @RequestMapping(value = APIName.BOOKS_BY_CATEGORY, method = RequestMethod.GET, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> getBookByCategoryId(
            @PathVariable("company_id") Long companyId,
            @RequestParam(required = false, name = "category_id", defaultValue = "1") Long categoryId,
            @RequestParam(required = false, defaultValue = Constant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(required = false, defaultValue = Constant.DEFAULT_PAGE_SIZE) Integer pageSize) {

        Page<Book> books = bookService.getByCompanyIdAndCategoryId(companyId, categoryId, pageNumber, pageSize);
//        return writeObjectToJson(new StatusResponse(APIStatus.OK.getCode(), products.getContent(), products.getTotalElements()));
        return responseUtil.successResponse(books.getContent());
    }

    @ApiOperation(value = "filter book list", notes = "")
    @RequestMapping(value = APIName.BOOKS_FILTER_LIST, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> getProductFilterList(HttpServletRequest request,
                                                            @RequestBody ListBookModel listBook) {
        logger.info(APIName.PRODUCTS_FILTER_LIST + " listBook: " + listBook.toString());
        try {
            Page<Book> books = bookService.doFilterSearchSortPagingBook(listBook.getCompanyId(), listBook.getCategoryId(), listBook.getAttributeId(), listBook.getSearchKey(), listBook.getMinPrice(), listBook.getMaxPrice(), listBook.getMinRank(), listBook.getMaxRank(), listBook.getSortCase(), listBook.getAscSort(), listBook.getPageSize(), listBook.getPageNumber());
            PagingResponseModel finalRes = new PagingResponseModel(books.getContent(), books.getTotalElements(), books.getTotalPages(), books.getNumber());
            return responseUtil.successResponse(finalRes);
            } catch (Exception ex) {
            logger.error("filter book",ex);
            throw new ApplicationException(APIStatus.GET_LIST_PRODUCT_ERROR);
        }
    }

    @ApiOperation(value = "create batch books", notes = "")
    @RequestMapping(value = APIName.BOOK_BATCH_CREATE, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> createProducts(HttpServletRequest request,
                                                     @RequestBody List<Book> bookList) {
        logger.info("Create books request received bookList: " + bookList);
        List<Book> books = new ArrayList<>();
        for(Book book: bookList){
            try {
                if(book.getDescription() != null && book.getDescription().length()> 200)
                    book.setDescription(StringUtils.abbreviate(book.getDescription(), 200) );
                if (book.getCompanyId() == null || book.getCompanyId() <= 0)
                    book.setCompanyId((long) 1);
                if(book.getOverview() == null || book.getOverview().isEmpty())
                    book.setOverview(book.getDescription());
                if(book.getSku() == null || book.getSku().isEmpty())
                    book.setSku("PSC");
                ResponseEntity<APIResponse> responseEntity = createProduct(request, book);
                books.add((Book) responseEntity.getBody().getData());
            }catch (Exception e){
                logger.error("create batvch product ",e);
            }
        }
        return responseUtil.successResponse(books);
    }

    @ApiOperation(value = "create book", notes = "")
    @RequestMapping(value = APIName.BOOK_CREATE, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> createProduct(HttpServletRequest request,
                                                     @RequestBody Book book) {
        logger.info("Create book request received Book: " + book);
        try {
            book.setCreatedOn(new Date());
            book.setStatus(Constant.STATUS.ACTIVE_STATUS.getValue());
            book.setUpdatedOn(new Date());
            //create product
            bookService.save(book);
            //create product categories
            if (book.getListCategoriesId() != null) {
                for (Long categoriesId : book.getListCategoriesId()) {
                    ProductCategoryId productCategoryId = new ProductCategoryId();
                    ProductCategory productCategory = new ProductCategory();
                    productCategoryId.setCategoryId(categoriesId);
                    productCategoryId.setProductId(book.getBookId());
                    productCategory.setId(productCategoryId);
                    logger.info("id book : " + book.getBookId());
                    productCategoryRepository.save(productCategory);
//                productService.saveProductCategory(productCategory);
                }
            } else {
                logger.warn("LIst cat is missing");
            }
            return responseUtil.successResponse(book);
        } catch (Exception ex) {
            logger.error("create book",ex);
            throw new ApplicationException(APIStatus.CREATE_PRODUCT_ERROR);
        }
    }

    @ApiOperation(value = "delete book list", notes = "")
    @RequestMapping(value = APIName.BOOKS_DELETE, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> deleteProduct(HttpServletRequest request,
                                                     @RequestBody List<Long> ids,
                                                     @PathVariable Long company_id) {
        try {
            for (Long id : ids) {
                Book book = bookService.getBookById(company_id, id);
                if (book != null) {
//                //update status
                    book.setStatus(Constant.STATUS.DELETED_STATUS.getValue());
                    bookService.update(book);
                    List<ProductCategory> listBookCate = productCategoryRepository.getProCateByProductId(id);
                    for (ProductCategory result : listBookCate) {
                        //delete product category
                        productCategoryRepository.delete(result);
                    }
                }
            }
            return responseUtil.successResponse(null);
        } catch (Exception ex) {
            logger.error("delete book",ex);
            throw new ApplicationException(APIStatus.DELETE_PRODUCT_ERROR);
        }
    }

    @ApiOperation(value = "update product", notes = "")
    @RequestMapping(value = APIName.BOOK_UPDATE, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> updateProduct(HttpServletRequest request,
                                                     @RequestBody Book book) {
        try {
            Book bookData = bookService.getBookById(book.getCompanyId(), book.getBookId());
            if (bookData != null) {
                bookData.setUpdatedOn(new Date());
                //update product
                bookService.update(bookData);
                //delete old list product category
                List<ProductCategory> listBookCate = productCategoryRepository.getProCateByProductId(bookData.getBookId());
                for (ProductCategory result : listBookCate) {
                    //delete product category
                    productCategoryRepository.delete(result);
                }
                //create new list product categories
                for (Long categoriesId : bookData.getListCategoriesId()) {
                    ProductCategoryId productCategoryId = new ProductCategoryId();
                    productCategoryId.setCategoryId(categoriesId);
                    productCategoryId.setProductId(bookData.getBookId());
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setId(productCategoryId);
                    productCategoryRepository.save(productCategory);
                }
                return responseUtil.successResponse(bookData);
            } else {
                throw new ApplicationException(APIStatus.GET_PRODUCT_ERROR);
            }

        } catch (Exception ex) {
            logger.error("update book",ex);
            throw new ApplicationException(APIStatus.UPDATE_PRODUCT_ERROR);
        }
    }


    @ApiOperation(value = "upload product xls", notes = "")
    @RequestMapping(value = APIName.BOOK_UPLOAD, method = RequestMethod.POST, produces = APIName.CHARSET)
    public ResponseEntity<APIResponse> updateProduct(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("sheetname") String sheetName, @RequestParam("colrownum") int  colRowNum, @RequestParam("datarownum") int  dataRowNum, @RequestParam("insert") boolean  insertFlag) throws IOException, InvalidFormatException {
        ExcelUtil excelUtil = new ExcelUtil();
        String filePath = "C:\\Users\\hp\\Desktop\\Tarun\\Data sample\\Flat.File.BookLoader.in (1).xlsm";
        //excelUtil.loadFile(filePath);
        //excelUtil.readSheet(filePath, sheetName);
        List<Map<String, Object>> dataList = excelUtil.readDataAsKJson(convert(file), sheetName, colRowNum, dataRowNum);
        List<Book> bookList = excelUtil.dataListToObj(dataList);
        if(insertFlag){
           return createProducts(request, bookList);
        }
        return responseUtil.successResponse(bookList);
    }

    public File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());

        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
