-- select all top-level features
select id, version, class, name, unique_name 
from feature 
where class IN (
    'org.bbop.apollo.Gene', 
    'org.bbop.apollo.Pseudogene',
    'org.bbop.apollo.TransposableElement',
    'org.bbop.apollo.RepeatRegion'
);


-- find sub-level features of top-level features
select id, version, class, name, unique_name 
from feature 
where id IN 
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
);

-- find orgaism_id of features
select organism_id from sequence where id IN 
(
    select sequence_id from feature_location 
    where feature_id IN 
    (
        select id from feature where class IN 
        (
        'org.bbop.apollo.Gene', 
        'org.bbop.apollo.Pseudogene',
        'org.bbop.apollo.TransposableElement',
        'org.bbop.apollo.RepeatRegion'
        )
    )
);


-- top features
--select count(f.id), o.common_name
select f.id, f.class, f.name, f.unique_name, o.common_name 
from feature as f, feature_location as l, sequence as s, organism as o
where f.class IN
(
    'org.bbop.apollo.Gene', 
    'org.bbop.apollo.Pseudogene',
    'org.bbop.apollo.TransposableElement',
    'org.bbop.apollo.RepeatRegion'
)
AND f.id=l.feature_id
AND l.sequence_id=s.id 
AND s.organism_id = o.id;
--GROUP BY o.common_name;


-- sub-level features
--select count(f.id), o.common_name
select f.id, f.class, f.name, f.unique_name, o.common_name 
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
AND s.organism_id = o.id;
--GROUP BY o.common_name;


-- top features without children
--select count(f.id), o.common_name
select f.id, f.class, f.name, f.unique_name, o.common_name 
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
--GROUP BY o.common_name;


-- count annotations group by organisms
select organism, sum(num) as annotations
from 
(
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
