#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

long long i; //reserve i for loops and similar stuff

//define random function, since rand() only goes from 0 to 32767 or 2^15
long long big_rand(){
  long long result = 0;
  result += rand() % 10000;
  result += 10000 * (rand() % 10000);
  result += 100000000 * (rand() % 10000);
  return llabs(result); //result is in range of 0 to 10^12-1
}

//define average function
double ladder_avg(long long *array, long long length_of_array, long long tot_part){
  double result = 0;
  for(i=0; i < length_of_array; i++){
    result += array[i] * i;
  }
  result /= tot_part;
  return result;
}

//define std function
double ladder_std(long long *array, long long length, long long tot_part){
  double result = 0, avg = ladder_avg(array, length, tot_part);
  for(i=0; i < length; i++){
    result += array[i] * pow(i-avg,2);
  }
  result = sqrt(result / (tot_part-1));
  return result;
}

int main(){
  //set random seed
  srand(time(NULL));

  //define variables
  //in this simulation epsilon is set to 1, without loss of generality!
  long long n; //number of particles to simulate
  long long m; //total energy in the system, and also the max height, wich a particle can climb up to
  long long iter; //number of iterations

  //input
  printf("How many particles should your simulation have? \nN = ");
  scanf("%lld", &n);
  printf("How much energy is there in the system? \ne = ");
  scanf("%lld", &m);
  printf("How many iterations do you want to simulate? \nnum of iterations = ");
  scanf("%lld", &iter);

  //prepare simulation
  long long *position; //array, in wich all the current ladder positions of the particles are stored in
  position = (long long*) malloc (n * sizeof(long long));

  //place particles on ladder steps
  long long to_be_distributed_m = m; //account the energy e, wich can still be distributed
  long long e_for_particle; //energy for current particle
  long long avg_e = m/n +1;

  for(i=0; i < n-1; i++){
    e_for_particle = rand() % avg_e + rand() % avg_e;
    position[i] = e_for_particle;//e_for_particle;
    to_be_distributed_m -= e_for_particle; //subtract energy given to particle from to be distributed energy
  }
  position[n-1] = to_be_distributed_m; //make sure, that all the energy gets distributed

  //run simulation
  for(i=0; i < iter; i++){
    long long part1 = big_rand() % n, part2 = big_rand() % n; //choose two random particles
    if(position[part1] == 0){
      continue; //since particle 1 does not have any energy it could transfer, there is no need for a transfer
    }
    else{
      position[part1] -= 1;
      position[part2] += 1;
    }
  }

  //count, how many particles there are on each ladder step
  long long *num_of_part; //for counting the particles on each ladder step
  num_of_part = (long long*) malloc (m * sizeof(long long));
  memset(num_of_part, 0, m * sizeof(long long)); //fill with 0's

  for(i=0; i < n; i++){
    num_of_part[position[i]] += 1;
  }

  //calcualtions
  //calculate the theoretical particle density for a microcanonical ensemble
  long long *part_density;
  part_density = (long long*) malloc (m * sizeof(long long));
  part_density[0] = (n-1)*1. / (m+n-1) * n; //particle density at ground level

  for(i=1; i < m; i++){
    part_density[i] = part_density[i-1] * (m+1-i)*1. / (m+n-1-i); //calculate expected particles per ladder step
  }

  //calculate the average height, and standart deviation of said height for the simulated particles
  double avg_h_sim = ladder_avg(num_of_part, m, n);
  double std_h_sim = ladder_std(num_of_part, m, n);

  //and for the theoretiical values
  double avg_h_thry = ladder_avg(part_density, m, n);
  double std_h_thry = ladder_std(part_density, m, n);

  //output
  printf("\n");
  printf("\t\tsimulated: \t  theory: \n");
  for(i=0; i<26; i++){
    printf("Step %2lld: \t%9lld \t%9lld \n",i, num_of_part[i], part_density[i]);
  }

  printf("\n");
  printf("avg heigth: \t%9.7lf \t%9.7lf \n", avg_h_sim, avg_h_thry);
  printf("std heigth: \t%9.6lf \t%9.6lf \n", std_h_sim, std_h_thry);
  //and theory for the first 10 steps:
  printf("\n");
  printf("Now for the first 10 steps: \n");

  //count, how many particles there are up t the 10th step:
  long long step_10_sim=0, step_10_thry=0;
  for(i=0; i < 11; i++){
    step_10_sim += num_of_part[i];
    step_10_thry += part_density[i];
  }
  printf("avg heigth: \t%9.7lf \t%9.7lf \n", ladder_avg(num_of_part, 11, step_10_sim), ladder_avg(part_density, 11, step_10_thry));
  printf("std heigth: \t%9.6lf \t%9.6lf \n", ladder_std(num_of_part, 11, step_10_sim), ladder_std(part_density, 11, step_10_thry));

  free(position), free(num_of_part), free(part_density);
  return 0;
}
