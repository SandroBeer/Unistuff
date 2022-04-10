#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

double factorial(int x){
  double res=1.;
  int i;
  for(i=x; i>1; i--){
    res*=i;
  }
  return res;
}

int main(){
  //set random seed
  srand(time(NULL));

  //define variables
  //in this simulation epsilon is set to 1, without loss of generality!
  int n; //number of particles to simulate
  int m; //total energy in the system, and also the max height, wich a particle can climb up to
  long long iter;
  int i; //reserve i for loops and similar stuff

  //input
  printf("How many particles should your simulation have? \nN = ");
  scanf("%d", &n);
  printf("How much energy is there in the system? \ne = ");
  scanf("%d", &m);
  printf("How many iterations do you want to simulate? \nnum of iterations = ");
  scanf("%lld", &iter);

  //prepare simulation
  int position[n]; //array, in wich all the current ladder positions of the particles are stored in

  //place particles on ladder steps
  int to_be_distributed_m = m; //account the energy e, wich can still be distributed
  int e_for_particle; //energy for current particle
  for(i=0; i < n-1; i++){
    e_for_particle = rand() % (to_be_distributed_m+1); //give current particle a random amount of energy
    position[i] = e_for_particle;
    to_be_distributed_m -= e_for_particle; //subtract energy given to particle from to be distributed energy
  }
  position[n-1] = to_be_distributed_m; //make sure, that all the energy gets distributed

  //run simulation
  for(i=0; i < iter; i++){
    int part1 = rand() % n, part2 = rand() % n; //choose two random particles
    if(position[part1] == 0){
      continue; //since particle 1 does not have any energy it could transfer, there is no need for a transfer
    }
    else{
      position[part1] -= 1;
      position[part2] += 1;
    }
  }

  //count, how many particles there are on each ladder step
  int num_of_part[m]; //keep track of said criteria
  memset(num_of_part, 0, m * sizeof(int));

  for(i=0; i < n; i++){
    num_of_part[position[i]] += 1;
  }

  //calcualtions
  //calculate the theoretical particle density for the first 10 steps
  double part_density[10];
  part_density[0] = (n-1)*1. / (m+n-1) * n;
  for(i=1; i < 10; i++){
    part_density[i] = part_density[i-1] * (m+1-i)*1. / (m+n-1-i); //calculate expected particles per ladder step
  }

  for(i=0; i<10; i++){
    printf("Step %d \t measured: %05d \t expected %5.2lf \n",i, num_of_part[i], part_density[i]);
  }
  return 0;
}
