setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

# Löschen von allen Objekten
# rm(list=ls(all=TRUE))
# hat das Löschen geklappt
# ls()

# read a file with list of changes
changesets <- read.table(file = "optimization_5.csv", header=T, dec=".", sep=";")
# inspect the data set
str(changesets)
names(changesets)
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

# table: for each user the number of changesets for each algorithm
# and changesets == 0
table(changesets.no_changes.is_0$user, changesets.no_changes.is_0$algorithm)

# table: for each user the number of changesets for each algorithm
# and changesets == 1
table(changesets.no_changes.is_1$user, changesets.no_changes.is_1$algorithm)

# table: for each user the number of changesets for each algorithm
# and changesets > 1
table(changesets.no_changes.is_greater_than_1$user, changesets.no_changes.is_greater_than_1$algorithm)

### Inspect suspicion changesets  
changesets.areaguard <- subset(changesets, algorithm == 'area guard (0.0073)' && user == 'wheelmap_visitor')
length(changesets.areaguard$changesetId)

changesets.areaguard.error <- subset (changesets.areaguard, area > 0 && no_changes < 1)
length(changesets.areaguard.error$changesetId)

changesets.areaguard.no_changes.is_0 <- subset (changesets.areaguard, no_changes == 0)
# 5877
length(changesets.areaguard.no_changes.is_0$changesetId)

changesets.areaguard.no_changes.is_1 <- subset (changesets.areaguard, no_changes == 1)
# 0
length(changesets.areaguard.no_changes.is_1$changesetId)

changesets.areaguard.no_changes.is_greater_than_1 <- subset (changesets.areaguard, no_changes > 1)
# 1
length(changesets.areaguard.no_changes.is_greater_than_1$changesetId)
summary(changesets.areaguard.no_changes.is_greater_than_1)

