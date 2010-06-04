ea.data <- read.table("exp_all.txt", header=T)

pdf("evaluations.pdf")
boxplot(ea.data$EVALUATIONS ~ ea.data$EXP, col="bisque", notch=TRUE, range=0)
#boxplot(ea.data$EVALUATIONS ~ ea.data$EXP, col="bisque", range=0)
dev.off()

pdf("evaluations_sorted.pdf")
sortedByMedians <- reorder(ea.data$EXP, ea.data$EVALUATIONS, median)
boxplot(ea.data$EVALUATIONS ~ sortedByMedians, col="bisque", notch=TRUE, cex.axis=0.5)
dev.off()
