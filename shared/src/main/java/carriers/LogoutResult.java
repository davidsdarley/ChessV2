package carriers;

public class LogoutResult {
    boolean result;
    public LogoutResult(boolean result){
        this.result = result;
    }
    public boolean getResult(){
        return result;
    }
    @Override
    public String toString(){
        return "Logout result: " + result;
    }
}
