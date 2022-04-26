#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

long long i; //reserve i for loops and similar stuff
long long n; //number of particles to simulate
long long m; //total energy in the system, and also the max height, wich a particle can climb up to
//in this simulation epsilon is set to 1, without loss of generality!

//define function, that runs simulation
void do_simulation_one_time(long long *account){
  long long e_left = m, j=0, curr_pos;

  //create array which stores the position of each particle
  long long *position = calloc (n, sizeof(long long)); //array, in wich all the current ladder positions of the particles are stored in
  long long avg_e = m/n;

  //position the particles
  while(e_left>0 && j<n){
    curr_pos = rand() % avg_e + avg_e/2;
    position[j] = curr_pos;
    e_left -= curr_pos;
    j++;
  }
  if(e_left > 0){
    position[n-1] += e_left; //account for possible energy loss
  }
  else{
    position[j-1] -= e_left; //account for possible energy overshoot
  }

  //run simulation with enough steps
  for(j=0; j < m*m; j++){
    long long part1 = rand() % n, part2 = rand() % n; //choose two random particles
    if(position[part1] == 0){
      continue; //since particle 1 does not have any energy it could transfer, there is no need for a transfer
    }
    else{
      position[part1] -= 1;
      position[part2] += 1;
    }
  }

  //now transfer simulation results
  for(j=0; j < n; j++){
    account[position[j]] += 1;
  }

  free(position);
}

//define average function
double ladder_avg(double *array){
  double result = 0;
  for(i=0; i < m; i++){
    result += array[i] * i;
  }
  result /= n;
  return result;
}

//define std function
double ladder_std(double *array){
  double result = 0, avg = ladder_avg(array);
  for(i=0; i < m; i++){
    result += array[i] * pow(i-avg,2);
  }
  result = sqrt(result / (n-1));
  return result;
}

int main(){
  //set random seed
  srand(time(NULL));

  //define variables
  //in this simulation epsilon is set to 1, without loss of generality!
  long long precision; //number of iterations

  //input
  printf("How many particles should your simulation have? \nN = ");
  scanf("%lld", &n);
  printf("How much energy is there in the system? \ne = ");
  scanf("%lld", &m);
  printf("Approximately how precise do you want your ismulation results to be? \n");
  printf("percent precision = 3, pro mille = 4, in general the first x digits. \n");
  printf("num of digits = ");
  scanf("%lld", &precision);

  //prepare simulation
  //calaulate iterations of simulation
  long long iter = pow(100,precision);

  //count, how many particles there are on each ladder step
  long long *part_on_step = calloc(m+1, sizeof(long long)); //for counting the particles on each ladder step

  //run simulation
  for(i=0; i < iter; i++){
    do_simulation_one_time(part_on_step);
  }
  //calcualtions
  //calculate the theoretical particle density for a microcanonical ensemble
  double *thry_density = calloc (m+1, sizeof(double));
  thry_density[0] = (n-1)*1. / (m+n-1) * n; //particle density at ground level

  for(i=1; i < m+1; i++){
    thry_density[i] = thry_density[i-1] * (m+1-i)*1. / (m+n-1-i); //calculate expected particles per ladder step
  }

  //calculate avg particles on ladder steps gained from the do_simulation_one_time
  double *sim_density = calloc (m+1, sizeof(double));
  for(i=0; i < m+1; i++){
    sim_density[i] = part_on_step[i]*1. / iter;
  }

  //calculate the average height, and standart deviation of said height for the simulated particles
  double avg_h_sim = ladder_avg(sim_density);
  double std_h_sim = ladder_std(sim_density);

  //and for the theoretiical values
  double avg_h_thry = ladder_avg(thry_density);
  double std_h_thry = ladder_std(thry_density);

  //output
  printf("\n");
  printf("\t\tsimulated: \t  theory: \n");
  double testsum=0, testsum0=0;
  for(i=0; i<m+1; i++){
    printf("Step %3lld: \t%9lf \t%9lf \n", i, sim_density[i], thry_density[i]);
    testsum += sim_density[i];
    testsum0 += thry_density[i];
  }

  printf("\n");
  printf("avg heigth: \t%9.7lf \t%9.7lf \n", avg_h_sim, avg_h_thry);
  printf("std heigth: \t%9.6lf \t%9.6lf \n", std_h_sim, std_h_thry);

  free(part_on_step), free(sim_density), free(thry_density);
  return 0;
}
