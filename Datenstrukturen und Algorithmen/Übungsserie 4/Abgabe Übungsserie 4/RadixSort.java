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
