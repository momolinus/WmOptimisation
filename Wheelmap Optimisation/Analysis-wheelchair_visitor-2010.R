setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

### dataset ###

# read a table with all changesets for wheelchair_visitor in year 2010
# each row contains one (unique) changeset, the number of changes for this 
# changeset, the area this changeset covers, the user and the algorithm the
# changeset was generated

# because the file wheelchair_visitor-2010-raw.csv contains original raw data, the user 
# is allways wheelchair_visitor and the algorithm is allways original

# read the data
wheel <- read.table(file = "wheelchair_visitor-2010-raw.csv", header=T, dec=".", sep=";")
# describe dataset
str(wheel)
# the names of the columns
names(wheel)
# 1298
length(wheel$changesetId)

# inspect changeset with area > 0
wheel.area.not.null <- subset(wheel, area > 0) 
# 258
length(wheel.area.not.null$changesetId)