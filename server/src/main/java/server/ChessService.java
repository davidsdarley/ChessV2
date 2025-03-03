package server;
import dataaccess.DataAccess;

import javax.xml.crypto.Data;
import java.util.Collection;

public class ChessService {
    private final DataAccess data;

    public ChessService(){
        data = new DataAccess();
    }
    public ChessService(DataAccess dataAccess){
        data = dataAccess;
    }
}
