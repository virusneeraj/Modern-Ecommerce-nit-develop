package com.nitsoft.ecommerce.database.model.book;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Table(name = "book")
@XmlRootElement
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "book_id")
    private Long bookId;
    
    @Basic(optional = false)
    @Column(name = "company_id")
    private Long companyId;
    
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    
    @Column(name = "browsing_name")
    private String browsingName;
    
    @Basic(optional = false)
    @Column(name = "sale_price")
    private double salePrice;
    
    @Basic(optional = false)
    @Column(name = "list_price")
    private double listPrice;
    
    @Basic(optional = false)
    @Column(name = "overview")
    private String overview;
    
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "is_stock_controlled")
    private Boolean isStockControlled;
    
    @Basic(optional = false)
    @Column(name = "status")
    private int status;
    
    @Column(name = "description")
    private String description;
    
    @Basic(optional = false)
    @Column(name = "rank")
    private int rank;
    
    @Basic(optional = false)
    @Column(name = "sku")
    private String sku;
    
    @Basic(optional = false)
    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;
    
    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @Transient
    @JsonSerialize
    @JsonDeserialize
    public List<Long> listCategoriesId;

    @Column(nullable = true)
    public String feedProductType;

    @Column(nullable = true)
    public Long itemSku;

    @Column(nullable = true)
    public String itemName;

    @Column(nullable = true)
    public Long externalProductId;

    @Column(nullable = true)
    public String externalProductIdType;

    @Column(nullable = true)
    public Long externalProductInformation;

    @Column(nullable = true)
    public String binding;

    @Column(nullable = true)
    public String edition;

    @Column(nullable = true)
    public Long publicationDate;

    @Transient
    @JsonSerialize
    @JsonDeserialize
    public List<String> authors;

    @Transient
    @JsonSerialize
    @JsonDeserialize
    public List<Image> images;

    @Column(nullable = true)
    public Long catalogNumber;

    @Column(nullable = true)
    public String genericKeywords;

    @Transient
    @JsonSerialize
    @JsonDeserialize
    public List<String> targetAudienceBases;

    @Column(nullable = true)
    public Long pages;

    @Column(nullable = true)
    public String languagePublished;

    @Column(nullable = true)
    public Long minimumReadingInterestAge;

    @Column(nullable = true)
    public String websiteShippingWeight;

    @Column(nullable = true)
    public String itemHeight;

    @Column(nullable = true)
    public String itemLength;

    @Column(nullable = true)
    public String itemWidth;

    @Column(nullable = true)
    public String itemWeight;

    @Column(nullable = true)
    public String countryOfOrigin;

    @Column(nullable = true)
    public String legalDisclaimerDescription;

}
