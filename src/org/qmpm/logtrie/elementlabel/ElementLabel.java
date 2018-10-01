package org.qmpm.logtrie.elementlabel;

public abstract class ElementLabel {
	
	public abstract String customToString();
	public abstract Object getLabel();
	
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((getLabel() == null) ? 0 : getLabel().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ElementLabel))
			return false;
		
		ElementLabel other = (ElementLabel) obj;
		
		if (getLabel() == null) {
			if (other.getLabel() != null)
				return false;
		} else if (!getLabel().equals(other.getLabel()))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return customToString();
	}

}
