-- count annotations group by organisms
-- if the top-level has child features,  count the child features
-- otherwise count the top-level feature itself
select organism, sum(num) as annotations
from 
(
    -- child features (ex. MRNA, Transcipt etc.)
    select count(f.id) as num, o.common_name as organism
    from feature as f, feature_location as l, sequence as s, organism as o
    where f.id IN
    (
        select child_feature_id from feature_relationship where parent_feature_id IN 
        (
            select id from feature where class IN 
            (
            'org.bbop.apollo.Gene', 
            'org.bbop.apollo.Pseudogene',
            'org.bbop.apollo.TransposableElement',
            'org.bbop.apollo.RepeatRegion'
            )
        )
    )
    AND f.id=l.feature_id
    AND l.sequence_id=s.id 
    AND s.organism_id = o.id
    GROUP BY o.common_name
    union all
    -- features without child features (ex. TransposableElement, RepeatRegion)
    select count(f.id) as num, o.common_name as organism
    from feature as f, feature_location as l, sequence as s, organism as o
    where class IN 
    (
        'org.bbop.apollo.Gene', 
        'org.bbop.apollo.Pseudogene',
        'org.bbop.apollo.TransposableElement',
        'org.bbop.apollo.RepeatRegion'
    )
    AND f.id NOT IN
    (
        select parent_feature_id from feature_relationship where parent_feature_id IN 
        (
            select id from feature where class IN 
            (
            'org.bbop.apollo.Gene', 
            'org.bbop.apollo.Pseudogene',
            'org.bbop.apollo.TransposableElement',
            'org.bbop.apollo.RepeatRegion'
            )
        )
)
AND f.id=l.feature_id
AND l.sequence_id=s.id
AND s.organism_id = o.id
GROUP BY o.common_name
) as t
group by organism;
