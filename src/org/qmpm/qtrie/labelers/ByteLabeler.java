package org.qmpm.qtrie.labelers;

import org.qmpm.qtrie.exceptions.ValueOutOfBoundsException;

public class ByteLabeler extends Labeler<Byte> {

	@Override
	protected Byte newValue(String o) throws ValueOutOfBoundsException {

		int size = Byte.MIN_VALUE + this.map.size();

		if (size < Byte.MAX_VALUE) {
			Byte val = (byte) (size + 1);
			this.map.put(o, val);
			return val;
		} else {
			throw new ValueOutOfBoundsException();
		}
	}

}
