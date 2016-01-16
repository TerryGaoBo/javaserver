package com.dol.cdf.common;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final  class Util {
	
	
	public final static String GM_COMMAND_KEY = "aploaaploaaploa";
	
	public static String replaceLastChar(String source, Object obj ){
		return source.substring(0,source.length()-1)+obj.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(Util.replaceLastChar("gem_1", 11));
	}
	
	public static Set<Integer> toIntSet(String str, String spliter) {
		Set<Integer> ints = new HashSet<Integer>();
		if (str != null && !str.isEmpty()) {
			for(String intStr : str.split(spliter)){
				ints.add(Integer.parseInt(intStr));
			}
		}
		return ints;
	}
	
    public static int[] arrayCopy(int[] array1, int[] array2) {
        if (array1 == null) {
            return  array2.clone();
        } else if (array2 == null) {
            return array1.clone();
        }
        int[] joinedArray = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
	
	public static boolean isSameDay(long time1, long time2) {
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = new Date(time1);
		Date date2 = new Date(time2);
		String day1 = sdf.format(date1);
		String day2 = sdf.format(date2);
		return day1.equals(day2);
	}
	
	public static int indexOf(int[] oldLuckQuals, int qual) {
		int here = -1;
		for (int i = 0; i < oldLuckQuals.length; ++i) {
			if (oldLuckQuals[i] == qual) {
				here = i;
				break;
			}
		}
		return here;
	}
	
	public static class ValueComparator implements Comparator<String> {
		 
		Map<String, Long> base;
 
		public ValueComparator(Map<String, Long> base) {
			this.base = base;
		}
 
		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		@Override
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}
}
