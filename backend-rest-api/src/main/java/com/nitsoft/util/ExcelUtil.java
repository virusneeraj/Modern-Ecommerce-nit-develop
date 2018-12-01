package com.nitsoft.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nitsoft.ecommerce.database.model.book.Book;
import com.nitsoft.ecommerce.database.model.book.Image;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Workbook loadFile(String filePath) throws IOException, InvalidFormatException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(new File(filePath));

        // Retrieving the number of sheets in the Workbook
        logger.info("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        readSheet(workbook);



        return workbook;
    }

    public Workbook loadFile(File file) throws IOException, InvalidFormatException {
        // Creating a Workbook from an Excel file (.xls or .xlsx)
        Workbook workbook = WorkbookFactory.create(file);

        // Retrieving the number of sheets in the Workbook
        logger.info("Workbook has " + workbook.getNumberOfSheets() + " Sheets : ");

        readSheet(workbook);



        return workbook;
    }

    public void readSheet(Workbook workbook){
        // 2. Or you can use a for-each loop
        logger.info("Retrieving Sheets using for-each loop");
        for(int i =0; i < workbook.getNumberOfSheets(); i++) {
            // Getting the Sheet
            Sheet sheet = workbook.getSheetAt(i);
            logger.info("sheet name"+sheet.getSheetName());
            //readData(sheet);
        }
    }

    public void readSheet(String filePath, String name) throws IOException, InvalidFormatException {
        Sheet sheet = loadFile(filePath).getSheet(name);
        readData(sheet);
    }

    public void readData(Sheet sheet){
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

        // 2. Or you can use a for-each loop to iterate over the rows and columns
        logger.info("\n\nIterating over Rows and Columns using for-each loop\n");
        for (Row row: sheet) {
            for(Cell cell: row) {
                String cellValue = dataFormatter.formatCellValue(cell);
                logger.info(cellValue + "\t");
            }
        }
    }

    public void readData(Sheet sheet, int colRowNum, int dataRowNum){
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();

        Row colRow = null;

        int index = 0;
        for (Row row: sheet) {
            if(index == colRowNum){
                colRow = row;
            }

            for(Cell cell: row) {
                String cellValue = dataFormatter.formatCellValue(cell);
                logger.info(cellValue + "\t");
            }
            index++;
        }
    }

    public List<Map<String, Object>> readDataAsKJson(File file, String name, int colRowNum, int dataRowNum) throws IOException, InvalidFormatException {
        Sheet sheet = loadFile(file).getSheet(name);
        DataFormatter dataFormatter = new DataFormatter();

        List<Map<String, Object>> dataList = new ArrayList<>();

        Row colRow = null;

        int index = 0;
        for (Row row: sheet) {
            if(index == colRowNum){
                colRow = row;
            }

            if(index >= dataRowNum){
                Map<String, Object> objectMap = new HashMap<>();
                for(int i=0; i< colRow.getPhysicalNumberOfCells(); i++){
                    objectMap.put(dataFormatter.formatCellValue(colRow.getCell(i)),dataFormatter.formatCellValue(row.getCell(i)));
                }
                dataList.add(objectMap);
            }


            index++;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(dataList);

        System.out.println(json);

        return dataList;
    }

    public List<Book> dataListToObj(List<Map<String, Object>> mapList){
        List<Book> bookList = new ArrayList<>();
        for (Map map : mapList){
            Book book = new Book();
            List<String> targetAudienceBases = new ArrayList<>();
            List<String> authors = new ArrayList<>();
            List<Image> imageList = new ArrayList<>();
            book.setDescription("Description not available");
            String generic_keywords = "";
            if(!map.get("item_name").toString().isEmpty())
                book.setName(map.get("item_name").toString());
            if(!map.get("item_name").toString().isEmpty())
                book.setItemName(map.get("item_name").toString());
            if(!map.get("binding").toString().isEmpty())
                book.setBinding(map.get("binding").toString());
            if(!map.get("feed_product_type").toString().isEmpty())
                book.setFeedProductType(map.get("feed_product_type").toString());
            if(!map.get("item_weight").toString().isEmpty())
                book.setItemWeight(map.get("item_weight").toString());
            if(!map.get("item_package_quantity").toString().isEmpty())
                book.setQuantity(Integer.parseInt(map.get("item_package_quantity").toString()));
            if(!map.get("product_description").toString().isEmpty())
                book.setDescription(map.get("product_description").toString());
            if(!map.get("target_audience_base1").toString().isEmpty())
                targetAudienceBases.add(map.get("target_audience_base1").toString());
            if(!map.get("target_audience_base2").toString().isEmpty())
                targetAudienceBases.add(map.get("target_audience_base2").toString());
            if(!map.get("target_audience_base3").toString().isEmpty())
                targetAudienceBases.add(map.get("target_audience_base3").toString());
            if(!map.get("target_audience_base4").toString().isEmpty())
                targetAudienceBases.add(map.get("target_audience_base4").toString());
            if(!map.get("target_audience_base5").toString().isEmpty())
                targetAudienceBases.add(map.get("target_audience_base5").toString());
            book.setTargetAudienceBases(targetAudienceBases);
            if(!map.get("country_of_origin").toString().isEmpty())
                book.setCountryOfOrigin(map.get("country_of_origin").toString());
            if(!map.get("legal_disclaimer_description").toString().isEmpty())
                book.setLegalDisclaimerDescription(map.get("legal_disclaimer_description").toString());
            if(!map.get("external_product_id_type").toString().isEmpty())
                book.setExternalProductIdType(map.get("external_product_id_type").toString());
            if(!map.get("item_width").toString().isEmpty())
                book.setItemWidth(map.get("item_width").toString());
            if(!map.get("website_shipping_weight").toString().isEmpty())
                book.setWebsiteShippingWeight(map.get("website_shipping_weight").toString());
            if(!map.get("external_product_id").toString().isEmpty())
                book.setExternalProductId(Long.valueOf(map.get("external_product_id").toString()));
            if(!map.get("item_height").toString().isEmpty())
                book.setItemHeight(map.get("item_height").toString());
            if(!map.get("publication_date").toString().isEmpty())
                book.setPublicationDate(Long.valueOf(map.get("publication_date").toString()));
            if(!map.get("minimum_reading_interest_age").toString().isEmpty())
                book.setMinimumReadingInterestAge(Long.valueOf(map.get("minimum_reading_interest_age").toString()));
            if(!map.get("generic_keywords1").toString().isEmpty())
                generic_keywords += map.get("generic_keywords1");
            if(!map.get("generic_keywords2").toString().isEmpty())
                generic_keywords += map.get("generic_keywords2");
            if(!map.get("generic_keywords3").toString().isEmpty())
                generic_keywords += map.get("generic_keywords3");
            if(!map.get("generic_keywords4").toString().isEmpty())
                generic_keywords += map.get("generic_keywords4");
            if(!map.get("generic_keywords5").toString().isEmpty())
                generic_keywords += map.get("generic_keywords5");
            book.setGenericKeywords(generic_keywords);
            if(!map.get("sale_price").toString().isEmpty())
                book.setSalePrice(Double.parseDouble(map.get("sale_price").toString()));
            if(!map.get("maximum_retail_price").toString().isEmpty())
                book.setListPrice(Double.parseDouble(map.get("maximum_retail_price").toString()));
            if(!map.get("author1").toString().isEmpty())
                authors.add(map.get("author1").toString());
            if(!map.get("author2").toString().isEmpty())
                authors.add(map.get("author2").toString());
            if(!map.get("author3").toString().isEmpty())
                authors.add(map.get("author3").toString());
            book.setAuthors(authors);
            if(!map.get("external_product_information").toString().isEmpty())
                book.setExternalProductInformation(Long.valueOf(map.get("external_product_information").toString()));
            if(!map.get("pages").toString().isEmpty())
                book.setPages(Long.valueOf(map.get("pages").toString()));
            if(!map.get("main_image_url").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("main_image_url").toString());
                imageList.add(image);
            }
            if(!map.get("other_image_url1").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url1").toString());
                imageList.add(image);
            }
            if(!map.get("other_image_url2").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url2").toString());
                imageList.add(image);
            }
            if(!map.get("other_image_url3").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url3").toString());
                imageList.add(image);
            }
            if(!map.get("other_image_url4").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url4").toString());
                imageList.add(image);
            }
            if(!map.get("other_image_url5").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url5").toString());
                imageList.add(image);
            }if(!map.get("other_image_url6").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url6").toString());
                imageList.add(image);
            }
            if(!map.get("other_image_url7").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url7").toString());
                imageList.add(image);
            }if(!map.get("other_image_url8").toString().isEmpty()){
                Image image = new Image();
                image.setData(map.get("other_image_url8").toString());
                imageList.add(image);
            }
            book.setImages(imageList);
            if(!map.get("catalog_number").toString().isEmpty())
                book.setCatalogNumber(Long.valueOf(map.get("catalog_number").toString()));
            if(!map.get("item_length").toString().isEmpty())
                book.setItemLength(map.get("item_length").toString());

            bookList.add(book);
        }
        return bookList;
    }

}
