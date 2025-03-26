package carriers;

public class JoinResult {
    boolean result;
    int code;
    public JoinResult(boolean result, int code){
        this.result = result;
        this.code = code;
    }
    public boolean getResult(){
        return result;
    }
    public int getCode(){
        return code;
    }
    @Override
    public String toString(){
        return "JoinResult: "+result+", "+code;
    }
}

