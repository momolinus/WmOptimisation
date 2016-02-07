rm(list=ls(all=TRUE))
# mit xpd=T werden die Daten auch über das Diagramm gezeichnet
par(xpd=T)
mapper <- c("wheelchair_visitor\n2010","roald-linus\n2011", "area_guard\n0.00146°", "area_guard\n0.0073°", "area_guard\n0.0365°")
changesets.num <- c(258, 43, 2750, 2529, 1819)
area.mean <- c(479.8549, 1.202933E-04, 4.649216E-07, 1.172182E-05, 1.408914E-04)
changesets.mean.num <- c(49.2, 50.0, 2.9, 4.5, 6.8)

f1 <- rgb(255,0,0,maxColorValue=255)
f2 <- rgb(0,0,255,maxColorValue=255)
f3 <- rgb(132,255,132,maxColorValue=255)
f4 <- rgb(132,255,132,maxColorValue=255)
f5 <- rgb(132,255,132,maxColorValue=255)

plot(changesets.num, changesets.mean.num,
	xlim=c(0, 3000), ylim=c(0, 70), pch=3,
	xlab="Anzahl der Changesets", ylab="i. D. Änderung / Changeset")

# (de) pch=22 stellt die Punkte als Qudrat ohne Füllung dar, pch=15 als gefülltes Quadrat
# siehe auch Datendesign mit R, S. 76
points(changesets.num[2:5], changesets.mean.num[2:5], pch=22, cex=sqrt(area.mean[2:5]*7000000),col=c(f2,f3,f4,f5))

# 2 = left, 3 = above , 4 = right
# 1 = wheelchair hitory
# 2= roald-linus
text(changesets.num[1], changesets.mean.num[1], mapper[1],pos=4, cex=0.75,  offset=0.7)
text(changesets.num[2], changesets.mean.num[2], mapper[2],pos=3, cex=0.75, offset=0.7)
# 0,00146
text(changesets.num[3], changesets.mean.num[3], mapper[3],pos=4, cex=0.75, offset=1.2)
# 0,0073
text(changesets.num[4], changesets.mean.num[4], mapper[4],pos=3, cex=0.75, offset=1.8)
# 0,0365
text(changesets.num[5], changesets.mean.num[5], mapper[5],pos=3, cex=0.75, offset=1.8)

help(text)
# ??cex

# und nun noch Linien vom Kreuz bis zum Quadrat, bei den Wheelchair-Daten halt ohne Quadrat

help(points)
help(polygon)