set xlabel "Number of documents"
set ylabel "Count"
cd 'D:\Source Code\loccs\results\document\hidesp\wordcount'
set terminal postscript eps lw 2 "Times New Roman" 22
set output "wordcount1.eps"
set key left top
plot "wordcount1.txt" using 4:3 w lp pt 5 lt 1 linecolor 1 title "Word Count"
set output