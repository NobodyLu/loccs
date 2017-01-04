set xlabel "Number of documents"
set ylabel "Count"
cd 'D:\Source Code\loccs\results\document\hidesp\group'
set terminal postscript eps lw 2 "Times New Roman" 22
set output "good1.eps"
set key left top
plot "group1.txt" using 5:4w lp pt 5 lt 1 linecolor 1 title "Total Words",\
"group1.txt" using 5:3 w lp pt 6 lt 3 linecolor 4 title "Search Pattern Hidden Words"
set output