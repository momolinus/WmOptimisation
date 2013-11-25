setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

#
roald <- read.table(file = "rl-2010-2012.csv", header=T, dec=",", sep=";")
wheel <- read.table(file = "wheelmap_visitor-2010-2012.csv", header=T, dec=",", sep=";")

str(roald)
names(roald)
roald.area <- roald$area[roald$area > 0]
hist(roald.area)
summary(roald.area)
str(roald.area)

str(wheel)
names(wheel)
wheel.area <- wheel$area[wheel$area > 0]
boxplot(wheel.area)
summary(wheel.area)