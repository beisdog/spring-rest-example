package com.beisert.demo.spring.boot.rest.product.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Order entity.
 */
@Entity
@Table(name = "Orders")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    private List<Product> products;

    private BigDecimal total;

    @Email
    @NotBlank
    private String buyersEmail;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    @PreUpdate @PrePersist
    public void calcTotal() {
        if (this.products != null) {
            BigDecimal sum = this.products.stream().map(product -> product.getPrice())
                    .reduce(BigDecimal.ZERO, (dec1, dec2) -> dec1.add(dec2));
            this.total = sum;
        }
    }
}
