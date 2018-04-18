package com.power.common.utils.lunar;
public class LunarUtilDemo {

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		runExample();
	}

	/**
	 * Run example.
	 */
	public static void runExample() {
//		// 公历转农历
//		System.out.println("---------------------");
//		System.out.println("公历转农历:");
//		System.out.println("---------------------");
//		{
//			System.out.println("Example 1: 公历1990年6月23日转农历");
//			Lunar lunar = LunarUtil.solar2Lunar(1990, 6, 23).get();
//			System.out.println(lunar.toString());
//			System.out.println("年: " + lunar.getYear() + ", 月: " + lunar.getMonth() + ", 日: " + lunar.getDay() + ", 闰月: "
//			        + (lunar.isLeap() ? "是" : "否"));
//			System.out.println();
//		}
//		{
//			System.out.println("Example 2: 公历2033年12月22日转农历");
//			Lunar lunar = LunarUtil.solar2Lunar(2033, 12, 22).get();
//			System.out.println(lunar.toString());
//			System.out.println("年: " + lunar.getYear() + ", 月: " + lunar.getMonth() + ", 日: " + lunar.getDay() + ", 闰月: "
//			        + (lunar.isLeap() ? "是" : "否"));
//			System.out.println("闰月: " + (lunar.isLeap() ? "是" : "否"));
//			System.out.println();
//		}
//
//		// 农历转公历
//		System.out.println("---------------------");
//		System.out.println("农历转公历:");
//		System.out.println("---------------------");
//		{
//			Solar solar = LunarUtil.lunar2Solar(1990, 5, 1, true).get();
//			System.out.println("Example 1: 农历1990年闰五月初一转公历");
//			System.out.println(solar.toString());
//			System.out.println("年: " + solar.getYear() + ", 月: " + solar.getMonth() + ", 日: " + solar.getDay());
//			System.out.println();
//		}
//		{
//			Solar solar = LunarUtil.lunar2Solar(2033, 11, 1, true).get();
//			System.out.println("Example 2: 农历2033年闰十一月初一转公历");
//			System.out.println(solar.toString());
//			System.out.println("年: " + solar.getYear() + ", 月: " + solar.getMonth() + ", 日: " + solar.getDay());
//			System.out.println();
//		}
		
		Solar solar = LunarUtil.lunar2Solar(2016, 4, 1).get();
		System.out.println(solar.toString());
		System.out.println("年: " + solar.getYear() + ", 月: " + solar.getMonth() + ", 日: " + solar.getDay());
	}

}