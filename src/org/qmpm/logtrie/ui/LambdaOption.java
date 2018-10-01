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
