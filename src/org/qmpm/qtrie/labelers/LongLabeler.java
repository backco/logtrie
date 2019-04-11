package org.qmpm.qtrie.labelers;

import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;

public class LongLabeler extends Labeler<Long> {

	@Override
	protected Long newValue(String o) throws ValueOutOfBoundsException {

		long size = Long.MIN_VALUE + this.map.size();

		if (size < Long.MAX_VALUE) {
			Long val = (long) (size + 1);
			this.map.put(o, val);
			return val;
		} else {
			throw new ValueOutOfBoundsException();
		}
	}

}
