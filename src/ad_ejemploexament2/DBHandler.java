/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad_ejemploexament2;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;

/**
 *
 * @author oracle
 */
public class DBHandler {
    
    ODB odb=null;
    private static MongoClient cliente;
    private static MongoDatabase db;
    private static MongoCollection<Document> coleccion;    
    
    public void conectarMongo(){
        //Conectar a mongo
        cliente=new MongoClient("localhost",27017);
        //cargar una db
        db=cliente.getDatabase("resultado");
        //cargar una colleccion de la db cargada anteriormente
        coleccion=db.getCollection("xerado");
        //desactivamos los mensajes de log de mongo
        Logger x=Logger.getLogger("org.mongodb.driver");
        x.setLevel(Level.OFF);
    }
    
    public void desconectarMongo(){
        cliente.close();
    }
    
    public void conectarNeodatis(){
        odb = ODBFactory.open("vinho");
    }
    
    public void desconectarNeodatis(){
        odb.close();
    }
    
}
