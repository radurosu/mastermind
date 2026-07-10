r=fopen('c:\teza\fromnet\time_mat.txt','r')
a=fscanf(r,'%f\n',[12,8])
a=a'
meshz(a)
view(25,25);
TITLE('Time in seconds per 100 solved codes')
ZLABEL('Number of guesses')
YLABEL('Positions')
Xlabel('Colors')

loglog(a(:,1),a(:,2)/100,'ro')

 plot(sqrt(a(:,1)),a(:,2)/100,'ro')
» plot(log(a(:,1)),a(:,2)/100,'ro')
» loglog(sqrt(a(:,1)),a(:,2)/100,'ro')
» plot(log(a(:,1)),a(:,2)/100,'ro')