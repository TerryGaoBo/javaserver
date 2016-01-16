package com.jelly.team;

import com.jelly.hero.Hero;

// 作为临时存储hero 和唯一id的调用
public class MyHero {
	
	public int id; // hero 在背包中的id
	public Hero hero;
	
	public MyHero(int id,Hero hero)
	{
		this.id = id;
		this.hero = hero;
	}
}
