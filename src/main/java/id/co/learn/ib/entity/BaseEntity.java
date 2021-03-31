package id.co.learn.ib.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import lombok.Data;

/**
 * @author  Adinandra Dharmasurya
 * @version 1.0
 * @since   2020-12-08
 */
@Data
@MappedSuperclass
public class BaseEntity {

    @CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", length = 19)
	private Date createdDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_modified_date", length = 19)
	private Date lastModifiedDate;

    @CreatedBy
	@Column(name = "created_by")
	private String createdBy;

    @LastModifiedBy
	@Column(name = "last_modified_by")
	private String lastModifiedBy;
    
}
