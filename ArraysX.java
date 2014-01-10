import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ArraysX {

	public static String[] connectArray(String[]... arr){
		ArrayList<String> resultAL = new ArrayList<>();


		for(int i = 0; i < arr.length; i++){
			for(int j = 0; j < arr[0].length; j++){
				resultAL.add(arr[i][j]);
			}
		}

		String[] result = (String[])resultAL.toArray(new String[0]);

		return result;
	}

	public static int countNaN0(double[] arr) {
		int counter = 0;

		for(double n: arr){
			if(n == 0 || Double.isNaN(n))
				counter++;
		}

		return counter;
	}

	public static String[][] dlmRead(String filename, String dlm, String encode){

		try {
			ArrayList<String[]> lines = new ArrayList<String[]>();

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encode));

			String line = "";
			while ((line = br.readLine()) != null) {
				lines.add(line.split(dlm));
			}
			br.close();

			String[][] result = new String[lines.size()][getLongestElement(lines)];

			for(int i = 0; i < result.length; i++){
				for(int j = 0; j < result[0].length; j++){
					if(lines.get(i).length <= j)
						result[i][j] = "";
					else
						result[i][j] = lines.get(i)[j].trim();
				}
			}

			return result;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String[][] flip(String[][] mat) {
		String[][] ans = new String[mat[0].length][mat.length];

		for(int i = 0; i < mat.length; i++){
			for(int j = 0; j < mat[i].length; j++){
				ans[j][i] = mat[i][j];
			}
		}	

		return ans;
	}

	public static double get0Mean(double[] arr) {
		double ans = 0;
		int counter = 1;

		for(int i = 0; i < arr.length; i ++){
			if(arr[i] != 0 && arr[i] != Double.NaN){
				ans += arr[i];
				counter++;
			}
		}

		return ans/((double)counter-1.0);
	}

	public static double get0Median(float[] tmpf0) {
		ArrayList<Double> n0arr = new ArrayList<>();

		for(int i = 0; i < tmpf0.length; i++){
			if(tmpf0[i] != 0 && tmpf0[i] != Double.NaN){
				n0arr.add((double)tmpf0[i]);
			}
		}

		Collections.sort(n0arr);

		if(n0arr.size() == 1)
			return n0arr.get(0);
		if(n0arr.size() == 2)
			return (n0arr.get(0) + n0arr.get(1))/2.0;
		if(n0arr.size() == 0)
			return Double.NaN;


		if(n0arr.size() % 2 == 0){
			return (n0arr.get(n0arr.size()/2) + n0arr.get(n0arr.size()/2 + 1))/2.0;
		}
		else
			return (n0arr.get((int)(n0arr.size()/2)));
	}

	public static double get0Median(double[] arr, double thr) {
		ArrayList<Double> n0arr = new ArrayList<>();

		for(double n: arr){
			if(n != 0 && !Double.isNaN(n)){
				n0arr.add(n);
			}
		}

		Collections.sort(n0arr);

		if(n0arr.size() < arr.length*thr)
			return Double.NaN;

		if(n0arr.size() % 2 == 0){
			return (n0arr.get(n0arr.size()/2) + n0arr.get(n0arr.size()/2 + 1))/2.0;
		}
		else
			return (n0arr.get((int)(n0arr.size()/2)));
	}

	public static double get0Mode(double[] arr, double delimiter, double thr){
		double max = ArraysX.max(arr);

		double[] hgraph = new double[(int) (max/delimiter)+1];

		for(double n: arr){
			if(!Double.isNaN(n) && n != 0)
				hgraph[(int)(n/delimiter)]++;
		}

		int sum = 0;
		for(double n:hgraph){
			sum += n;
		}

		if(sum > arr.length * thr)
			return maxIndex(hgraph)*delimiter;
		else {
			return Double.NaN;
		}
	}

	public static double[] get0Quod(double[] arr, double thr) {
		ArrayList<Double> n0arr = new ArrayList<>();

		for(int i = 0; i < arr.length; i++){
			if(arr[i] != 0 && !Double.isNaN(arr[i])){
				n0arr.add(arr[i]);
			}
		}

		Collections.sort(n0arr);
		double[] ans = {Double.NaN, Double.NaN, Double.NaN};

		if(n0arr.size() <= arr.length*thr || n0arr.size() <= Math.round(n0arr.size()*0.75))
			return ans;
		else{
			int q1 = (int) Math.round(n0arr.size()*0.25);
			int q2 = (int) Math.round(n0arr.size()*0.5);
			int q3 = (int) Math.round(n0arr.size()*0.75);

			ans[0] = n0arr.get(q1);
			ans[1] = n0arr.get(q2);
			ans[2] = n0arr.get(q3);

			return ans;
		}
	}

	public static double[] getCol(double[][] matrix, int col) {
		double[] ans = new double[matrix.length];

		for(int i = 0; i < ans.length; i++){
			ans[i] = matrix[i][col];
		}

		return ans;
	}

	public static String[] getCol(String[][] matrix, int col){

		String[] ans = new String[matrix.length];

		for(int i = 0; i < ans.length; i++){
			ans[i] = matrix[i][col];
		}

		return ans;
	}

	public static String[] getCol(String[][] matrix, int col, int st, int ed) {
		String[] ans = new String[ed-st+1];

		for(int i = st; i <= ed; i++){
			ans[i-st] = matrix[i][col];
		}

		return ans;
	}


	public static int getLongestElement(ArrayList<String[]> al){
		int result = 0;

		for(int i = 0; i < al.size(); i++){
			if(result < al.get(i).length)
				result = al.get(i).length;
		}

		return result;
	}


	public static double getMean(double[] arr) {
		double ans = 0;

		for(int i = 0; i < arr.length; i ++){
			ans += arr[i];
		}

		return ans/(double)arr.length;
	}

	public static double getMean(double[] arr, int st, int ed) {
		double ans = 0;

		for(int i = st; i <= ed; i ++){
			ans += arr[i];
		}

		return ans/(double)(ed-st+1);
	}

	public static double getSum(double[] arr, int st, int ed) {
		double ans = 0;

		for(int i = st; i <= ed; i ++){
			ans += arr[i];
		}

		return ans;
	}

	public static double max(double[] arr){

		double ans = Double.NEGATIVE_INFINITY;

		for(double n: arr){
			if(n > ans){
				ans = n;
			}
		}

		return ans;
	}

	public static int maxIndex(double[] arr) {

		double val = Double.NEGATIVE_INFINITY;
		int ans = -1;

		for(int i = 0; i < arr.length; i++){
			if(arr[i] > val){
				val = arr[i];
				ans = i;
			}
		}

		return ans;
	}

	public static double min(double[] arr) {
		double ans = Double.POSITIVE_INFINITY;

		for(double n: arr){
			if(n < ans){
				ans = n;
			}
		}

		return ans;
	}

	public static double min0(double[] arr){
		return min(remove0(arr));
	}

	public static double[] parseDouble(String[] strings) {

		double[] ans = new double[strings.length];

		for(int i = 0; i < ans.length; i++){
			ans[i] = Double.parseDouble(strings[i]);
		}

		return ans;		
	}

	public static double[][] parseDouble(String[][] strArrs) {

		double[][] ans = new double[strArrs.length][strArrs[0].length];

		for(int i = 0 ; i < ans.length; i++){
			ans[i] = parseDouble(strArrs[i]);
		}

		return ans;
	}


	public static float[][] parseFloat(String[][] strArrs) {

		float[][] ans = new float[strArrs.length][strArrs[0].length];

		for(int i = 0; i < ans.length; i++){
			for(int j = 0; j < ans[0].length; j++){
				ans[i][j] = Float.parseFloat(strArrs[i][j]);			
			}			
		}


		return ans;
	}


	public static int[][] ParseInt(String[][] arr) {
		int[][] ans = new int[arr.length][arr[0].length];

		for(int i = 0; i < ans.length; i++){
			for(int j = 0; j < ans[i].length; j++){
				ans[i][j] = Integer.valueOf(arr[i][j]);
			}
		}

		return ans;
	}

	public static String[] parseString(double[] strings) {

		String[] ans = new String[strings.length];

		for(int i = 0; i < ans.length; i++){
			ans[i] = Double.toString(strings[i]);
		}

		return ans;
	}

	private static String[][] parseString(int[][] arr) {
		String[][] ans = new String[arr.length][arr[0].length];

		for(int i = 0; i < ans.length; i++){
			for(int j = 0; j < ans[i].length; j++){
				ans[i][j] = Integer.toString(arr[i][j]);
			}
		}

		return ans;
	}

	public static double[] remove0(double[] arr){

		ArrayList<Double> non0 = new ArrayList<>();

		for(double n: arr){
			if(n != 0){
				non0.add(n);
			}
		}

		double[] ans = new double[non0.size()];

		for(int i = 0; i < ans.length; i++){
			ans[i] = non0.get(i);
		}

		return ans;

	}

	public static double[] toArray(ArrayList<Double> arrList) {
		Double[] Darr = arrList.toArray(new Double[arrList.size()]);
		double[] ans = new double[Darr.length];

		for(int i = 0; i < Darr.length; i++){
			ans[i] = Darr[i];
		}

		return ans;
	}

	public static void writeCSV(String filename, double[] arr) {
		writeCSV(filename, parseString(arr));
	}

	public static void writeCSV(String filename, double[][] dat) {

		String[][] strArr = new String[dat.length][dat[0].length];

		for(int i = 0; i < strArr.length; i++){
			for(int j = 0; j < strArr[i].length; j++){
				strArr[i][j] = Double.toString(dat[i][j]);
			}
		}

		writeCSV(filename, strArr);
	}

	public static void writeCSV(String filename, float[][] dat) {
		String[][] strArr = new String[dat.length][dat[0].length];

		for(int i = 0; i < strArr.length; i++){
			for(int j = 0; j < strArr[i].length; j++){
				strArr[i][j] = Float.toString(dat[i][j]);
			}
		}

		writeCSV(filename, strArr);
	}

	public static void writeCSV(String filename, int[][] arr) {
		writeCSV(filename, parseString(arr));		
	}

	public static void writeCSV(String filename, String[] data) {
		try{
			File file = new File(filename);
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

			for(int i = 0; i < data.length; i++){
				pw.println(data[i]);
			}

			pw.close();
		}catch(IOException e){
			System.out.println(e);
		}

	}

	public static void writeCSV(String filename, String[][] data){
		String[] lines = new String[data.length];

		for(int i = 0; i < data.length; i++){
			lines[i] = data[i][0];
			for(int j = 1; j < data[i].length; j++){
				lines[i] = lines[i] + "," + data[i][j];
			}
		}

		writeCSV(filename, lines);
	}

	public static float[] parseFloat(String[] strings) {
		float[] ans = new float[strings.length];

		for(int i = 0; i < ans.length; i++){
			ans[i] = Float.parseFloat(strings[i]);
		}

		return ans;		
	}

	public static ArrayList<String> readLines(String filename, String encode) {

		try {
			ArrayList<String> lines = new ArrayList<String>();

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), encode));

			String line = "";
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();

			return lines;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeCSV(String filename, short[] arr) {
		writeCSV(filename, parseString(arr));
	}

	public static String[] parseString(short[] strings) {

		String[] ans = new String[strings.length];

		for(int i = 0; i < ans.length; i++){
			ans[i] = Short.toString(strings[i]);
		}

		return ans;
	}

	public static String[][] WebdlmRead(String address, String dlm, String encode) {

		try {
			ArrayList<String[]> lines = new ArrayList<String[]>();

			URL url = new URL(address);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();

			BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), encode));

			String line = "";
			while ((line = br.readLine()) != null) {
				lines.add(line.split(dlm));
			}
			br.close();

			String[][] result = new String[lines.size()][getLongestElement(lines)];

			for(int i = 0; i < result.length; i++){
				for(int j = 0; j < result[0].length; j++){
					if(lines.get(i).length <= j)
						result[i][j] = "";
					else
						result[i][j] = lines.get(i)[j].trim();
				}
			}

			return result;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
