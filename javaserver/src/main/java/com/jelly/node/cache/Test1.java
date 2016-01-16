package com.jelly.node.cache;

import io.nadron.app.impl.DefaultPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.util.CollectionUtils;

import com.dol.cdf.common.CircularArrayList;
import com.dol.cdf.common.DynamicJsonProperty;
import com.dol.cdf.common.Rnd;
import com.dol.cdf.common.StringHelper;
import com.dol.cdf.common.TimeUtil;
import com.dol.cdf.common.bean.QualityRef;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.common.config.ItemConstant;
import com.dol.cdf.common.crypto.Guid;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.AtomicLongMap;
import com.jelly.hero.Hero;
import com.jelly.node.datastore.mapper.RoleEntity;
import com.jelly.player.ItemInstance;

public class Test1 {
	protected static final ObjectMapper jackson = new ObjectMapper();
	private final static ObjectNode json = jackson.createObjectNode();

	public static class Items {
		ItemInstance[] its = new ItemInstance[100];

		public ItemInstance[] getIts() {
			return its;
		}

		public void setIts(ItemInstance[] its) {
			this.its = its;
		}

	}

	public static class Items1 {
		List<ItemInstance> its = new ArrayList<ItemInstance>(100);

		public List<ItemInstance> getIts() {
			return its;
		}

		public void setIts(List<ItemInstance> its) {
			this.its = its;
		}

	}
	private static CronTrigger createCronTrigger(String express) throws ParseException {
		CronTrigger trigger0 = new CronTrigger();
//		trigger0.setName("myTrigger" + jobName);
		trigger0.setGroup(Scheduler.DEFAULT_GROUP);
//		trigger0.setJobName(jobName);
		trigger0.setJobGroup(Scheduler.DEFAULT_GROUP);
		trigger0.setCronExpression(express);
		return trigger0;
	}
	public static void main_Tree(String[] args) {
			TreeMap<Integer, String> treeMap = Maps.newTreeMap();
			treeMap.put(1, "");
			treeMap.put(100, "");
			treeMap.put(5, "");
			treeMap.put(6, "");
			treeMap.put(3, "");
			NavigableMap<Integer, String> descendingMap = treeMap.descendingMap();
			NavigableMap<Integer, String> headMap = treeMap.headMap(5,false);
			NavigableMap<Integer, String> tailMap = treeMap.tailMap(5, true);
			System.out.println(headMap);
			System.out.println(tailMap);
			System.out.println(descendingMap);
			
	}
	public static void main_frag(String[] args) {
//		long fragmentInSeconds = DateUtils.getFragmentInSeconds(new Date(),Calendar.MONTH);
//		System.out.println(fragmentInSeconds);
		Date truncate1 = DateUtils.truncate(new Date(), Calendar.YEAR);
		Date truncate2 = DateUtils.ceiling(new Date(), Calendar.YEAR);
		long fragmentInSeconds1 = DateUtils.getFragmentInSeconds(truncate1,Calendar.MONTH);
		long fragmentInSeconds2 = DateUtils.getFragmentInSeconds(truncate2,Calendar.MONTH);
		System.out.println(fragmentInSeconds1);
		System.out.println(fragmentInSeconds2);
	}
	
	public static void main_sql(String[] args) {
		for (int i = 0; i < 10; i++) {
			String userId = "haha@"+i+"."+i;
			String ch = "99901";
			String net = "1";
			String beforeGuid = DefaultPlayer.genPlayerGuid(userId,"", net);
			String afterGuid = DefaultPlayer.genPlayerGuid(userId,ch, net);
			String delSqlString = "delete from role where guid = '"+afterGuid+"';";
			String delAllkeys = "delete from allkeys where guid = '"+afterGuid+"';";
			String sqlString =  "UPDATE allkeys SET guid = '"+afterGuid+"' , val=REPLACE(val, '"+beforeGuid+"', '"+afterGuid+"') where guid = '"+beforeGuid+"';";
			String sql1 = "UPDATE role SET guid = '"+afterGuid+"'  where userId = '"+userId+"' and channel='"+ch+"';";
			//System.out.println(delSqlString);
			//System.out.println(delAllkeys);
			System.out.println(sqlString);
			System.out.println(sql1);
		}
		
		
	}
	static int starLv = 0;
	static int starPoint = 0;
	public static void addStarPoint(int count) {
		int quality = 1;
		int upLv = starLv + 1;
		int  newPoint = starPoint + count;
		QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(quality);
		int length = qualityRef.getUpVals().length;
		//已经达到最高等级
		if(upLv >= 2*length) {
			return;
		}
		int idx = upLv - 1;
		if(upLv > length) {
			qualityRef = AllGameConfig.getInstance().qref.getQualityRef(quality+1);
			//已经最高了
			if(qualityRef == null) {
				return;
			}
			idx -= length;
		}
		int needPoint = qualityRef.getUpVals()[idx];
		if(newPoint >= needPoint) {
			starLv = upLv;
			int leftPoint = newPoint - needPoint;
			System.out.println("left point =" + leftPoint);
			starPoint = 0;
			addStarPoint(leftPoint);
		}else {
			starPoint =  newPoint;
		}
	}
	
	public static int getAllStarPoint(){
		int quality = 2;
		QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(quality);
		int tmpStarLv = starLv;
		int length = qualityRef.getUpVals().length;
		int points = starPoint;
		for (int i = 0; i < length; i++) {
			points+= qualityRef.getPoints()[i];
			tmpStarLv--;
			if(tmpStarLv < 0) {
				return points;
			}
		}
		qualityRef = AllGameConfig.getInstance().qref.getQualityRef(quality+1);
		if(qualityRef == null) {
			return points;
		}
		for (int i = 0; i < length; i++) {
			points+= qualityRef.getPoints()[i];
			tmpStarLv--;
			if(tmpStarLv < 0) {
				return points;
			}
		}
		return points;
		
	}
	
	public static void addStarPoint1(int count) {
		int newPoint = starPoint + count;
		int quality = 4;
		int length = AllGameConfig.getInstance().qref.getQualityStarUpLength();
		int upLv = starLv +1;
		int delta = upLv / length;
		int idx = upLv % length;
		QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(quality+delta);
		if(qualityRef == null) {
			return;
		}
		int needPoint = qualityRef.getUpVals()[idx];
		if (newPoint >= needPoint) {
			starLv = upLv;
			starPoint = 0;
			addStarPoint1(newPoint - needPoint);
		} else {
			starPoint = newPoint;
		}
	}
	public static String parseItemString(int[] counts, Integer itemId, int i) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("<").append(itemId).append(";").append(counts[i]).append(">");
		return sBuilder.toString();
	}
	
	public static int getWholeStarPoint2() {
		int baseQuality = 4;
		int qlv = 0;
		int currQuality = 4;
		int starPoint = 0;
		QualityRef qualityRef = AllGameConfig.getInstance().qref.getQualityRef(baseQuality);
		int basePoint = qualityRef.getUpVals()[0];
		int qLvBasePoint = qlv * basePoint;
		if(baseQuality != currQuality) {
			qualityRef = AllGameConfig.getInstance().qref.getQualityRef(currQuality);
		}
		return starPoint + qualityRef.getPoints()[starLv % 5] + qLvBasePoint;
	}
	public static void main(String[] args) {
		for (int i = 0; i < 30; i++) {
			System.out.println(1 << i);
		}
		
	}
	public static Guid theGuid = Guid.SHA1("ictWIr7/+h6q/3GYEsM3GMyABWoServerGlobalProps");
	public static void main_act(String[] args) throws ParseException {
		String rewards = "10;50;100;500;1000;5000;999999,50;40;35;25;20;15;10";
		int itemId = 9001;
		String[] splitStr = rewards.split(",");
		int[] values = StringHelper.getIntList(splitStr[0].split(";"));
		int[] counts = StringHelper.getIntList(splitStr[1].split(";"));
		TreeMap<String, Long> map = Maps.newTreeMap();
		for (long i = 0; i < 999999; i++) {
			map.put(i+"", i);
		}
		int i = 0;
		for (Entry<String, Long> entry : map.entrySet()) {
			String rewardItemString = null;
			String name = entry.getKey();
			Long hurtVal = entry.getValue();
			int j = 0;
			String itemNUmberString = "";
			for (int val : values) {
				if (i + 1 <= val) {
					rewardItemString = parseItemString(counts, itemId, j);
					itemNUmberString += counts[j];
					break;
				}
				j++;
			}
			
			System.out.println("rewardItemString" + rewardItemString);

			i++;
			if (i >= values[values.length - 1]) {
				break;
			}
		}

	}
	public static long getTriggerSeconds(Date nowDate, String showTime) throws ParseException {
		CronTrigger trigger = createCronTrigger(showTime);
		Date showDate = trigger.getFireTimeAfter(nowDate);
		System.out.println(TimeUtil.formatDate(showDate));
		long time = DateUtils.getFragmentInSeconds(showDate, Calendar.MONTH);
		return time;
	}
	
	public static void main_1(String[] args) {
		long startTime = System.nanoTime();
		List<String> list  = Lists.newArrayList();
		for (int i = 0; i < 1000000; i++) {
			list.add(i+"");
		}
		Collections.shuffle(list);
		
		for (int i = 0; i < 10; i++) {
			list.get(i);
		}
		long time = System.nanoTime() - startTime;
		System.out.println("onLogin cost time {} ms.."+ time / (1000 * 1000));
		
		startTime = System.nanoTime();
		Set<Integer> luckyIdx = Sets.newHashSet();
		List<String> list2 = Lists.newArrayList(list);
		for (int i = 0; i < list.size(); i++) {
			luckyIdx.add(Rnd.get(list.size()));
			if(luckyIdx.size() >= 10) {
				break;
			}
		}
		for (Integer i : luckyIdx) {
			String string = list2.get(i);
		}
		time = System.nanoTime() - startTime;
		System.out.println("onLogin cost time {} ms.."+ time / (1000 * 1000));
		

	}
	public static void main_Circular(String[] args) {
		CircularArrayList<Integer> c = new CircularArrayList<Integer>();
		c.add(1);
		c.add(2);
		c.add(3);
		c.add(4);
		c.add(5);
		int idx = 3;
		for (int i = idx -1; i >= idx -5; i--) {
			System.out.println(c.get(i));
		}
	}
//	 public static void main_sortByValue(String[] args) {
//
//	        HashMap<String,Double> map = new HashMap<String,Double>();
//	        ValueComparator bvc =  new ValueComparator(map);
//	        TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
//
//	        map.put("A",99.5);
//	        map.put("B",67.4);
//	        map.put("C",67.4);
//	        map.put("D",67.3);
//
//	        System.out.println("unsorted map: "+map);
//
//	        sorted_map.putAll(map);
//	        System.out.println("results: "+sorted_map);
//	    }

	public static void main111(String[] args) {
		AtomicLongMap<Integer> map = AtomicLongMap.create();
		map.put(1, 10);
		map.put(3, 30);
		map.put(2, 20);
		System.out.println(map);
	}

	public static void main_30(String[] args) throws Exception {
		int a = 10000 - (int)(((10000 - 9700)*1.5f));
	}

	public static void main_skill(String[] args) {
		List<Integer> i1 = Lists.newArrayList(1, 2, null, null);
		List<Integer> i2 = Lists.newArrayList(3, 4);
		List<Integer> i3 = Lists.newArrayList(5, 6);
		List<Integer> i4 = Lists.newArrayList(7, null);
		ArrayNode arrayNode = DynamicJsonProperty.jackson.createArrayNode();
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(i1));
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(i2));
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(i3));
		arrayNode.add(DynamicJsonProperty.convertToJsonNode(i4));
		System.out.println(arrayNode);
	}

	public static void main12(String[] args) {
		Map<Integer, Integer> map = ImmutableMap.of(1, 1, 2, 2);
		JsonNode writeValueAsString = jackson.convertValue(map, new TypeReference<JsonNode>() {
		});
		System.out.println(writeValueAsString);
	}

	public static void main1(String[] args) {
		List<String> asList = Arrays.asList("1", "2", "3");
		try {
			ArrayNode writeValueAsString = jackson.convertValue(asList, new TypeReference<ArrayNode>() {
			});
			ArrayNode createArrayNode = jackson.createArrayNode();
			// System.out.println(writeValueAsString);
			json.put("test", writeValueAsString);
			System.out.println(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main2(String[] args) {
		ObjectNode obj = DynamicJsonProperty.jackson.createObjectNode();
		obj.put("p1", DynamicJsonProperty.jackson.createObjectNode().put("pp1", 1111));
		// obj.with("p1").put("ppp2",2222);
		obj.with("p1").put("ppp2", 2222);
		System.out.println(obj);
		// obj.put("player",
		// DynamicJsonProperty.jackson.createObjectNode().put("p1", 1).put("p2",
		// 22222));
		// // obj.with("player").put("id", 22222);
		// System.out.println(obj);
		// obj.removeAll();
		// System.out.println(obj);
		LinkedHashMap<Integer, Hero> heros = Maps.newLinkedHashMap();
		heros.put(1, new Hero(1001));

		// Pair<Integer, Hero> pair = new Pair<Integer,Hero>(1,heros.get(1));
		// ObjectNode convertToArrayNode =
		// DynamicJsonProperty.convertToObjectNode(new Hero(1001));
		// System.out.println(convertToArrayNode);
	}

	public static void main3(String[] args) {
		ObjectNode node = jackson.createObjectNode();
		node.put("a1", jackson.createObjectNode().put("p1", 111));
		node.put("a2", jackson.createObjectNode().put("p2", 111));
		Iterator<Entry<String, JsonNode>> fields = node.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			System.out.println(key + value);

		}
	}

	public static void main4(String[] args) {
		int[] enemySkills = { 1, 2, 3 };
		List<Integer> asList = Ints.asList(enemySkills);
		for (Integer integer : asList) {
			System.out.println(integer);
		}
	}

	public static void main5(String[] args) {
		Map<String, String> map = ImmutableMap.of("ON", "TRUE", "OFF", "FALSE");
		File file = new File("C:\\Users\\zhoulei\\Desktop\\game.jsc");
		try {
			FileInputStream fis = new FileInputStream(file);
			long l = System.currentTimeMillis();
			String md5Hex = DigestUtils.md5Hex(fis);
			fis.close();
			System.out.println((System.currentTimeMillis() - l) + "ms");
			System.out.println(md5Hex);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	public static void main7(String[] args) {
		List<ItemInstance> items = Lists.newArrayList();
		ItemInstance item1 = new ItemInstance();
		item1.setItemId(5000);
		ItemInstance item2 = new ItemInstance();
		item2.setItemId(5001);
		items.add(item1);
		items.add(item2);
		items.add(null);
		// Collections.sort(items);
		List<ItemInstance> sortedCopy = Ordering.natural().nullsLast().sortedCopy(items);
		for (ItemInstance itemInstance : sortedCopy) {
			System.out.println(itemInstance == null ? "null" : itemInstance.getItemId());
		}
		boolean ordered = Ordering.natural().nullsLast().isOrdered(sortedCopy);
		System.out.println(ordered);

		for (ItemInstance itemInstance : items) {
			System.out.println(itemInstance == null ? "null" : itemInstance.getItemId());
		}
	}

	public List<ItemInstance> charList = Lists.newArrayList();

	public List<ItemInstance> getContainerById(int cid) {
		switch (cid) {
		case ItemConstant.CON_CHAR:
			return charList;
		default:
			return null;
		}
	}

	/**
	 * 排序道具
	 * 
	 * @param cid
	 * @return
	 */
	public List<ItemInstance> sortItems(int cid) {
		List<ItemInstance> items = getContainerById(cid);
		Ordering<Comparable<ItemInstance>> order = Ordering.natural().nullsLast();
		boolean ordered = order.isOrdered(items);
		if (ordered) {
			return null;
		} else {
			List<ItemInstance> sortedCopy = order.sortedCopy(items);
			items.clear();
			// TODO 需要测试是否已经更改了源列表
			items.addAll(sortedCopy);
			return sortedCopy;
		}

	}

	public static void main10(String[] args) {
		Test1 t = new Test1();
		ItemInstance item1 = new ItemInstance();
		item1.setItemId(5000);
		ItemInstance item2 = new ItemInstance();
		item2.setItemId(5001);
		t.charList.add(item1);
		t.charList.add(item2);
		t.charList.add(null);
		List<ItemInstance> sortedCopy = t.sortItems(ItemConstant.CON_CHAR);
		for (ItemInstance itemInstance : sortedCopy) {
			System.out.println(itemInstance == null ? "null" : itemInstance.getItemId());
		}
		for (ItemInstance itemInstance : t.charList) {
			System.out.println(itemInstance == null ? "null" : itemInstance.getItemId());
		}
	}

	public static void initCd(List<Integer> cdTime, int idx) {
		if (cdTime.isEmpty() || cdTime.size() <= idx) {
			for (int i = cdTime.size(); i < idx + 1; i++) {
				cdTime.add(0);
			}
		}
		System.out.println(cdTime);
	}

	public static class B extends A {
		public LinkedHashMap<String, String> map = Maps.newLinkedHashMap();

		public LinkedHashMap<String, String> getMap() {
			return map;
		}

		public void setMap(LinkedHashMap<String, String> map) {
			this.map = map;
		}

	}

	public static class A {
		public int accessibility = 1;

		public A() {
			System.out.println();
		}

		public int getAccessibility() {
			return accessibility;
		}

		public void setAccessibility(int accessibility) {
			this.accessibility = accessibility;
		}

	}

	public static void main11(String[] args) {
		int[] a = { 1, 2 };
		List arrayToList = CollectionUtils.arrayToList(a);
		System.out.println(arrayToList);
	}

	private static class SortedSetSupplier extends CountingSupplier<TreeSet<RoleEntity>> {
		@Override
		public TreeSet<RoleEntity> getImpl() {
			return new TreeSet<RoleEntity>() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean add(RoleEntity arg0) {
					boolean isAdd = super.add(arg0);
					if (isAdd && size() > 10) {
						remove(first());
					}
					return isAdd;
				}
			};
		}

		private static final long serialVersionUID = 0;
	}

	private abstract static class CountingSupplier<E> implements Supplier<E>, Serializable {
		private static final long serialVersionUID = 1L;

		abstract E getImpl();

		@Override
		public E get() {
			return getImpl();
		}
	}

	public static void main_SortedSetMultimap(String[] args) throws Exception {
		CountingSupplier<TreeSet<RoleEntity>> factory = new SortedSetSupplier();
		Map<Integer, Collection<RoleEntity>> map = Maps.newHashMap();
		final SortedSetMultimap<Integer, RoleEntity> multimap = Multimaps.newSortedSetMultimap(map, factory);
		final SortedSetMultimap<Integer, RoleEntity> synchronizedSortedSetMultimap = Multimaps.synchronizedSortedSetMultimap(multimap);
		RoleEntity r1 = new RoleEntity();
		r1.setCharId(1);
		r1.setLastLogin(new Timestamp(System.currentTimeMillis()));
		// Thread.sleep(1000);
		RoleEntity r2 = new RoleEntity();
		r2.setCharId(2);
		r2.setLastLogin(new Timestamp(System.currentTimeMillis()));
		// Thread.sleep(1000);
		RoleEntity r3 = new RoleEntity();
		r3.setCharId(3);
		r3.setLastLogin(new Timestamp(System.currentTimeMillis()));
		long l = System.currentTimeMillis();
		Thread thread1 = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 10000; i++) {
					RoleEntity r1 = new RoleEntity();
					r1.setCharId(i);
					r1.setLastLogin(new Timestamp(System.currentTimeMillis() + i * 100));
					synchronizedSortedSetMultimap.put(1, r1);
					if (i % 10 == 0) {
						synchronizedSortedSetMultimap.remove(1, r1);
					}
				}
			}
		};
		Thread thread2 = new Thread() {
			@Override
			public void run() {
				for (int i = 11000; i < 20000; i++) {
					RoleEntity r1 = new RoleEntity();
					r1.setCharId(i);
					r1.setLastLogin(new Timestamp(System.currentTimeMillis() + i * 100));
					synchronizedSortedSetMultimap.put(1, r1);
					if (i % 10 == 0) {
						synchronizedSortedSetMultimap.remove(1, r1);
					}
				}
			}
		};

		Thread thread3 = new Thread() {
			@Override
			public void run() {
				for (int i = 21000; i < 30000; i++) {
					RoleEntity r1 = new RoleEntity();
					r1.setCharId(i);
					r1.setLastLogin(new Timestamp(System.currentTimeMillis() + i * 100));
					synchronizedSortedSetMultimap.put(1, r1);
					if (i % 10 == 0) {
						synchronizedSortedSetMultimap.remove(1, r1);
					}
				}
			}
		};
		thread1.start();
		thread2.start();
		thread3.start();
		synchronizedSortedSetMultimap.put(1, r1);
		synchronizedSortedSetMultimap.put(1, r2);
		synchronizedSortedSetMultimap.put(1, r3);
		System.out.println(synchronizedSortedSetMultimap.get(1).size());
		System.out.println(synchronizedSortedSetMultimap);
		System.out.println(System.currentTimeMillis() - l);

		Thread.sleep(1000);
	}

}
