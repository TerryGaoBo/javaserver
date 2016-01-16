package com.jelly.team;

import java.util.HashMap;
import java.util.List;

import com.dol.cdf.common.KeyWordsFilter.DFANode;
import com.google.common.collect.Lists;

/**
 * 军团名称过滤器,使用AC算法实现
 **/
public class TeamNameFilter {
	
	private List<String> keyWords;
	private DFANode dfaEntrance; // DFA入口
	private List<DFANode> allDFANodes; // 记录所有DFA节点，方便清理
	
	public TeamNameFilter(List<String> keyWords) {
		this.keyWords = keyWords;
		this.dfaEntrance = new DFANode();
		this.allDFANodes = Lists.newArrayList();
		initialize();
	}
	
	private void initialize() {
		char[] patternTextArray;
		DFANode currentDFANode;
		DFANode newDFANode;

		// 构造DFA
		for (int i = 0; i < keyWords.size(); i++) {
			if (keyWords.get(i) != null) {
				patternTextArray = keyWords.get(i).toCharArray();
				currentDFANode = dfaEntrance;
				for (int j = 0; j < patternTextArray.length; ++j) {
					// 逐点加入DFA
					if (currentDFANode.dfaTransition.containsKey(patternTextArray[j])) {
						// 如果有路径，行进
						currentDFANode = currentDFANode.dfaTransition.get(patternTextArray[j]);
					} else {
						// 新加入DFA节点
						newDFANode = new DFANode();
						newDFANode.index = i;
						currentDFANode.dfaTransition.put(patternTextArray[j], newDFANode);
						allDFANodes.add(newDFANode);
						currentDFANode = newDFANode;
					}
				}
				// 最后一个字符，设置此时的Node为可退出
				currentDFANode.canExit = true;
			}
		}
	}
	
	public void addKeyWords(String keyWords) {
	}
	
	public void removeKeyWords(String keyWords) {
	}
	
	public List<String> getMatchNames(String words) {
		List<String> matchNames = Lists.newArrayList();
		char[] input = words.toCharArray();
		DFANode currentDFANode;
		for (int i = 0; i < input.length; i++) {
			// TODO:优化，可考虑每次后移多步？
			currentDFANode = dfaEntrance;
			// 开始遍历DFA
			for (int j = i; j < input.length; j++) {
				if (currentDFANode.dfaTransition.containsKey(input[j])) {
					// 找到状态转移，可继续
					currentDFANode = currentDFANode.dfaTransition.get(input[j]);
					matchNames.add(keyWords.get(currentDFANode.index));
					for (DFANode node : currentDFANode.dfaTransition.values()) {
						matchNames.add(keyWords.get(node.index));
					}
					// 看看当前状态可退出否
					if (currentDFANode.canExit) {
						// 可退出，返回true
						break;
					}
				} else {
					// 无法继续转移，不匹配
					break;
				}
			}
		}
		return matchNames;
	}
	
	private class DFANode {
		public HashMap<Character, DFANode> dfaTransition;
		public boolean canExit;
		public int index;

		public DFANode() {
			dfaTransition = new HashMap<Character, DFANode>();
			canExit = false;
			index = -1;
		}
	}
	
	public static void main(String[] args) {
		List<String> keyWords = Lists.newArrayList(
				"Sloven", "火影村", "火焰山", "Saff", "大牧业", 
				"222222", "山火红", "绿帽子", "大保健", "保健党", "党魁之家", "日勒狗了");
		TeamNameFilter tnf = new TeamNameFilter(keyWords);
		System.out.println(tnf.getMatchNames("火"));
	}
}
