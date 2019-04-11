package org.qmpm.qtrie.labelers;

import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;

public class ShortLabeler extends Labeler<Short> {

	@Override
	protected Short newValue(String o) throws ValueOutOfBoundsException {

		int size = Short.MIN_VALUE + this.map.size();

		if (size < Short.MAX_VALUE) {
			Short val = (short) (size + 1);
			this.map.put(o, val);
			return val;
		} else {
			throw new ValueOutOfBoundsException();
		}
	}

}
