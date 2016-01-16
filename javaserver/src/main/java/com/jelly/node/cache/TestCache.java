package com.jelly.node.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

public class TestCache {
	static LoadingCache<String, String> graphs = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.recordStats()
			.build(new CacheLoader<String, String>() {
				public String load(String key) throws Exception {
					return key.toUpperCase();
				}
			});
	
	public static void main(String[] args) {
		
		BloomFilter<Person> friends = BloomFilter.create(personFunnel, 500, 0.01);
		ImmutableList<Person> friendsList = ImmutableList.of(new Person(), new Person(), new Person());
		Person dude = new Person();
		dude.birthYear = 1;
		for(Person friend : friendsList) {
			  friends.put(friend);
			}
			// much later
			if (friends.mightContain(dude)) {
				System.out.println("XXXXXXXXXXXXXXXXXXXXXXX");
			  // the probability that dude reached this place if he isn't a friend is 1%
			  // we might, for example, start asynchronously loading things for dude while we do a more expensive exact check
			}
		CacheStats stats = graphs.stats();
		System.out.println(graphs.getUnchecked("a"));
		try {
			graphs.get("a", new Callable<String>() {
			    @Override
			    public String call() throws Exception {
			    	System.out.println("CCCCCCCCC");
			      return "a";
			    }
			  });
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		graphs.put("a", "1");
//		BiMap<String, Integer> bm = Maps.synchronizedBiMap(bimap)
		BiMap<String, Integer> bm = HashBiMap.create();
		bm.put("a", 1);
		System.out.println(bm.get("a"));
		System.out.println(bm.inverse().get(1));
	}
	
	static Funnel<Person> personFunnel = new Funnel<Person>() {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		  public void funnel(Person person, PrimitiveSink into) {
		    into
		      .putInt(person.id)
		      .putString(person.firstName, Charsets.UTF_8)
		      .putString(person.lastName, Charsets.UTF_8)
		      .putInt(person.birthYear);
		  }
		};
		
		public static class Person {
			  final int id = 1;
			  final String firstName = "a";
			  final String lastName = "b";
			  int birthYear = 1;
			}
}
