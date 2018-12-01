package com.nitsoft.ecommerce.database.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@Table(name = "Slider")
@XmlRootElement
public class Slider implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "slider_id")
    private Long sliderId;

    @Basic(optional = false)
    @Column(name = "company_id")
    private Long companyId;

    @Basic(optional=true)
    @Column(name = "header")
    private String header;

    @Basic(optional=true)
    @Column(name = "title")
    private String title;

    @Basic(optional=true)
    @Column(name = "message")
    private String message;

    @Basic(optional=true)
    @Column(name = "images")
    @ElementCollection(targetClass=String.class, fetch = FetchType.EAGER)
    private List<String> images;

    @Basic(optional = false)
    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Column(name = "updated_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

}
