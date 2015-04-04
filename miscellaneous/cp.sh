#!/bin/bash

for APP in agrpla anogla athros blager cataqu cenexi cimlec cercap copflo diacit drobia drobip droele droeug drofic drokik drorho drotak ephdan euraff fraocc gerbue halhal homvit hyaazt ladful lathes lepdec limlun loxrec mansex maydes oncfas onttau oruabi pacven partep tigcal tripre
do
    mkdir -p /path/to//tomcat/webapps/$APP/WEB-INF/classes/org/bbop/apollo/web/statistics
    cp lib/ant.jar /path/to//tomcat/webapps/$APP/WEB-INF/lib/
    cp -a classes/org/bbop/apollo/web/StatisticsService*.class /path/to//tomcat/webapps/$APP/WEB-INF/classes/org/bbop/apollo/web/
    cp -a classes/org/bbop/apollo/web/statistics/*.class /path/to//tomcat/webapps/$APP/WEB-INF/classes/org/bbop/apollo/web/statistics/
    chown tomcat:i5k /path/to//tomcat/webapps/$APP/WEB-INF/classes/org/bbop/apollo/web/StatisticsService*.class
    chown -R tomcat:i5k /path/to//tomcat/webapps/$APP/WEB-INF/classes/org/bbop/apollo/web/statistics
done
