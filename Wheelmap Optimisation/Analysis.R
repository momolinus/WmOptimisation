setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

# read a file with list of changes
changesets <- read.table(file = "optimization_5.csv", header=T, dec=".", sep=";")
# inspect the data set
str(changesets)
names(changesets)

# table: for each user the number of changesets for each algorithm
table(changesets$user, changesets$algorithm)
tapply(changesets$no_changes, list(changesets$user, changesets$algorithm), FUN=mean)
tapply(changesets$area, list(changesets$user, changesets$algorithm), FUN=mean)

# changes with more than 1 change
changesets.more_than_one_change <- subset(changesets, no_changes > 1)
table(changesets.more_than_one_change$user, changesets.more_than_one_change$algorithm)

# changes with 1 change
changesets.one_change <- subset(changesets, no_changes == 1)
table(changesets.one_change$user, changesets.one_change$algorithm)


# changes with area > 0
changesets.area_more_than_null <- subset(changesets, area > 0)
table(changesets.area_more_than_null$user, changesets.area_more_than_null$algorithm)
# create a column combining user and algorithm
changesets.area_more_than_null$algo <- paste(changesets.area_more_than_null$algorithm, changesets.area_more_than_null$user, sep=": ")  
boxplot(area ~ algo, changesets.area_more_than_null, ylab='Fläche in °x°', log='y')