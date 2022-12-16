package io.github.avew.criteria;

import lombok.*;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.StringFilter;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuditingCriteria {

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

}
