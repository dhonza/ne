ea.data <- read.table("exp_all.txt", header=T)
#boxplot(ea.data$EVALUATIONS ~ ea.data$EXP, col="bisque", range=0)
boxplot(ea.data$EVALUATIONS ~ ea.data$EXP, col="bisque")
