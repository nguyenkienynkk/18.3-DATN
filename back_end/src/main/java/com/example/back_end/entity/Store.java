package com.example.back_end.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "store")
public class Store extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "url", length = Integer.MAX_VALUE)
    private String url;

    @Column(name = "hosts", length = Integer.MAX_VALUE)
    private String hosts;

    @Column(name = "company_name", length = Integer.MAX_VALUE)
    private String companyName;

    @Column(name = "company_address", length = Integer.MAX_VALUE)
    private String companyAddress;

    @Column(name = "company_phone", length = Integer.MAX_VALUE)
    private String companyPhone;

    @Column(name = "ssl_enabled")
    private Boolean sslEnabled;

    @Column(name = "default_language_id")
    private Integer defaultLanguageId;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "deleted")
    private Boolean deleted;

}