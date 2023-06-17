package br.com.stenox.cxc.login.check;

public abstract interface Verify {

	public abstract boolean verify(String playerName);

	public abstract boolean getResult();

}
