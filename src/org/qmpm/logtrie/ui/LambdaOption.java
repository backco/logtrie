/*
 * 	LogTrie - an efficient data structure and CLI for XES event logs and other sequential data
 * 
 * 	Author: Christoffer Olling Back	<www.christofferback.com>
 * 
 * 	Copyright (C) 2018 University of Copenhagen 
 * 
 *	This file is part of LogTrie.
 *
 *	LogTrie is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	LogTrie is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with LogTrie.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.qmpm.logtrie.ui;

import org.apache.commons.cli.Option;

public class LambdaOption extends Option {

	private static final long serialVersionUID = 1L;
	protected Operation operation;
	
	public interface Operation {
		void execute();
	}
	
	public void doOperation() {
		operation.execute();
	}
	
	public LambdaOption(String opt, String desc){
		super(opt, desc);
		operation =	 () -> {return;};
	}

	public LambdaOption(String opt, String desc, Operation oper){
		super(opt, desc);
		operation = oper;
	}
	
	public LambdaOption(String opt, boolean hasArg, String desc){
		super(opt, hasArg, desc);
		operation =	 () -> {return;};
	}
	
	public LambdaOption(String opt, boolean hasArg, String desc, Operation oper){
		super(opt, hasArg, desc);
		operation = oper;
	}
	
	public LambdaOption(String opt, String longOpt, boolean hasArg, String desc){
		super(opt, longOpt, hasArg, desc);
		operation =	 () -> {return;};
	}
	
	public LambdaOption(String opt, String longOpt, boolean hasArg, String desc, Operation oper){
		super(opt, longOpt, hasArg, desc);
		operation = oper;
	}
	
	public void setOperation(Operation oper) {
		operation = oper;
	}
}
