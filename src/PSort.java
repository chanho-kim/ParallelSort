import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PSort {
	public static ExecutorService executor = Executors.newCachedThreadPool();
	
	public static void parallelSort(int[] A, int begin, int end) {
		if(A == null || end <= 0){
			System.out.println("ILLEGAL INPUT.");
			return;
		}
		
		QSort qsort = new QSort(A, begin, end-1);
		Future<?> done = executor.submit(qsort);
		
		//spin until it's done
		try {
			while(done.get() != null){}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		executor.shutdown();
	}
}

class QSort implements Runnable{
	
	private static int[] array;
	
	int left, right;
	
	//A = array, begin = beginning index, end = ending index
	public QSort(int[] A, int begin, int end){
		array = A;
		left = begin;
		right = end;
	}
	
	public QSort(int begin, int end){
		left = begin;
		right = end;
	}
	
	public void run() {
		Future<?> a = null;
		Future<?> b = null;
		
		//generic quicksort algorithm
		int i = left;
		int j = right;
		int pivot = array[(left + right) / 2];
		while(i <= j) {
			while(array[i] < pivot) i+=1;
			while(array[j] > pivot) j-=1;
			if(i <= j) {
				int temp = array[i];
				array[i] = array[j];
				array[j] = temp;
				i+=1;
				j-=1;
			}
		}
		
		if(left < j) {
			QSort qsort = new QSort(left, j);
			a = PSort.executor.submit(qsort);
		}
		if (i < right) {
			QSort qsort2 = new QSort(i, right);
			b = PSort.executor.submit(qsort2);
		}
		
		//if there is work, spin until it is finished
		if(a != null) {
			try {
				while(a.get() != null) {}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		if(b != null) {
			try {
				while(b.get() != null) {}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]){
		 System.out.println("start");
	     int A[] = {}; 
	     PSort.parallelSort(A, 0, A.length);
	     for(int i=0; i<A.length; i+=1){
	    	 System.out.println(A[i]);
	     }
	}
	
}