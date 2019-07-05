# Obs: It works only if the script is "Sourced" before the run.
# This can be done via the command: source('path/to/this/file/plots2.R')
# The suggestion is to use Rstudio as IDE and make sure to have the flag "Source on Save" checked.
# ( see: http://stackoverflow.com/questions/13672720/r-command-for-setting-working-directory-to-source-file-location )
# If it doesn't work because of UTF8, see: https://support.rstudio.com/hc/en-us/community/posts/200661587-Bug-when-sourcing-the-application?sort_by=votes 
this.dir <- dirname(parent.frame(2)$ofile)
setwd(this.dir)

library(ggplot2)
options(gsubfn.engine = "R") # Thanks to http://stackoverflow.com/questions/17128260/r-stuck-in-loading-sqldf-package
library(sqldf)
library(gridExtra)
library(gtable)
library(grid)
library(scales)
library(reshape2)
library(rPref)
library(xtable)

models <- c("register","gpl","TightVNC","Rhiscom1","Windows80","ERP_SPL_1")
folder <- '../2017_fosd/images/'
folderInput <- '../output/'
OUTPUT = TRUE
# p1 <- ggplot(mydata, aes(model,time/1000,color=model)) + geom_boxplot()
#  print(p1)
optLegend <- theme(legend.direction = "horizontal", legend.justification = c(0, 1), legend.position = c(0.03,0.99))
noLegend <- theme(legend.position = "none", axis.title.x=element_blank(), axis.title.y=element_blank())
noAxisLabel <- theme(axis.title.x=element_blank(), axis.title.y=element_blank())
scaleGrey <- scale_fill_grey(start = 0.5, end = 1)

g_legend <- function(a.gplot){
  tmp <- ggplot_gtable(ggplot_build(a.gplot))
  leg <- which(sapply(tmp$grobs, function(x) x$name) == "guide-box")
  legend <- tmp$grobs[[leg]]
  return(legend)}
geo <- geom_point(size=2)
nl <- theme(legend.position="none")
bl <- theme(legend.direction = "horizontal", legend.position = "bottom")
sm <- geom_smooth(method=lm, se=FALSE) #see: http://www.sthda.com/english/wiki/ggplot2-scatter-plots-quick-start-guide-r-software-and-data-visualization

cleanData <- function(da) {
  da <- sqldf("select * from da where method<>'NONE' and method<>'null' and type<>'null' and flit<>'null' and method<>'bestLEV' and method<>'bestSIMPL'")
  dat <- da
  dat <- sqldf(c(fn$identity("update dat set type='strengthening' where type='AND'"), fn$identity("update dat set type='weakening' where type='OR'"),fn$identity("update dat set method='onlySelection' where method='OnlySelec'"),"select * from dat"))
  da <- dat
  return(da)
}

# see http://stackoverflow.com/questions/1236620/global-variables-in-r
# see http://stackoverflow.com/questions/16838613/cannot-read-unicode-csv-into-r
doSql <- function() {
  assign("dat1", cleanData(read.csv(paste(folderInput,"stats_",models[1],".txt",sep=""), encoding="UTF-8")), envir=.GlobalEnv)
  assign("dat2", cleanData(read.csv(paste(folderInput,"stats_",models[3],".txt",sep=""), encoding="UTF-8")), envir=.GlobalEnv)
  assign("datGlob", rbind(dat1, dat2), envir=.GlobalEnv) # The whole data
}

doSql2 <- function() {
  assign("dat1", read.csv(paste(folderInput,"stats2_",models[1],".csv",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
  assign("dat2", read.csv(paste(folderInput,"stats2_",models[3],".csv",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
  datGlob <<- rbind(dat1, dat2) # The whole data
}

sortMethods <- function(da) {
  #da$method <- factor(da$method, c("Naive", "atgt", "espresso", "jbool", "kmkeencnf","qm", "bestLEV", "bestSIMPL"))
  da$method <- factor(da$method, c("Naïve", "onlySelection", "ATGT", "Espresso", "JBool", "QM"))
  return(da)
}

# see http://stackoverflow.com/questions/14504869/histogram-with-negative-logarithmic-scale-in-r
asinh_trans <- function(){
  trans_new(name = 'asinh', transform = function(x) asinh(x), 
            inverse = function(x) sinh(x))
}

drawPlot1 <- function(da, modelName) {
  dat <- da
  dat3 <- sortMethods(sqldf("select * from dat"))
  
  #p1 <- ggplot(dat3, aes(method,lev,fill=method)) + geom_violin(varwidth=T) + geom_boxplot(width=0.1, fill=rgb(1,1,1)) + scaleGrey + theme_bw() + ggtitle(paste("Experiment 1 - ",modelName)) + nl #+ scale_y_continuous(trans='log10')
  #p1b <- ggplot(dat3, aes(method,simpl,fill=method)) + geom_violin(varwidth=T) + geom_boxplot(width=0.1, fill=rgb(1,1,1)) + scaleGrey + theme_bw() + nl #+ scale_y_continuous(trans='log10')
  p2 <- ggplot(sortMethods(sqldf("select method,flit as ficSize,avg(lev) as avgEditDistance from dat group by method,ficSize")), aes(x=ficSize,y=avgEditDistance,color=method)) + geom_line(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + nl+sm #+ scale_y_continuous(trans='log10')
  
  p3 <- ggplot(dat3, aes(method,lev,fill=type)) + geom_boxplot(varwidth=T) + scaleGrey + theme_bw() + theme(legend.direction = "horizontal") + labs(x = "strategy", y = "BFED") + theme(axis.title.x=element_blank()) #+ scale_y_continuous(trans='log10')
  p3b <- ggplot(dat3, aes(method,simpl,fill=type)) + geom_boxplot(varwidth=T) + scaleGrey + theme_bw() + nl + labs(x = "strategy", y = "BFCD") + theme(axis.title.x=element_blank())#+ scale_y_continuous(trans='log10')
  
  #p4 <- ggplot(sortMethods(sqldf("select method,flit,avg(lev) as a from dat where type='AND' group by method,flit")), aes(x=flit,y=a,color=method)) + geom_line(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Type-AND f.i.c.") + bl + sm #+ scale_y_continuous(trans='log10')
  #p5 <- ggplot(sortMethods(sqldf("select method,flit,avg(lev) as a from dat where type='OR' group by method,flit")), aes(x=flit,y=a,color=method)) + geom_line(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Type-OR f.i.c.") +nl+sm#+ scale_y_continuous(trans='log10')
  #leg <- g_legend(p4)
  #p4 <- p4+nl
  
  leg <- g_legend(p3)
  p3 <- p3+nl
  p <- grid.arrange(arrangeGrob(p3,p3b,leg,ncol=1,heights=c(.45,.45,.1)))
  #p <- grid.arrange(arrangeGrob(p1,p3,p1b,p3b,p2,arrangeGrob(p4,p5,ncol=2),leg,ncol=1,heights=c(3/19,3/19,3/19,3/19,3/19,3/19,.5/19)))
  #p <- grid.arrange(arrangeGrob(p1,p3,p1b,p3b,p2,arrangeGrob(p4,p5,ncol=2),ncol=1))
  
  #p <- grid.arrange(arrangeGrob(arrangeGrob(p1,p2,ncol=1),ncol=1))
  #if (OUTPUT) ggsave(p,file=paste(folder,"plot_",modelName,"1.png",sep=""), width=6, height=12)
  if (OUTPUT) ggsave(p,file=paste(folder,"plot_",modelName,"1.pdf",sep=""), width=5, height=4)
}

# see http://www.p-roocks.de/rpref/index.php?section=examples
drawPareto <- function() {
  p <- ggplot(sortMethods(sqldf("select method,lev as levenshtein, simpl as complexity from datGlob where method<>'bestSIMPL' and method<>'bestLEV'")), aes(x=levenshtein,y=complexity,color=method)) + geom_point(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Correlation between simpl and lev") +bl+sm + scale_y_continuous(trans='asinh') + scale_x_continuous(trans='log10')
  if (OUTPUT) ggsave(p,file=paste(folder,"plot_pareto.png",sep=""), width=5, height=5)
}

drawTime <- function(da, modelName) {
  dat <- da  #per l'istruzione sotto con SQL Update, see http://stackoverflow.com/questions/20130417/update-function-sqldf-r-language
  dat <- sortMethods(sqldf(c(fn$identity("update dat set timeSimplification=timeSelection where method='noSimpl'"),fn$identity("update dat set timeSimplification=timeSimplification+1, timeSelection=timeSelection+1"),"select * from dat")))
  
  p <- ggplot(dat, aes(method,timeSimplification,fill=type)) + geom_boxplot(varwidth=T) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + optLegend + scale_y_continuous(trans='log10',  breaks=c(1,5,10,50,100,500,1000,10000))

  if (OUTPUT) ggsave(p,file=paste(folder,"plot_time",modelName,".png",sep=""), width=3, height=2)
}

#EXPERIMENT 2
drawPlot2A <- function(da, modelName) {
  #dat_m <- melt(da, id.vars = "method", measure.vars = c("simpl","lev"))
  #p <- ggplot(dat_m, aes(x=variable,y=value,colour=as.factor(variable))) + geom_boxplot(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Simplification steps") + optLegend

  p1 <- ggplot(da, aes(x=method,y=lev,fill=method)) + geom_boxplot(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + bl #+ scale_y_continuous(trans='log10')
  p2 <- ggplot(da, aes(x=method,y=simpl,fill=method)) + geom_boxplot(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + nl #+ scale_y_continuous(trans='log10')

  leg<-g_legend(p1)
  p1<-p1+nl
  
  p <- grid.arrange(arrangeGrob(p1,p2,ncol=2),leg,heights=c(.8,.2))
  
  if (OUTPUT) ggsave(p,file=paste(folder,"plot_",modelName,"_2A.png",sep=""), width=7, height=3)
}
drawPlot2B <- function(da, modelName) {
  i <- theme(axis.text.x = element_text(angle = 15, vjust=.5))
  p1 <- ggplot(da, aes(x=mutation,y=steps,fill=mutation)) + geom_boxplot(aes(group=mutation), varwidth=T) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + nl + i #+ scale_y_continuous(trans='log10')
  p2 <- ggplot(da, aes(x=as.factor(steps),y=lev,fill=as.factor(steps))) + geom_boxplot(aes(group=as.factor(steps)),varwidth=T) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + nl #+ scale_y_continuous(trans='log10')

  p3 <- ggplot(da, aes(x=mutation,y=simpl,fill=mutation)) + geom_boxplot(aes(group=mutation),varwidth=T) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + nl + i #+ scale_y_continuous(trans='log10')
  p4 <- ggplot(da, aes(x=mutation,y=lev,fill=mutation)) + geom_boxplot(aes(group=mutation),varwidth=T) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + nl + i #+ scale_y_continuous(trans='log10')
  
  p <- grid.arrange(arrangeGrob(p1,p2,p3,p4,ncol=2))
  
  if (OUTPUT) ggsave(p,file=paste(folder,"plot_",modelName,"_2B.png",sep=""), width=7, height=6)
}

firstExperiment <- function() {
  doSql()
  drawPlot1(dat1, models[1])
  drawPlot1(dat2, models[3])
  
  #drawPlot1B() #useless
  drawPareto()
  drawTime(dat1, models[1])
  drawTime(dat2, models[3])
}

secondExperiment <- function() {
  doSql2()
  drawPlot2B(dat1, models[1])
  drawPlot2A(dat1, models[1])
  #drawPlot2(dat2, models[2])
  #drawPlot2(datGlob, "glob")
}

#firstExperiment()
#secondExperiment()

initstats3 <- function(m) {
  assign("dat1", read.csv(paste(folderInput,"exp3_",m,"_andor.csv",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
  assign("dat2", read.csv(paste(folderInput,"exp3_",m,"_orand.csv",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
  assign("dat3", read.csv(paste(folderInput,"exp3_",m,"_rand.csv",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
}

querystat3 <- function(tab,m,modelname) {
  print(paste(modelname,"-",m,"&",
    sqldf(paste("select count(*) from ",tab," where type='AND' and method='",m,"'",sep="")),
    "&",sqldf(paste("select round(avg(flit),1) from ",tab," where type='AND' and method='",m,"'",sep="")),
    "&",
    sqldf(paste("select count(*) from ",tab," where type='OR' and method='",m,"'",sep="")),
    "&",sqldf(paste("select round(avg(flit),1) from ",tab," where type='OR' and method='",m,"'",sep="")),
    "&",
    sqldf(paste("select count(*) from ",tab," where method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(avg(timeSelection),1) from ",tab," where type='AND' and method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(avg(timeSelection),1) from ",tab," where type='OR' and method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(avg(timeSelection),1) from ",tab," where method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(avg(timeSimplification),1) from ",tab," where type='AND' and method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(avg(timeSimplification),1) from ",tab," where type='OR' and method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(avg(timeSimplification),1) from ",tab," where method='",m,"'",sep="")),
    "&",
    sqldf(paste("select round(sum(timeTotal)) from ",tab," where method='",m,"'",sep="")),
    "&",
    sqldf(paste("select BFED from ",tab," where method='",m,"' order by step desc limit 1",sep="")),
    "&",
    sqldf(paste("select BFCD from ",tab," where method='",m,"' order by step desc limit 1",sep="")),
    "\\"
  ))
}

stats3 <- function() {
  for (j in 4:6) {
    initstats3(models[j]) # stats for experiment C
    print("modelname, #s, ss, #w, sw, #s+w, tsels, tselw, tsel, tsimpls, tsimplw, tsimpl, time, ed, cd")
    for (i in 1:1) {
      querystat3(paste("dat",i,sep=""),"Naive",models[j])
      querystat3(paste("dat",i,sep=""),"onlySelection",models[j])
      #querystat3(paste("dat",i,sep=""),"JBool",models[j])
      querystat3(paste("dat",i,sep=""),"Espresso",models[j])
    }
  }
}

stats1 <- function() {
  models <- c("Example","Register","Django","TightVNC","GPL_AHEAD","Rhiscom3")
  methods <- c("Naive","onlySelection","ATGT","Espresso","JBool","QM")
  types <- c("AND","OR")
  for (model in models) {
    assign("dat", read.csv(paste(folderInput,"exp1_",model,".txt",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
    for (rt in types) {
      s <- paste("&",rt,"&",sqldf(paste("select avg(flit) from dat where type='",rt,"' and method='onlySelection'",sep="")))
      for (m in methods) {
        s <- paste(s,
                   "&",
                   sqldf(paste("select avg(BFED) from dat where type='",rt,"' and method='",m,"'",sep="")),
                   "&",
                   sqldf(paste("select avg(BFCD) from dat where type='",rt,"' and method='",m,"'",sep="")),
                   "&",
                   sqldf(paste("select avg(timeTotal) from dat where type='",rt,"' and method='",m,"'",sep="")))
      }
      print(paste(s,"\\"))
    }
  }
}

mutatedModelsStats <- function() {
  assign("dat", read.csv(paste(folderInput,"mutatedModelsStats.csv",sep=""), encoding="UTF-8"), envir=.GlobalEnv)
  s <- sqldf(paste("select model, avg(features) || ' (' || min(features) || '-' || max(features) || ')', avg(constraints) || ' (' || min(constraints) || '-' || max(constraints) || ')', avg(literals) || ' (' || min(literals) || '-' || max(literals) || ')' from dat group by model",sep = ""))
  print(xtable(s, type = "latex"))
}

#stats1()
stats3()
#firstExperiment()
mutatedModelsStats()

# USELESS:
drawPlot1B <- function() {
  p1 <- ggplot(sqldf("select * from dat where method<>'None'"), aes(method,lev,fill=type)) + geom_boxplot(varwidth=T) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Levenshtein distance") + optLegend #+ scale_y_continuous(trans='log10')
  p2 <- ggplot(sqldf("select method,flit,avg(lev) as a from dat where method<>'None' and type='AND' group by method,flit"), aes(x=flit,y=a,color=method)) + geom_line(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Levenshtein distance AND") +bl+sm#+ scale_y_continuous(trans='log10')
  p3 <- ggplot(sqldf("select method,flit,avg(lev) as a from dat where method<>'None' and type='OR' group by method,flit"), aes(x=flit,y=a,color=method)) + geom_line(aes(group=method)) + scale_fill_grey(start = 0.5, end = 1) + theme_bw() + ggtitle("Levenshtein distance OR") +nl+sm#+ scale_y_continuous(trans='log10')
  leg<-g_legend(p2)
  p2<-p2+nl
  p <- grid.arrange(arrangeGrob(p1,arrangeGrob(p2,p3,ncol=2),leg,ncol=1,heights=c(4/9,4/9,1/9)))
  if (OUTPUT) ggsave(p,file=paste(folder,"plot_",model,"1B.png",sep=""), width=7, height=7)
}
