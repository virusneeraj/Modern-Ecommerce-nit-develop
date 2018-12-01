/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nitsoft.ecommerce.api.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author acer
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookModel {
    public String name;
    public Long bookId;
    public Long companyId;
    public String description;
    public String browsingName;
    public String defaultImage;
    public Long salePrice;
    public List<Long> listCategoriesId = null;
    public String listPrice;
    public String overview;
    public Long quantity;
    public Boolean isStockControlled;
    public String sku;
    public Long rank;
    public String feedProductType;
    public Long itemSku;
    public String itemName;
    public Long externalProductId;
    public String externalProductIdType;
    public Long externalProductInformation;
    public String binding;
    public String edition;
    public Long publicationDate;
    public List<String> authors = null;
    //public List<Image> images = null;
    public Long catalogNumber;
    public String genericKeywords;
    public List<String> targetAudienceBases = null;
    public Long pages;
    public String languagePublished;
    public Long minimumReadingInterestAge;
    public String websiteShippingWeight;
    public String itemHeight;
    public String itemLength;
    public String itemWidth;
    public String itemWeight;
    public String countryOfOrigin;
    public String legalDisclaimerDescription;
}
