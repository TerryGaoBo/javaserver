package com.dol.cdf.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 关键字过滤类 使用AC算法
 * 
 */
public class KeyWordsFilter {

	private DFANode dfaEntrance; // DFA入口
	private ArrayList<DFANode> dfaNodes; // 记录所有DFA节点，方便清理
	private HashSet<Character> ignoreChars; // 要忽略的字符

	public KeyWordsFilter(String[] keyWords, char[] ignores, char substitute) {

		// 加入DFA入口节点
		dfaEntrance = new DFANode();

		// 初始化 dfaNodes
		dfaNodes = new ArrayList<DFANode>();

		// 初始化ignoreChars
		ignoreChars = new HashSet<Character>();
		
		this.initialize(keyWords, ignores, substitute);
	}

	/**
	 * 初始化时先调用此函数清理
	 */
	private void clear() {
		// 清除DFA所有节点
		for (DFANode node : dfaNodes) {
			node.dfaTransition.clear();
		}

		dfaNodes.clear();

		// 清除HashSet
		ignoreChars.clear();
	}

	/**
	 * 初始化，构造状态机
	 * 
	 * @param keyWords
	 *            关键字列表
	 * @param ignore
	 *            处理时被忽略的字符，通常为无意义的字符
	 * @param substitute
	 *            替换后的关键字符(例如*)
	 * @return 是否初始化成功，成功true，否则false
	 */
	public boolean initialize(String[] keyWords, char[] ignore, char substitute) {
		char[] patternTextArray;
		DFANode currentDFANode;
		DFANode newDFANode;

		clear();

		for (int i = 0; i < ignore.length; i++) {
			ignoreChars.add(ignore[i]);
		}

		// 构造DFA
		for (int s = 0; s < keyWords.length; s++) {
			if (keyWords[s] != null) {
				patternTextArray = keyWords[s].toCharArray();
				currentDFANode = dfaEntrance;
				for (int i = 0; i < patternTextArray.length; i++) {
					// 如果过滤关键字中含有要忽略的字符，那么忽略
					if (!ignoreChars.contains(patternTextArray[i])) {
						// 逐点加入DFA
						if (currentDFANode.dfaTransition.containsKey(patternTextArray[i])) {
							// 如果有路径，行进
							currentDFANode = currentDFANode.dfaTransition.get(patternTextArray[i]);
						} else {
							// 新加入DFA节点
							newDFANode = new DFANode();
							currentDFANode.dfaTransition.put(patternTextArray[i], newDFANode);
							dfaNodes.add(newDFANode);
							currentDFANode = newDFANode;
						}
					}
				}
				// 最后一个字符，设置此时的Node为可退出
				currentDFANode.canExit = true;
			}
		}

		return true;
	}

	/**
	 * 过滤关键词
	 * 
	 * @param s
	 *            要被处理的字符串
	 * @return 处理完毕的字符串
	 */
	public String filt(String s) {
		char[] input = s.toCharArray();
		char[] result = s.toCharArray();
		int killto = -1;
		int i, j;
		DFANode currentDFANode;

		for (i = 0; i < input.length; i++) {
			// TODO:优化，可考虑每次后移多步？
			killto = -1;
			currentDFANode = dfaEntrance;

			// 开始遍历DFA
			for (j = i; j < input.length; j++) {
				// 如果过滤关键字中含有要忽略的字符，那么忽略
				if (!ignoreChars.contains(input[j])) {
					if (currentDFANode.dfaTransition.containsKey(input[j])) {
						// 找到状态转移，可继续
						currentDFANode = currentDFANode.dfaTransition.get(input[j]);
						// 看看当前状态可退出否
						if (currentDFANode.canExit) {
							// 可退出，记录，可以替换到这里
							// 此记录可能增长，我们要替换最长的匹配串
							killto = j;
						}
					} else {
						// 无法继续转移，不匹配
						break;
					}
				}
			}

			if (killto != -1) {
				// 有匹配的，替换掉
				for (j = i; j <= killto; j++) {
					// 如果不是忽略的
					if (!ignoreChars.contains(input[j])) {
						result[j] = '*';
					}
				}
			}

		}

		return String.valueOf(result);
	}

	/**
	 * 检测输入的消息是否有关键字
	 * 
	 * @param inputMsg
	 *            要检测的消息
	 * @return 若有返回true，否则false
	 */
	public boolean contain(String inputMsg) {
		if (inputMsg == null) {
			return false;
		}
		char[] input = inputMsg.toCharArray();
		int i, j;
		DFANode currentDFANode;

		for (i = 0; i < input.length; i++) {
			// TODO:优化，可考虑每次后移多步？
			currentDFANode = dfaEntrance;

			// 开始遍历DFA
			for (j = i; j < input.length; j++) {
				// 如果过滤关键字中含有要忽略的字符，那么忽略
				if (!ignoreChars.contains(input[j])) {
					if (currentDFANode.dfaTransition.containsKey(input[j])) {
						// 找到状态转移，可继续
						currentDFANode = currentDFANode.dfaTransition.get(input[j]);
						// 看看当前状态可退出否
						if (currentDFANode.canExit) {
							// 可退出，返回true
							return true;
						}
					} else {
						// 无法继续转移，不匹配
						break;
					}
				}
			}

		}

		return false;
	}

	public class DFANode {
		public HashMap<Character, DFANode> dfaTransition;
		public boolean canExit;

		public DFANode() {
			dfaTransition = new HashMap<Character, DFANode>();
			canExit = false;
		}
	}

//	public static void main(String[] args) {
//		try {
//			KeyWordsFilter filter = KeyWordsFilter.getInstance();
//			boolean b = filter.contain("肏");
//			System.out.println(filter.filt("肏"));
//		} catch (Exception e) {
//		}
//	}
}
