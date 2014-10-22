setwd("C:/Users/Marcus/git/WmOptimisation/Wheelmap Optimisation")

# read a file with list of changes
changes <- read.table(file = "optimization_5.csv", header=T, dec=".", sep=";")

# expected: a data.frame with columns "changesetId", "area", "no_changes", "user" and "algorithm"
str(changes)
# names -> see columns aboven
names(changes)
# summary(changes)
table(changes$user, changes$algorithm)
help(tapply)
help(min)
tapply(changes$area, changes$algorithm, FUN=max)
tapply(changes$area, list(changes$user, changes$algorithm), FUN=max)
tapply(changes$area, list(changes$user, changes$algorithm), FUN=min)
tapply(changes$area, list(changes$user, changes$algorithm), FUN=mean)
tapply(changes$no_changes, list(changes$user, changes$algorithm), FUN=max)
tapply(changes$no_changes, list(changes$user, changes$algorithm), FUN=min)
tapply(changes$no_changes, list(changes$user, changes$algorithm), FUN=mean)
tapply(changes$no_changes, list(changes$user, changes$algorithm), FUN=median)

changes.positiv.area <- changes[changes$area > 0, ]
changes.positiv.area$algo <- paste(changes.positiv.area$algorithm, changes.positiv.area$user, sep=": ")  
str(changes.positiv.area)
names(changes.positiv.area)
summary(changes.positiv.area)
summary(changes.positiv.area$area)
boxplot(changes.positiv.area$area)
help(summary)
boxplot(area ~ algo, changes.positiv.area, ylab='Fläche in °x°', log='y')
boxplot(no_changes ~ algo, changes.positiv.area, ylab='Changes/Changeset')
plot (changes.positiv.area$algo)
help(boxplot)

changesets.algo <- tapply(changes.positiv.area$changesetId, changes.positiv.area$algo,length)
changes.algo <- tapply(changes.positiv.area$no_changes, changes.positiv.area$algo,length)
changes.algo
changesets.algo
names(changesets.algo)
help(tapply)

# Anzahl von Changes

summary(changes.positiv.area[changes.positiv.area$user == 'roald-linus', ])
summary(changes.positiv.area[changes.positiv.area$user == 'no_user', ])
summary(changes.positiv.area[changes.positiv.area$user == 'wheelmap_visitor', ])

#hist(area ~ user, changes.positiv.area)

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