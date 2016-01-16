package com.dol.cdf.common.gamefunction.gfi;

import com.dol.cdf.common.gamefunction.parameter.IGameFunctionParameter;

/**
 * 
 * @author zhoulei
 *
 */
public class BaseGameFunctionInterface implements IGameFunctionInterface{

	private final int id;

	@Override
	public int getId() {
		return id;
	}

	public BaseGameFunctionInterface(int id) {
		this.id = id;
	}
	
	protected IGameFunctionParameter parameter;

	@Override
	public IGameFunctionParameter getParameter() {
		return parameter;
	}

	public void setParameter(IGameFunctionParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseGameFunctionInterface other = (BaseGameFunctionInterface) obj;
		if (id != other.id)
			return false;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		return true;
	}

	
	

}
