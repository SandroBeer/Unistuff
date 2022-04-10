#include <stdio.h>
#include <math.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

//exponantial sum function
double exp_sum(int min, int max, double a, double b){
  double i;
  double sum=0;
  for(i=min; i<max+1; i++){
    sum += a*exp(b*i);
  }
  return sum;
}

double i_to_pow_times_exp_sum(int min, int max, int exponent, double a, double b){
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
  double beta, epsilon, beta_epsilon; //beta times epsilon
  int n; //number of Ladder steps {0,1,...,N}
  long long iter; //total number of iterations
  int position; //current position of particle {0,...,N}

  //input
  printf("How many steps should your ladder have? \nN = ");
  scanf("%d", &n);
  printf("How many iterations should your simulation run? \nnum of iterations = ");
  scanf("%lld", &iter);
  printf("What value should beta equal to? \nb = ");
  scanf("%lf", &beta);
  printf("What value should epsilon equal to? \ne = ");
  scanf("%lf", &epsilon);

  //prepare the simulation
  beta_epsilon = beta*epsilon;
  double q = 1. / (1+exp(beta_epsilon)); //probability for transition step n -> n+1
  //add array, wich counts, how many times the particle visited each ladder-steps
  long long count[n+1], j;
  memset(count, 0, (n+1) * sizeof(long long)); //initialize array count with 0's

  //run simulation
  //set particle on a random steps
  position = rand() % (n+1);
  for(j=0; j < iter; j++){
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
    double rho[n+1], rho_0 = 0, x = q / (1-q);
    //first, calculate rho_0 (similar to Ex.4 Home1)
    //using the geometric series we get
    rho_0 = (1-x) / (1-pow(x,n+1));

    //fill out the equilibrium probabilities in to the array
    int i;
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
    double z = exp_sum(0, n-1 , 1., -beta_epsilon);
    double dz = i_to_pow_times_exp_sum(1, n-1, 1, -epsilon, -beta_epsilon);
    double ddz = i_to_pow_times_exp_sum(1, n-1, 2, pow(epsilon,2), -beta_epsilon);

    double h = -1. / z * dz; // <H>
    double dh = sqrt(1. / z * ddz -1. / pow(z,2) * pow(dz,2)); //<dH>

    //output
    for(i=0;i<n+1;i++){
      printf("Step %03d: \tmeasured: %010lld \texpected: %.0lf \n",i, count[i], rho[i]);
    }
    printf("\n");
    printf("simulated:  \tavg(E): %.6lf \tstd(E): %.6lf \n", avg_e_sim, std_e_sim);
    printf("theoretical:\t<H>:    %.6lf \t<dH>:   %.6lf", h, dh);

  return 0;
}
