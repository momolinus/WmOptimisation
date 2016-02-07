rm(list=ls(all=TRUE))
# mit xpd=T werden die Daten auch über das Diagramm gezeichnet
par(xpd=T)
mapper <- c("wheelchair_visitor\2019","roald-linus\n2011", "area_guard\n0.00146°", "area_guard\n0.0073°", "area_guard\n0.0365°")
changesets.num <- c(258, 43, 2750, 2529, 1819)
area.mean <- c(479.8549, 1.202933E-04, 4.649216E-07, 1.172182E-05, 1.408914E-04)
changesets.mean.num <- c(49.2, 50.0, 2.9, 4.5, 6.8)

f1 <- rgb(255,0,0,maxColorValue=255)
f2 <- rgb(0,0,255,maxColorValue=255)
f3 <- rgb(132,255,132,maxColorValue=255)
f4 <- rgb(132,255,132,maxColorValue=255)
f5 <- rgb(132,255,132,maxColorValue=255)

plot(changesets.num, changesets.mean.num,
	xlim=c(0, 3000), ylim=c(0, 70), pch='',
	xlab="Anzahl der Changesets", ylab="mittl. Änderung/Changeset")

# (de) pch=22 stellt die Punkte als Qudrat ohne Füllung dar, pch=15 als gefülltes Quadrat
# siehe auch Datendesign mit R, S. 76
points(changesets.num[2:5], changesets.mean.num[2:5], pch=15, cex=sqrt(area.mean[2:5]*2000000),col=c(f2,f3,f4,f5))
# 2 = left, 3 = above , 4 = right
text(changesets.num, changesets.mean.num, mapper, pos=c(3,3,4,3,3),cex=0.75, offset=1)
help(text)
# ??cex

help(points)
help(polygon)