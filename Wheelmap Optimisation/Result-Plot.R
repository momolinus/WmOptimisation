mapper <- c("wheelchair_visitor\2019", "roald-linus\n2011", "area_guard\n0.00146°", "area_guard\n0.0073°", "area_guard\n0.0365°")
changesets.num <- c(258, 43, 2750, 2529, 1819)
area.mean <- c(479.8549, 1.202933E-04, 4.649216E-07, 1.172182E-05, 1.408914E-04)
changesets.mean.num <- c(49.2, 50.0, 2.9, 4.5, 6.8)

f1 <- rgb(255,0,0,maxColorValue=255)
f2 <- rgb(0,0,255,maxColorValue=255)
f3 <- rgb(0,255,0,maxColorValue=255)
f4 <- rgb(0,255,0,maxColorValue=255)
f5 <- rgb(0,255,0,maxColorValue=255)

plot(changesets.num, changesets.mean.num, xlim=c(0, 300000), ylim=c(0, 7000),cex.lab=1.5)
points(changesets.num, changesets.mean.num, pch=19, cex=sqrt(area.mean*2000000),col=c(f1,f2,f3,f4,f5))

# help(plot)
# ??cex

help(points)
help(polygon)