# Löschen von allen Objekten
rm(list=ls(all=TRUE))
# hat das Löschen geklappt
ls()

setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

# read a file with list of changes
changesets <- read.table(file = "optimization_15.csv", header=T, dec=".", sep=";")
# inspect the data set
str(changesets)
names(changesets)

# table: for each user the number of changesets for each algorithm
table(changesets$user, changesets$algorithm)
# table: mean number of changes in a changeset
tapply(changesets$no_changes, list(changesets$user, changesets$algorithm), FUN=mean)
# table: mean area in a changeset
tapply(changesets$area, list(changesets$user, changesets$algorithm), FUN=mean)

# //TODO prüfen warum es bei dem orignal nur Area = -1 gibt
# obiges ist erledigt, trotzdem ist eine Unterschediung nach area < 0, area == 0 und 
# area > 0 besser, da die Changesets sich "semantisch" unterscheiden, == -1 bedeutet
# dass keine Changes gemacht wurden
 
# changes with more than 1 change
changesets.more_than_one_change <- subset(changesets, no_changes > 1)
table(changesets.more_than_one_change$user, changesets.more_than_one_change$algorithm)

# changes with 1 change
changesets.one_change <- subset(changesets, no_changes == 1)
table(changesets.one_change$user, changesets.one_change$algorithm)

# changes with area > 0
changesets.area_more_than_null <- subset(changesets, area > 0)
table(changesets.area_more_than_null$user, changesets.area_more_than_null$algorithm)
# table: mean area in a changeset
tapply(changesets.area_more_than_null$area, list(changesets.area_more_than_null$user, changesets.area_more_than_null$algorithm), FUN=mean)
tapply(changesets.area_more_than_null$no_changes, list(changesets.area_more_than_null$user, changesets.area_more_than_null$algorithm), FUN=mean)

# create a column combining user and algorithm
changesets.area_more_than_null$algo <- paste(changesets.area_more_than_null$algorithm, changesets.area_more_than_null$user, sep=": ")  
boxplot(area ~ algo, changesets.area_more_than_null, ylab='Fläche in °x° (log)', log='y')

