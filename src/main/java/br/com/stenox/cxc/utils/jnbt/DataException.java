package br.com.stenox.cxc.utils.jnbt;

public class DataException extends Exception
{
    private static final long serialVersionUID = 5806521052111023788L;
    
    public DataException(final String msg) {
        super(msg);
    }
    
    public DataException() {
    }
}
