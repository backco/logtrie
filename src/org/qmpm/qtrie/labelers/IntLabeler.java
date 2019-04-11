package org.qmpm.qtrie.labelers;

import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;

public class IntLabeler extends Labeler<Integer> {

	@Override
	protected Integer newValue(String o) throws ValueOutOfBoundsException {

		int size = Integer.MIN_VALUE + this.map.size();

		if (size < Integer.MAX_VALUE) {
			Integer val = size + 1;
			this.map.put(o, val);
			return val;
		} else {
			throw new ValueOutOfBoundsException();
		}
	}

}
