package com.dol.cdf.common.base;

/**
 * 检测输入是否被接受
 * 
 * @author zhoulei
 * 
 * @param <T>
 */
public interface IPredicate<T> {
	boolean apply(T input);
}
