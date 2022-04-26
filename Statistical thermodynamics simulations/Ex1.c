#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

//define random function, since rand() only goes from 0 to 32767 or 2^15
long long big_rand(){
  long long result = 0;
  result += rand() % 10000;
  result += 10000 * (rand() % 10000);
  result += 100000000 * (rand() % 10000);
  return llabs(result); //result is in range of 0 to 10^12-1
}

//sum of form k^exponent*a*e^b
double i_to_pow_times_exp_sum(long long min, long long max, int exponent, double a, double b){
  double i;
  double sum=0;
  for(i=min; i < max+1; i++){
    sum += pow(i,exponent)*a*exp(b*i);
  }
  return sum;
}

int main(){
  //set random seed
  srand(time(NULL));

  //define variables
  double beta, epsilon, beta_epsilon;
  long long n; //number of Ladder steps {0,1,...,N}
  long long iter; //total number of iterations
  long long position; //current position of particle {0,...,N}
  long long i; //reserve i for loops and similar stuff

  //input
  printf("How many steps should your ladder have? \nN = ");
  scanf("%lld", &n);
  printf("How many iterations should your simulation run? \nnum of iterations = ");
  scanf("%lld", &iter);
  printf("What value should beta equal to? \nb = ");
  scanf("%lf", &beta);
  printf("What value should epsilon equal to? \ne = ");
  scanf("%lf", &epsilon);

  //prepare the simulation
  beta_epsilon = beta*epsilon;
  double q = 1. / (1+exp(beta_epsilon)); //probability for transition step n -> n+1
  long long *count = calloc (n+1, sizeof(long long)); //add array, wich counts, how many times the particle visited each ladder-steps

  //run simulation
  //set particle on a random steps
  position = big_rand() % (n+1);
  for(i=0; i < iter; i++){
    //get random number between 0 and 1 (in reality a rand. number between 1 and 2^32 is divided by 2^32)
    double p = rand()*1. / RAND_MAX;
    if(position == n && p <= q){
      //particle can't go higher
      count[position] += 1;
      }
    else if(position == 0 && p > q){
      //particle can't go lower
      count[position] += 1;
      }
    else if(p <= q){
      position += 1;
      count[position] += 1;
      }
    else{
      position += -1;
      count[position] += 1;
      }
    }

    //calculations
    //calculate the equilibrium probability
    double x = q / (1-q);
    double *rho = calloc(n+1, sizeof(double));
    //first, calculate rho_0 (similar to Ex.4 Home1)
    double rho_0 = (1-x) / (1-pow(x,n+1)); //using the geometric series we get

    //fill out the equilibrium probabilities in to the array
      for(i=0; i < n+1; i++){
      rho[i] = pow(x,i)*rho_0*iter;
    }

    //calculate the average energy and standart deviation of the particle simulated
    double avg_e_sim = 0, std_e_sim = 0;

    //calculate the average
    for(i=0; i < n+1; i++){
      avg_e_sim += count[i]*i*epsilon;
    }
    avg_e_sim /= iter;

    //calculate std
    for(i=0; i < n+1; i++){
      std_e_sim += count[i]*pow(avg_e_sim-i*epsilon,2);
    }
    std_e_sim = sqrt(std_e_sim / (iter-1));


    //calculate the theoretical energy <H> and the standart deviation <dH>
    //first we calculate some useful sums
    double e_be = exp(-beta_epsilon);
    double z = (1-pow(e_be, n+1)) / (1-e_be);
    double dz = i_to_pow_times_exp_sum(1, n, 1, -epsilon, -beta_epsilon);
    double ddz = i_to_pow_times_exp_sum(1, n, 2, pow(epsilon,2), -beta_epsilon);

    double h = -1. / z * dz; // <H>
    double dh = sqrt(1. / z * ddz -1. / pow(z,2) * pow(dz,2)); //<dH>

    //output
    printf("\t\t measured: \ttheory: \n");
    for(i=0;i<n+1;i++){
      printf("Step %03lld: \t%10lld \t%.0lf \n",i, count[i], rho[i]);
    }
    printf("\n");
    printf("avg E: \t\t%.6lf \t%.6lf \n", avg_e_sim, h);
    printf("std E: \t\t%.6lf \t%.6lf\n", std_e_sim, dh);

  return 0;
}
