setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

#
#roald <- read.table(file = "rl-2010-2012.csv", header=T, dec=",", sep=";")
compare <- read.table(file = "roald-linus-2011-wheelchair-2010.csv", header=T, dec=".", sep=";")

str(compare)
names(compare)
summary(compare)

compare.positiv.area <- compare[compare$area > 0, ] 
str(compare.positiv.area)
names(compare.positiv.area)
summary(compare.positiv.area)
boxplot(area ~ algorithm, compare.positiv.area, ylab='Fläche in °x°', log='y')
boxplot(no_changes ~ algorithm, compare.positiv.area, ylab='Changes/Changeset')
help(boxplot)

summary(compare.positiv.area[compare.positiv.area$user == 'roald-linus', ])
summary(compare.positiv.area[compare.positiv.area$user == 'no_user', ])
summary(compare.positiv.area[compare.positiv.area$user == 'wheelmap_visitor', ])

#hist(area ~ user, compare.positiv.area)

#str(roald)
#names(roald)
#roald.area <- roald$area[roald$area > 0]
#hist(roald.area)
#summary(roald.area)
#str(roald.area)
#help(str)
# show the structur of wheel
#str(wheel)

# show the column names
#names(wheel)

# summary for all columns
#summary(wheel)

#wheel.area <- wheel[wheel$area > 0, ]
#summary(wheel.area)
#summary(wheel.area$area)
#hist(wheel.area$area)
#help(boxplot)

#wheel.area.user <- wheel.area[wheel.area$user == "no_user", ]
#summary(wheel.area.user)
#length(wheel.area.user$area)
#wheel.area.user.wrong <- wheel.area.user[wheel.area.user$area > 40,]  
#length(wheel.area.user.wrong$area)

#boxplot(area ~ user, data=wheel.area)