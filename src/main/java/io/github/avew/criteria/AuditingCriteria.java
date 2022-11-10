package io.github.avew.criteria;

import io.github.avew.query.InstantFilter;
import io.github.avew.query.StringFilter;
import lombok.*;

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
