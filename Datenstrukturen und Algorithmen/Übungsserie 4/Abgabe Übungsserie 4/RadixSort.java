import java.util.ArrayList;
import java.util.LinkedList;

public class RadixSort {

    /*
     * Implements radix sort. Character arrays of different lengths
     * are ordered lexicographically, for example, a<ab<b. The
     * implementation doesn't use counting sort as a stable sort.
     * Instead, it simply uses an array of queues for each character
     * value.
     *
     * @param A an array of character arrays with different lengths
     * @param d the length of the longest String in A
     */
	
	public static int[] sortByLength(ArrayList<String> A, int maxLength) {
		
		int[] integer = new int[A.size()];
		ArrayList<String> result = new ArrayList<String>();
		result.addAll(A);
		
		for (int i = 0; i < A.size(); i++) {
			integer[i]= A.get(i).length();
		}
		
		int[] C = new int[maxLength];
		
		for (int j = 0; j < maxLength; j++) {
			C[j] = 0;
		}
		
		for (int j = 0; j < A.size(); j++) {
			C[integer[j] - 1] += 1;
		}
		
		for (int j = 1; j < maxLength; j++) {
			C[j] = C[j] + C[j - 1];
		}
		
		int[] C_copy = C.clone();

		for (int j = A.size() - 1; j >= 0; j--) {
			// we have to subtract 1 because for example the largest String T with possibly the size maxLength has C[T.length - 1] = 10000 and so we have to subtract 1.
			result.set(C[integer[j] - 1] - 1, A.get(j));
			C[integer[j] - 1] -= 1;
		}
		
		A.clear();
		A.addAll(result);
		
		return C_copy;
	}
	
    public static void radixSortImproved(ArrayList<String> A, int d)
    {
    	int index_start;
    	int[] counterArray = sortByLength(A, d);
    	
        ArrayList<LinkedList<String>> queues = new ArrayList<LinkedList<String>>();
        // 27 queues for 26 characters plus 'empty' character
        for (int i=0; i<27; ++i) {
            queues.add(new LinkedList<String>());
        }

        // for all positions from right to left
        for(int j=d-1; j>=0; j--)
        {
        	if (j == 0) {
        		index_start = 0;
        	}
        	else {
        		index_start = counterArray[j - 1];
        	}
        	
            // initialize empty queues
            //for(int i=0; i<27; i++) queues.set(i, new LinkedList<char[]>());
            for(int i=0; i<27; i++) queues.get(i).clear();

            // place each character array in correct queue
            for(int i=index_start; i<A.size(); i++)
            {
            	queues.get(A.get(i).charAt(j)-'a'+1).addLast(A.get(i));
            }

            // traverse queues
            int n = index_start;
            for(int i=0; i<27; i++)
            {
                while(queues.get(i).size() > 0)
                {
                    A.set(n, queues.get(i).removeFirst());
                    n++;
                }
            }
        }
    }
    public static void radixSort(ArrayList<String> A, int d)
    {
        ArrayList<LinkedList<String>> queues = new ArrayList<>();
        // 27 queues for 26 characters plus 'empty' character
        for (int i=0; i<27; ++i) {
            queues.add(new LinkedList<String>());
        }

        // for all positions from right to left
        for(int j=d-1; j>=0; j--)
        {
            // initialize empty queues
            //for(int i=0; i<27; i++) queues.set(i, new LinkedList<char[]>());
            for(int i=0; i<27; i++) queues.get(i).clear();

            // place each character array in correct queue
            for(int i=0; i<A.size(); i++)
            {
                if(j<A.get(i).length())
                {
                    // characters 'a'-'z'
                    queues.get(A.get(i).charAt(j)-'a'+1).addLast(A.get(i));
                }
                else
                {
                    // character array is shorter than current position.
                    // place it in 'empty' queue. 'emtpy' queue is queue 0
                    // to get lexicographically correct results, i.e., a<ab.
                    queues.get(0).addLast(A.get(i));
                }
            }

            // traverse queues
            int n = 0;
            for(int i=0; i<27; i++)
            {
                while(queues.get(i).size() > 0)
                {
                    A.set(n, queues.get(i).removeFirst());
                    n++;
                }
            }
        }
    }
}
