package com.jelly.buff;

import com.jelly.player.IFighter;

public abstract class BaseBuff implements IBuff {
	
    private IFighter owner;
    
	@Override
	public IFighter getOwner() {
		return owner;
	}

	@Override
	public void setOwner(IFighter owner) {
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		BaseBuff other = (BaseBuff) obj;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}


}
