package com.dol.cdf.common.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.bean.Accessory;
import com.dol.cdf.common.bean.Item;
import com.dol.cdf.common.bean.Material;
import com.dol.cdf.common.bean.NpcTalk;
import com.dol.cdf.common.bean.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

public class NpcTalkConfigManager extends BaseConfigLoadManager {

	private static final String JSON_FILE_FORM = "NpcTalk.json";

	@Override
	public void loadConfig() {
		
		//只有是测试环境才检测
		if (AllGameConfig.getInstance().env != RuntimeEnv.OTHER) {
			return;
		}
		
		List<NpcTalk> list = readConfigFile(JSON_FILE_FORM, new TypeReference<List<NpcTalk>>() {
		});

		logger.info("check npcTalk..");
		for (NpcTalk npcTalk : list) {
			if (npcTalk.getUrl() == null) {
				continue;
			}
			String d = AllGameConfig.ImagePath + "idialogue/" + npcTalk.getUrl() + "_d.png";
			checkFileIsExist(d);
			String n = AllGameConfig.ImagePath + "roleName/" + npcTalk.getUrl() + "_n.png";
			checkFileIsExist(n);
		}

		logger.info("check role..");
		Collection<Role> allRoles = AllGameConfig.getInstance().characterManager.getRoleMap().values();
		for (Role role : allRoles) {
			if (role.getXmlfile() == null) {
				continue;
			}
			String h = AllGameConfig.ImagePath + "iroleHead/" + role.getXmlfile() + "_h.png";
			checkFileIsExist(h);
			String d = AllGameConfig.ImagePath + "idialogue/" + role.getXmlfile() + "_d.png";
			checkFileIsExist(d);
			String n = AllGameConfig.ImagePath + "roleName/" + role.getXmlfile().trim() + "_n.png";
			checkFileIsExist(n);
			String g = AllGameConfig.ImagePath + "igameFace/" + role.getXmlfile() + "_g.png";
			checkFileIsExist(g);

		}
		
		//checkFileIsUsed();
		checkItemIsExsist();
	}

	private void checkItemIsExsist() {
		logger.info("check item..");
		Collection<Accessory> values = AllGameConfig.getInstance().items.getAccMap().values();
		for (Accessory accessory : values) {
			itemIsExsist(accessory.getIcon());
		}
		Collection<Item> values2 = AllGameConfig.getInstance().items.getItemMap().values();
		for (Item item : values2) {
			if(item.getCategory() != 13) {
				itemIsExsist(item.getIcon());
			}
		}
		Collection<Material> values3 = AllGameConfig.getInstance().items.getMatMap().values();
		for (Material item : values3) {
			itemIsExsist(item.getIcon());
		}
	}	
	
	private void itemIsExsist(String icon) {
		String path = AllGameConfig.ImagePath + "iitem/" + icon + ".png";
		checkFileIsExist(path);;
	}
	private void checkFileIsUsed() {
		File dir = new File(AllGameConfig.ImagePath);
		File js = new File(AllGameConfig.ImagePath+"../js/");
		File ccb = new File(AllGameConfig.ImagePath+"../ccb/");
		List<String> fns = Lists.newArrayList();
		if(dir.isDirectory()){
			for (File file : dir.listFiles()) {
				if(file.isDirectory()){
					for (File f : file.listFiles()) {
						if(!f.isDirectory()){
							System.out.println(f.getName());
							String fn = f.getName();
							fns.add(fn);
						}
					}
				}
			}
		}
		
		for (String fn : fns) {
			boolean has = false;
			for (File jf : js.listFiles(createFilter("idea"))) {
				if(jf.isDirectory()){
					for (File jff : jf.listFiles()) {
						boolean checkHasFName = checkHasFName(fn, jff);
						if (checkHasFName) {
							has = true;
							break;
						}
					}
					if (has) {
						break;
					}
				}else {
					boolean checkHasFName = checkHasFName(fn, jf);
					if (checkHasFName) {
						has = true;
						break;
					}
				}
			}
			if (!has) {
				for (File cf : ccb.listFiles(createFilter("idea"))) {
					if(cf.isDirectory()){
						for (File cff : cf.listFiles()) {
							boolean checkHasFName = checkHasFName(fn, cff);
							if (checkHasFName) {
								has = true;
								break;
							}
						}
						if (has) {
							break;
						}
					}else {
						boolean checkHasFName = checkHasFName(fn, cf);
						if (checkHasFName) {
							has = true;
							break;
						}
					}
				}
			}
			
			if (!has) {
				logger.error("file:{} not use.",fn);
			}
			
		}
	}
	
	public static FileFilter createFilter(final String endfix) {
		FileFilter fileFilter = new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (!pathname.getName().endsWith(endfix)) {
					return true;
				}
				return false;
			}
		};
		return fileFilter;
	}

	private boolean checkHasFName(String fn, File jf) {
		try {
			List<String> readLines = IOUtils.readLines(new FileInputStream(jf));
			for (String string : readLines) {
				if (string.contains(fn)) {
					return true;
				};
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	void checkFileIsExist(String path) {
		File file = new File(path);
		if (!file.exists()) {
			logger.error("file not exist. PATH:"+ path);
		}
	}

}