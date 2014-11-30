setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

# Löschen von allen Objekten
# rm(list=ls(all=TRUE))
# hat das Löschen geklappt
# ls()

# read a file with list of changes
changesets <- read.table(file = "optimization_9.csv", header=T, dec=".", sep=";")
# inspect the data set
str(changesets)
names(changesets)
summary(changesets$user)
summary(changesets$algorithm)
summary(changesets$no_changes)
summary(changesets$area)

# 10945
length(changesets$changesetId)

# all changesets with no changes == 0
changesets.no_changes.is_0 <- subset(changesets, no_changes == 0)
# 9732 
length(changesets.no_changes.is_0$changesetId)

# all changesets with no changes == 1
changesets.no_changes.is_1 <- subset(changesets, no_changes == 1)
# 912 
length(changesets.no_changes.is_1$changesetId)

# all changesets with no changes > 1
changesets.no_changes.is_greater_than_1 <- subset(changesets, no_changes > 1)
# 301 
length(changesets.no_changes.is_greater_than_1$changesetId)

# check the number of subsets
# = 10945
9732 + 912 + 301

#################################
### tables: number of changes ###
#################################

# number of changes for user/algorithm
table(changesets$user, changesets$algorithm)

# table: for each user the number of changesets for each algorithm
# and changesets == 0
table(changesets.no_changes.is_0$user, changesets.no_changes.is_0$algorithm)

# table: for each user the number of changesets for each algorithm
# and changesets == 1
table(changesets.no_changes.is_1$user, changesets.no_changes.is_1$algorithm)

# table: for each user the number of changesets for each algorithm
# and changesets > 1
table(changesets.no_changes.is_greater_than_1$user, changesets.no_changes.is_greater_than_1$algorithm)

####################################
### Inspect suspicion changesets ###
####################################
 
changesets.wheel <- subset(changesets, algorithm == 'area guard (0.0073)' & user == 'wheelmap_visitor')
str(changesets.wheel)
summary(changesets.wheel$user)
# 5878 changsets for "wheelchair_visitor" with "area guard (0.0073)"
length(changesets.wheel$changesetId)

### the wrong changesets ###

## area > 0 ##

# area > 0 and no_changes < 0 ###### this must be wrong #######
changesets.wheel.error <- subset (changesets.wheel, area > 0 & no_changes < 1)
# 2642
length(changesets.wheel.error$changesetId)

# area > 0 and no_changes == 1 could not be
changesets.wheel.error2 <- subset (changesets.wheel, area > 0 & no_changes == 1)
# 0
length(changesets.wheel.error2$changesetId)

# area > 0 and no_changes > 1 could  be
changesets.wheel.error3 <- subset (changesets.wheel, area > 0 & no_changes > 1)
# 0
length(changesets.wheel.error3$changesetId)

## area <= 0 ##

# the 1. rest area <= 0 and no changes < 1
changesets.wheel.error4 <- subset (changesets.wheel, area <= 0 & no_changes < 1)
# 3235
length(changesets.wheel.error4$changesetId)

# the 2. rest area <= 0 and no changes == 1
changesets.wheel.error5 <- subset (changesets.wheel, area <= 0 & no_changes == 1)
# 0
length(changesets.wheel.error5$changesetId)

# the 3. rest area <= 0 and no changes > 1 could be on the same node
changesets.wheel.error6 <- subset (changesets.wheel, area <= 0 & no_changes > 1)
# 1
length(changesets.wheel.error6$changesetId)

## Test the number ##

# = 5878
2642 + 3235 + 1