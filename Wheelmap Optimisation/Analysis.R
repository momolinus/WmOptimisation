setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

# read wheelmap_visitor's areas in 2010
w2010 <- read.table(file = "wh_area_2010.csv", header=T, dec=",")
rl2011 <- read.table(file = "rl_area_2011.csv", header=T, dec=",")

str(w2010)
names(w2010)
w2010.area <- w2010$area[w2010$area > 0]
hist(w2010.area)

str(rl2011)
names(rl2011)
rl2011.area <- rl2011$area[rl2011$area > 0]
boxplot(rl2011.area)